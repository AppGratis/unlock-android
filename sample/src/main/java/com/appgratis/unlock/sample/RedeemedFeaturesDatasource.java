package com.appgratis.unlock.sample;

import android.support.annotation.NonNull;

import com.appgratis.unlock.model.Feature;

import java.util.List;

public class RedeemedFeaturesDatasource implements SectionedMetaAdapter.SectionDatasource {
    private List<Feature> redeemedFeatures;

    public RedeemedFeaturesDatasource(@NonNull List<Feature> redeemedFeatures) {
        this.redeemedFeatures = redeemedFeatures;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public String getHeaderText() {
        return "Redeemed Features";
    }

    @Override
    public void onBindViewHolder(SectionedMetaAdapter.ItemViewHolder holder, int row) {

    }
}
