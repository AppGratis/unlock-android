package com.appgratis.unlock.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.lang.ref.WeakReference;

public class SectionedMetaAdapter extends SectionedRecyclerViewAdapter {

    private WeakReference<SectionsDataProvider> sectionsDataProvider;

    public SectionedMetaAdapter(SectionsDataProvider sectionsDataProvider) {
        this.sectionsDataProvider = new WeakReference<>(sectionsDataProvider);
    }

    @Override
    public int getSectionCount() {
        return getSectionsDataProvider().getSectionCount();
    }

    @Override
    public int getItemCount(int section) {
        return getSectionsDataProvider().getItemCount(section);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
        ((HeaderViewHolder)holder).textView.setText(getSectionsDataProvider().getHeaderText(section));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, int relativePosition, int absolutePosition) {
        getSectionsDataProvider().onBindViewHolder((ItemViewHolder)holder, section, relativePosition);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType == VIEW_TYPE_HEADER ? R.layout.main_rv_header : R.layout.main_rv_item, parent, false);
        return viewType == VIEW_TYPE_HEADER ? new HeaderViewHolder(v) : new ItemViewHolder(v);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.textView);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        public TextView titleTextView;
        public TextView detailsTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            titleTextView = (TextView)itemView.findViewById(R.id.titleTextView);
            detailsTextView = (TextView)itemView.findViewById(R.id.detailsTextView);
        }
    }

    private SectionsDataProvider getSectionsDataProvider() {
        return sectionsDataProvider.get();
    }

    interface SectionsDataProvider {
        int getSectionCount();

        int getItemCount(int section);

        String getHeaderText(int section);

        void onBindViewHolder(ItemViewHolder holder, int section, int row);
    }

    public interface SectionDatasource {

        int getItemCount();

        String getHeaderText();

        void onBindViewHolder(SectionedMetaAdapter.ItemViewHolder holder, int row);
    }
}
