package com.appgratis.unlock.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.appgratis.unlock.UnlockManager;
import com.appgratis.unlock.internal.DatasourceParsingException;
import com.appgratis.unlock.model.Feature;
import com.appgratis.unlock.model.Offer;
import com.appgratis.unlock.model.Resource;
import com.appgratis.unlock.source.BuiltinOfferSource;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SampleUnlockManager {
    private UnlockManager manager;

    public SampleUnlockManager(@NonNull Context context) throws IOException, DatasourceParsingException {
        context = context.getApplicationContext();
        manager = new UnlockManager(new BuiltinOfferSource(context, R.raw.offers), context);
    }

    @NonNull
    public List<Offer> getPendingOffers() {
        return manager.getPendingOffers();
    }

    @NonNull
    public List<Feature> getFeatures() {
        return manager.getFeatures();
    }

    public void consume(@NonNull Context context, @NonNull Offer offer) {
        if (!TextUtils.isEmpty(offer.unlockMessage)) {
            if (offer.data != null && offer.data.has("nickname")) {
                String message = offer.unlockMessage;
                try {
                    message = message.replace("nickname", offer.data.getString("nickname"));
                } catch (JSONException e) {
                    Log.e(SampleUnlockManager.class.getSimpleName(), "Error while reading 'nickname' from offer additional data", e);
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }

        // Features will be remembered by the manager,
        // but we need to take care of the resources here before marking the offer as consumed
        ResourceManager resourceManager = new ResourceManager(context);
        for (Resource resource : offer.resources) {
            resourceManager.add(resource.name.toUpperCase(Locale.US), resource.quantity);
        }

        manager.consumeOffer(offer);

        //TODO: Broadcast something to tell the UI to refresh
    }
}
