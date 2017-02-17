package com.appgratis.unlock.sample;

public class ResourcesDatasource implements SectionedMetaAdapter.SectionDatasource {
    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public String getHeaderText() {
        return "Resources";
    }

    @Override
    public void onBindViewHolder(SectionedMetaAdapter.ItemViewHolder holder, int row) {
        holder.rootView.setOnClickListener(null);
    }
}
