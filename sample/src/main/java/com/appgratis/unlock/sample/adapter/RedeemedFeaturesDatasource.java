package com.appgratis.unlock.sample.adapter;

import android.support.annotation.NonNull;

import com.appgratis.unlock.model.Feature;

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
        Feature feature = redeemedFeatures.get(row);
        holder.titleTextView.setText(feature.name.toUpperCase(Locale.US));
        holder.detailsTextView.setText(null);
    }
}
