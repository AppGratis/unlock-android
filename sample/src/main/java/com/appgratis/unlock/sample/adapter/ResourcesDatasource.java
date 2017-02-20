package com.appgratis.unlock.sample.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.appgratis.unlock.sample.manager.ResourceManager;

public class ResourcesDatasource implements SectionedMetaAdapter.SectionDatasource {

    private static String[] RESOURCE_NAMES = new String[] {"FOOD", "GOLD"};

    private ResourceManager manager;

    public ResourcesDatasource(@NonNull Context context) {
        manager = new ResourceManager(context);
    }

    @Override
    public int getItemCount() {
        return RESOURCE_NAMES.length;
    }

    @Override
    public String getHeaderText() {
        return "Resources";
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(SectionedMetaAdapter.ItemViewHolder holder, int row) {
        holder.rootView.setOnClickListener(null);
        String resource = RESOURCE_NAMES[row];
        holder.titleTextView.setText(resource);
        holder.detailsTextView.setText(Long.toString(manager.get(resource)));
    }
}
