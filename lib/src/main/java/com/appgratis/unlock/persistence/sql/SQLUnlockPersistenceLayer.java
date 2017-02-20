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

package com.appgratis.unlock.persistence.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.appgratis.unlock.internal.RedeemedFeature;
import com.appgratis.unlock.model.Feature;
import com.appgratis.unlock.persistence.UnlockPersistenceLayer;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Class managing the persisted unlock data
 */
public class SQLUnlockPersistenceLayer extends SQLiteOpenHelper implements UnlockPersistenceLayer {

    private static final String DATABASE_NAME = "appgratis_unlocks.db";
    private static final int DATABASE_VERSION = 1;

    public SQLUnlockPersistenceLayer(@NonNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    //region SQLiteOpenHelper

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OfferConsuptionTable.CREATE_TABLE_SQL);
        db.execSQL(FeatureTable.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Will be implemented when needed
    }

    //endregion

    //region UnlockPersistenceLayer methods

    @NonNull
    public synchronized void saveOffer(@NonNull String offerToken) {
        final SQLiteDatabase db = getWritableDatabase();
        db.insert(OfferConsuptionTable.TABLE_NAME, null, OfferConsuptionTable.convertToContentValues(offerToken, null));
        db.close();
    }

    @Override
    public synchronized void saveRepeatingOffer(@NonNull String offerToken, @NonNull Date unlockDate) {
        final SQLiteDatabase db = getWritableDatabase();
        db.insert(OfferConsuptionTable.TABLE_NAME, null, OfferConsuptionTable.convertToContentValues(offerToken, getDateWithoutTimeComponents(unlockDate)));
        db.close();
    }

    @Override
    public synchronized void saveFeature(@NonNull Feature feature) {
        final SQLiteDatabase db = getWritableDatabase();
        db.insert(FeatureTable.TABLE_NAME, null, FeatureTable.convertToContentValues(feature));
        db.close();
    }

    @Override
    @NonNull
    public synchronized List<RedeemedFeature> getAvailableFeatures() {
        final List<RedeemedFeature> redeemedFeatures = new LinkedList<>();
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(FeatureTable.TABLE_NAME, null, null, null, null, null, null);

            while (cursor.moveToNext()) {
                final Feature feature = new Feature();
                feature.name = cursor.getString(cursor.getColumnIndex(FeatureTable.COLUMN_NAME));
                feature.initialLifetime = cursor.getInt(cursor.getColumnIndex(FeatureTable.COLUMN_TTL));
                redeemedFeatures.add(new RedeemedFeature(feature, new Date(cursor.getLong(cursor.getColumnIndex(FeatureTable.COLUMN_REDEEM_DATE)))));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return redeemedFeatures;
    }

    @Override
    public synchronized boolean isOfferConsumed(@NonNull String offerToken) {
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(OfferConsuptionTable.TABLE_NAME, null, OfferConsuptionTable.COLUMN_TOKEN + " = ? AND " + OfferConsuptionTable.COLUMN_CONSUMPTION_DATE + " = 0", new String[]{offerToken}, null, null, null);
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public synchronized boolean isRepeatingOfferConsumedForDay(@NonNull String offerToken, @NonNull Date day) {
        day = getDateWithoutTimeComponents(day);

        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(OfferConsuptionTable.TABLE_NAME, null, OfferConsuptionTable.COLUMN_TOKEN + " = ? AND " + OfferConsuptionTable.COLUMN_CONSUMPTION_DATE + " = ?", new String[]{offerToken, Long.toString(day.getTime())}, null, null, null);
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public synchronized void deleteFeatures(List<Feature> features) {
        if (features.isEmpty()) {
            return;
        }
        String[] featureNames = new String[features.size()];
        final StringBuilder whereBuilder = new StringBuilder(FeatureTable.COLUMN_NAME);
        whereBuilder.append(" IN (");
        for (int i = 0; i < features.size(); i++) {
            if (i > 0) {
                whereBuilder.append(',');
            }
            whereBuilder.append('?');
            featureNames[i] = features.get(i).name;
        }
        whereBuilder.append(')');

        final SQLiteDatabase db = getWritableDatabase();
        db.delete(FeatureTable.TABLE_NAME, whereBuilder.toString(), featureNames);
        db.close();
    }

    @Override
    public synchronized void wipe() {
        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + OfferConsuptionTable.TABLE_NAME + ";");
        db.execSQL("DELETE FROM " + FeatureTable.TABLE_NAME + ";");
        db.close();
    }

    //endregion

    private static Date getDateWithoutTimeComponents(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
