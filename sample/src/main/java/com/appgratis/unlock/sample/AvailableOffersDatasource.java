package com.appgratis.unlock.sample;

import android.support.annotation.NonNull;

import com.appgratis.unlock.model.Offer;

import java.util.List;

public class AvailableOffersDatasource implements SectionedMetaAdapter.SectionDatasource {

    private List<Offer> availableOffers;

    public AvailableOffersDatasource(@NonNull List<Offer> availableOffers) {
        this.availableOffers = availableOffers;
    }

    @Override
    public int getItemCount() {
        return availableOffers.size();
    }

    @Override
    public String getHeaderText() {
        return "Available Offers";
    }

    @Override
    public void onBindViewHolder(SectionedMetaAdapter.ItemViewHolder holder, int row) {
        Offer offer = availableOffers.get(row);
        holder.titleTextView.setText(offer.token);
        holder.detailsTextView.setText(null);
    }
}
