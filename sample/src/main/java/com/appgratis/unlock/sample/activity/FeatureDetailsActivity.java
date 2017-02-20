package com.appgratis.unlock.sample.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.appgratis.unlock.internal.DatasourceParsingException;
import com.appgratis.unlock.model.Feature;
import com.appgratis.unlock.sample.R;
import com.appgratis.unlock.sample.manager.SampleUnlockManager;

import java.io.IOException;

public class FeatureDetailsActivity extends AppCompatActivity {

    public static String EXTRA_FEATURE_NAME = "feature_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_details);

        Feature feature = loadFeature();

        if (feature == null) {
            return;
        }

        TextView nameTV = (TextView) findViewById(R.id.nameTV);
        TextView initialLifetimeTV = (TextView) findViewById(R.id.initialLifetimeTV);
        TextView timeLeftTV = (TextView) findViewById(R.id.timeLeftTV);

        nameTV.setText(feature.name);
        initialLifetimeTV.setText(feature.isTimeLimited() ? feature.initialLifetime + " s" : "Unlimited");
        timeLeftTV.setText(feature.isTimeLimited() ? feature.remainingLifetime + " s" : "Unlimited");
    }

    private Feature loadFeature() {
        Feature retVal = null;

        String featureName = getIntent().getStringExtra(EXTRA_FEATURE_NAME);
        if (TextUtils.isEmpty(featureName)) {
            Log.e(FeatureDetailsActivity.class.getSimpleName(), "No feature name in extras");
            showFeatureLoadError();
        }

        try {
            SampleUnlockManager unlockManager = new SampleUnlockManager(this);

            for (Feature redeemedFeature : unlockManager.getFeatures()) {
                if (featureName.equalsIgnoreCase(redeemedFeature.name)) {
                    retVal = redeemedFeature;
                }
            }
        } catch (IOException | DatasourceParsingException e) {
            Log.e(FeatureDetailsActivity.class.getSimpleName(), "Error while loading the unlock manager", e);
            showFeatureLoadError();
        }

        if (retVal == null) {
            Log.e(FeatureDetailsActivity.class.getSimpleName(), "Could not find feature");
            showFeatureLoadError();
        }

        return retVal;
    }

    public void showFeatureLoadError() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Something happened while loading the feature. More info has been printed in the logcat.")
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        FeatureDetailsActivity.this.finish();
                    }
                })
                .show();
    }
}
