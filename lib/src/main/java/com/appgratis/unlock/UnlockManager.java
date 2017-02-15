/*
 * MIT License
 *
 * Copyright (c) 2016 iMediapp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.appgratis.unlock;

import android.content.Context;
import android.support.annotation.NonNull;

import com.appgratis.unlock.internal.DatasourceParser;
import com.appgratis.unlock.internal.DatasourceParsingException;
import com.appgratis.unlock.internal.DateProvider;
import com.appgratis.unlock.internal.DateProviderImpl;
import com.appgratis.unlock.internal.RedeemedFeature;
import com.appgratis.unlock.model.Feature;
import com.appgratis.unlock.model.Offer;
import com.appgratis.unlock.persistence.sql.SQLUnlockPersistenceLayer;
import com.appgratis.unlock.persistence.UnlockPersistenceLayer;
import com.appgratis.unlock.source.OfferSource;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * UnlockManager is the AppGratis Unlock Manager: your main entry point to the unlock library.
 * <p/>
 * It manages:
 * - Loading new rules from a file or from your APK's raw assets
 * - Getting available features
 * - Getting resources you should consume
 * - Consuming resources
 * - Wipe (clearing redeemed features)
 *
 * This class is not thread safe.
 */
public class UnlockManager {

    @NonNull
    protected List<Offer> offers;

    @NonNull
    private UnlockPersistenceLayer persistenceLayer;

    @NonNull
    private DateProvider dateProvider;

    /**
     * Instantiate a new UnlockManager with the wanted offer source, persistence layer and date provider.
     * Meant for testing.
     * @param offerSource Offer source implementation to use. Use {@link com.appgratis.unlock.source.BuiltinOfferSource}, {@link com.appgratis.unlock.source.StringOfferSource} or roll your own implementation.
     * @param persistenceLayer Persistence layer implementation to use
     */
    protected UnlockManager(@NonNull OfferSource offerSource, @NonNull UnlockPersistenceLayer persistenceLayer, @NonNull DateProvider dateProvider) throws DatasourceParsingException {
        this.persistenceLayer = persistenceLayer;
        init(offerSource, dateProvider);
    }

    //region Public APIs

    /**
     * Instantiate a new UnlockManager with the wanted offer source.
     * @param offerSource Offer source implementation to use. Use {@link com.appgratis.unlock.source.BuiltinOfferSource}, {@link com.appgratis.unlock.source.StringOfferSource} or roll your own implementation.
     * @param context Your application context.
     */
    public UnlockManager(@NonNull OfferSource offerSource, @NonNull Context context) throws DatasourceParsingException {
        this.persistenceLayer = new SQLUnlockPersistenceLayer(context);
        init(offerSource, new DateProviderImpl(context.getApplicationContext()));
    }

    /**
     * Returns the list of features the user should have access to.
     * Only features you've previously marked as consumed will be available in this list.
     * @return List of features the user has access to
     */
    @NonNull
    public List<Feature> getFeatures() {
        final Date currentDate = dateProvider.getCurrentDate();

        List<Feature> expiredFeatures = new LinkedList<>();
        List<Feature> features = new LinkedList<>();
        final List<RedeemedFeature> availableFeatures = persistenceLayer.getAvailableFeatures();
        for (RedeemedFeature availableFeature : availableFeatures) {
            final Feature feature = availableFeature.feature;
            feature.updateRemainingLifetime(currentDate, availableFeature.redeemDate);
            if (!feature.isExpired()) {
                features.add(availableFeature.feature);
            } else {
                expiredFeatures.add(availableFeature.feature);
            }
        }

        persistenceLayer.deleteFeatures(expiredFeatures);
        return features;
    }

    /**
     * Returns the pending offers. <br/>
     * A pending offer is an offer that's waiting to be consumed using {@link #consumeOffer(Offer)}: UnlockManager will not
     * remove an offer from this list until you consume it, or until it is no longer available.<br/>
     * Once you've given the features and/or resources to the user, you should consume it so UnlockManager starts remembering the unlocked features even if the {@link OfferSource} changes.<br/>
     * Since resources are consumables, it is your responsibility to remember how much the user has of the resource kind.<br/>
     *
     * Offers can also come with a message that you should show using your own means to the user.
     * @return List of pending offers. You should consume them as soon as possible.
     */
    @NonNull
    public List<Offer> getPendingOffers() {
        List<Offer> retVal = new LinkedList<>();

        for (Offer offer : offers) {
            if (offer.isRepeating()) {
                if (persistenceLayer.isRepeatingOfferConsumedForDay(offer.token, dateProvider.getCurrentDate())) {
                    continue;
                }
            } else {
                if (persistenceLayer.isOfferConsumed(offer.token)) {
                    continue;
                }
            }

            if (offer.conditions.isSatisfied(dateProvider)) {
                //TODO make a deep copy of the offer
                retVal.add(offer);
            }
        }

        return retVal;
    }

    /**
     * Consume an: the UnlockManager will stop delivering it when you ask for pending offers.
     * You should ONLY consume an offer when you've given every feature and resource a
     * Consuming an offer with a feature that the user already has will make the feature with the highest lifetime win.
     * If you're consuming a recurring offer, it will only be consumed for the current occurrence.
     * @param offer Offer to consume
     */
    public synchronized void consumeOffer(@NonNull Offer offer) {
        if (offer.isRepeating()) {
            persistenceLayer.saveRepeatingOffer(offer.token, dateProvider.getCurrentDate());
        } else {
            persistenceLayer.saveOffer(offer.token);
        }

        // Only save a feature if it won't be overwrite a lifetime unlocked one with a limited TTL one
        outer:
        for (Feature feature : offer.features) {
            if (feature.initialLifetime != Feature.UNLIMITED_LIFETIME) {
                List<RedeemedFeature> availableFeatures = persistenceLayer.getAvailableFeatures();
                for (RedeemedFeature availableFeature : availableFeatures) {
                    if (availableFeature.feature.initialLifetime == Feature.UNLIMITED_LIFETIME &&
                            feature.name.equalsIgnoreCase(availableFeature.feature.name)) {
                        // Don't overwrite it
                        continue outer;
                    }
                }
            }
            persistenceLayer.saveFeature(feature);
        }

    }

    /**
     * Wipes all data the UnlockManager saved: it will be revered in the same state if would have been if it is the first time you're using it.
     * This includes redeemed features, both current and old.
     */
    public void wipeData() {
        persistenceLayer.wipe();
    }

    //endregion

    private void init(@NonNull OfferSource offerSource, @NonNull DateProvider dateProvider) throws DatasourceParsingException {
        offers = new DatasourceParser(offerSource.getOffersJSON()).parse();
        this.dateProvider = dateProvider;
    }
}
