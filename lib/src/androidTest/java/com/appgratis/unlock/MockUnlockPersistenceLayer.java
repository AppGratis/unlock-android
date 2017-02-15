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

import android.support.annotation.NonNull;

import com.appgratis.unlock.internal.RedeemedFeature;
import com.appgratis.unlock.model.Feature;
import com.appgratis.unlock.persistence.UnlockPersistenceLayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Dummy persistor that only "persists" the data in RAM
 */
public class MockUnlockPersistenceLayer implements UnlockPersistenceLayer {

    private final List<RedeemedFeature> features = new ArrayList<>();

    private List<OfferOccurrence> consumedOffersOccurrences = new LinkedList<>();

    private Set<String> consumedOffers = new HashSet<>();

    @NonNull
    public void saveOffer(@NonNull String offerToken) {
        consumedOffers.add(offerToken);
    }

    @Override
    public void saveRepeatingOffer(@NonNull String offerToken, @NonNull Date unlockDate) {
        if (!isRepeatingOfferConsumedForDay(offerToken, unlockDate)) {
            consumedOffersOccurrences.add(new OfferOccurrence(offerToken, unlockDate));
        }
    }

    @Override
    public void saveFeature(@NonNull Feature feature) {
        synchronized (features) {
            features.add(new RedeemedFeature(feature, new Date()));
        }
    }

    @NonNull
    @Override
    public List<RedeemedFeature> getAvailableFeatures() {
        return new ArrayList<>(features);
    }

    @Override
    public boolean isOfferConsumed(@NonNull String offerToken) {
        return consumedOffers.contains(offerToken);
    }

    @Override
    public boolean isRepeatingOfferConsumedForDay(@NonNull String offerToken, @NonNull Date day) {
        return consumedOffersOccurrences.contains(new OfferOccurrence(offerToken, day));
    }

    @Override
    public synchronized void deleteFeatures(List<Feature> featuresToDelete) {
        synchronized (features) {
            List<RedeemedFeature> redeemedFeaturesCopy = new LinkedList<>(features);
            for (Feature featureToDelete : featuresToDelete) {
                for (RedeemedFeature redeemedFeature : redeemedFeaturesCopy) {
                    if (redeemedFeature.feature.name.equalsIgnoreCase(featureToDelete.name)) {
                        features.remove(redeemedFeature);
                    }
                }
            }
        }
    }

    @Override
    public void wipe() {
        features.clear();
        consumedOffersOccurrences.clear();
        consumedOffers.clear();
    }

    private class OfferOccurrence {
        private String offerToken;
        private Date redeemDay;

        public OfferOccurrence(String offerToken, Date redeemDate) {
            setOfferToken(offerToken);
            setRedeemDay(redeemDate);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OfferOccurrence) {
                final OfferOccurrence comparedOffer = (OfferOccurrence) obj;
                return comparedOffer.getOfferToken().equalsIgnoreCase(offerToken) && comparedOffer.getRedeemDay().compareTo(redeemDay) == 0;
            } else {
                return false;
            }
        }

        public String getOfferToken() {
            return offerToken;
        }

        public void setOfferToken(String offerToken) {
            this.offerToken = offerToken;
        }

        public Date getRedeemDay() {
            return redeemDay;
        }

        public void setRedeemDay(Date redeemDay) {
            this.redeemDay = getDateWithoutTime(redeemDay);
        }

        private Date getDateWithoutTime(Date date) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        }
    }
}
