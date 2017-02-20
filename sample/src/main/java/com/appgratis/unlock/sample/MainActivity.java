/*
 * MIT License
 *
 * Copyright (c) 2016 iMediapp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.appgratis.unlock.sample;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;

import com.appgratis.unlock.internal.DatasourceParsingException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SectionedMetaAdapter.SectionsDataProvider {

    private RecyclerView recyclerView;
    private SectionedMetaAdapter recyclerViewAdapter;
    private SectionedMetaAdapter.SectionDatasource[] sectionDatasources = new SectionedMetaAdapter.SectionDatasource[3];

    private SampleUnlockManager unlockManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            unlockManager = new SampleUnlockManager(this);
        } catch (IOException | DatasourceParsingException e) {
            Log.e(MainActivity.class.getSimpleName(), "Error while loading the unlock manager", e);
            showUnlockManagerError();
        }

        recyclerViewAdapter = new SectionedMetaAdapter(this);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDataProviders();
    }

    public void refreshDataProviders() {
        sectionDatasources[0] = new AvailableOffersDatasource(unlockManager.getPendingOffers());
        sectionDatasources[1] = new RedeemedFeaturesDatasource(unlockManager.getFeatures());
        sectionDatasources[2] = new ResourcesDatasource(this);

        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void showUnlockManagerError() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Something happened while loading the offer list. The app cannot continue.")
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MainActivity.this.finish();
                    }
                })
                .show();
    }

    @Override
    public int getSectionCount() {
        return sectionDatasources.length;
    }

    @Override
    public int getItemCount(int section) {
        return sectionDatasources[section].getItemCount();
    }

    @Override
    public String getHeaderText(int section) {
        return sectionDatasources[section].getHeaderText();
    }

    @Override
    public void onBindViewHolder(SectionedMetaAdapter.ItemViewHolder holder, int section, int row) {
        sectionDatasources[section].onBindViewHolder(holder, row);
    }

}
