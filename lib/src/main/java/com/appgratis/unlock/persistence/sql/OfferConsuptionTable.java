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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * SQL Table that stores offer consumptions (both recurring and one-shot)
 */
public class OfferConsuptionTable {
    public static final String TABLE_NAME = "OFFER_CONSUMPTIONS";

    public static final String COLUMN_DB_ID = "_db_id";
    public static final String COLUMN_TOKEN = "token";
    public static final String COLUMN_CONSUMPTION_DATE = "consumption_date";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_DB_ID + " integer primary key autoincrement, " +
            COLUMN_TOKEN + " text not null, " +
            COLUMN_CONSUMPTION_DATE + " integer not null, " +
            "unique(" + COLUMN_TOKEN + "," + COLUMN_CONSUMPTION_DATE + ") on conflict replace);";

    public static ContentValues convertToContentValues(@NonNull String offerToken, @Nullable Date date) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TOKEN, offerToken);
        contentValues.put(COLUMN_CONSUMPTION_DATE, date != null ? date.getTime() : 0);
        return contentValues;
    }
}
