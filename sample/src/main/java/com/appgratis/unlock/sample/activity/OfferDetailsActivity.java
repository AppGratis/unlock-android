package com.appgratis.unlock.sample.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.appgratis.unlock.internal.DatasourceParsingException;
import com.appgratis.unlock.model.Offer;
import com.appgratis.unlock.sample.R;
import com.appgratis.unlock.sample.manager.SampleUnlockManager;

import java.io.IOException;

public class OfferDetailsActivity extends AppCompatActivity {

    public static String EXTRA_OFFER_TOKEN = "offer_token";

    private SampleUnlockManager unlockManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        TextView tokenTV = (TextView) findViewById(R.id.tokenTV);
        TextView featuresTV = (TextView) findViewById(R.id.featuresTV);
        TextView resourcesTV = (TextView) findViewById(R.id.resourcesTV);

        unlockManager = loadUnlockManager();
        if (unlockManager == null) {
            return;
        }

        final Offer offer = loadOffer();

        if (offer == null) {
            return;
        }

        tokenTV.setText(offer.token);
        featuresTV.setText(Integer.toString(offer.features.size()));
        resourcesTV.setText(Integer.toString(offer.resources.size()));

        findViewById(R.id.redeemButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockManager.consume(OfferDetailsActivity.this, offer);
                finish();
            }
        });
    }

    private SampleUnlockManager loadUnlockManager() {
        try {
            return new SampleUnlockManager(this);
        } catch (IOException | DatasourceParsingException e) {
            Log.e(OfferDetailsActivity.class.getSimpleName(), "Error while loading the unlock manager", e);
            showOfferLoadError();
        }
        return null;
    }

    private Offer loadOffer() {
        Offer retVal = null;

        String offerToken = getIntent().getStringExtra(EXTRA_OFFER_TOKEN);
        if (TextUtils.isEmpty(offerToken)) {
            Log.e(OfferDetailsActivity.class.getSimpleName(), "No offer token in extras");
            showOfferLoadError();
        }

        for (Offer pendingOffer : unlockManager.getPendingOffers()) {
            if (offerToken.equalsIgnoreCase(pendingOffer.token)) {
                retVal = pendingOffer;
            }
        }

        if (retVal == null) {
            Log.e(OfferDetailsActivity.class.getSimpleName(), "Could not find offer");
            showOfferLoadError();
        }

        return retVal;
    }

    public void showOfferLoadError() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Something happened while loading the offer. More info has been printed in the logcat.")
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        OfferDetailsActivity.this.finish();
                    }
                })
                .show();
    }
}
