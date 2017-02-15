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

import android.content.ContentValues;

import com.appgratis.unlock.model.Feature;

import java.util.Date;

/**
 * SQL Table that stores redeemed features
 */
public class FeatureTable {
    public static final String TABLE_NAME = "FEATURES";

    public static final String COLUMN_DB_ID = "_db_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TTL = "ttl";
    public static final String COLUMN_REDEEM_DATE = "redeem_date";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_DB_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_TTL + " integer not null, " +
            COLUMN_REDEEM_DATE + " integer not null, " +
            "unique(" + COLUMN_NAME + ") on conflict replace);";

    public static ContentValues convertToContentValues(Feature feature) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, feature.name);
        contentValues.put(COLUMN_TTL, feature.initialLifetime);
        contentValues.put(COLUMN_REDEEM_DATE, new Date().getTime());
        return contentValues;
    }
}
