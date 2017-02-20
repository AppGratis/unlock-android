package com.appgratis.unlock.sample;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

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
        final String offerToken = offer.token;
        holder.titleTextView.setText(offerToken);
        holder.detailsTextView.setText(null);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context c = v.getContext();
                final Intent i = new Intent(c, OfferDetailsActivity.class);
                i.putExtra(OfferDetailsActivity.EXTRA_OFFER_TOKEN, offerToken);
                c.startActivity(i);
            }
        });
    }
}
