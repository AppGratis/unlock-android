package com.appgratis.unlock.sample.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.appgratis.unlock.model.Feature;
import com.appgratis.unlock.sample.activity.FeatureDetailsActivity;

import java.util.List;
import java.util.Locale;

public class RedeemedFeaturesDatasource implements SectionedMetaAdapter.SectionDatasource {
    private List<Feature> redeemedFeatures;

    public RedeemedFeaturesDatasource(@NonNull List<Feature> redeemedFeatures) {
        this.redeemedFeatures = redeemedFeatures;
    }

    @Override
    public int getItemCount() {
        return redeemedFeatures.size();
    }

    @Override
    public String getHeaderText() {
        return "Redeemed Features";
    }

    @Override
    public void onBindViewHolder(SectionedMetaAdapter.ItemViewHolder holder, int row) {
        final Feature feature = redeemedFeatures.get(row);
        holder.titleTextView.setText(feature.name.toUpperCase(Locale.US));
        holder.detailsTextView.setText(null);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context c = v.getContext();
                final Intent i = new Intent(c, FeatureDetailsActivity.class);
                i.putExtra(FeatureDetailsActivity.EXTRA_FEATURE_NAME, feature.name);
                c.startActivity(i);
            }
        });
    }
}
