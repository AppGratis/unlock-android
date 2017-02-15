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

package com.appgratis.unlock.internal;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.appgratis.unlock.model.Feature;
import com.appgratis.unlock.model.Offer;
import com.appgratis.unlock.model.Resource;
import com.appgratis.unlock.model.condition.BaseCondition;
import com.appgratis.unlock.model.condition.RepeatCondition;
import com.appgratis.unlock.model.condition.WeeklyRepeatCondition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Class responsible for parsing the JSON data given by a {@link com.appgratis.unlock.source.OfferSource} into native objects.
 * <p/>
 * This class isn't thread safe.
 */
public class DatasourceParser {

    private static final String TAG = "DatasourceParser";

    // Means exactly the same thing as targetSdkVersion
    private static final int TARGET_PAYLOAD_VERSION = 1;

    @NonNull
    private String rawPayload;

    public DatasourceParser(@NonNull String payload) {
        this.rawPayload = payload;
    }

    @NonNull
    public List<Offer> parse() throws DatasourceParsingException {
        JSONObject payload;
        try {
            payload = new JSONObject(rawPayload);
        } catch (JSONException e) {
            Log.e(TAG, "An exception was caught while trying to convert the payload String to JSON: ", e);
            throw new DatasourceParsingException("An exception was caught while trying to parse the offers JSON. No offer will be available.", e);
        }

        try {
            int version = payload.optInt("version", 0);
            if (version > TARGET_PAYLOAD_VERSION || version < 1) {
                throw new DatasourceParsingException("Invalid data version");
            }

            JSONArray offers = payload.getJSONArray("offers");

            final List<Offer> retVal = new LinkedList<>();

            for (int i = 0; i < offers.length(); i++) {
                retVal.add(parseOffer(offers.getJSONObject(i)));
            }

            return retVal;
        } catch (JSONException e) {
            throw new DatasourceParsingException("An exception was caught while trying to parse the offers JSON. No offer will be available.", e);
        }
    }

    @VisibleForTesting
    protected Offer parseOffer(@NonNull JSONObject offer) throws JSONException, DatasourceParsingException {
        final Offer retVal = new Offer();

        String token = JSONTools.optString(offer, "token", null);

        if (TextUtils.isEmpty(token)) {
            throw new DatasourceParsingException("Invalid offer definition");
        }
        retVal.token = token;

        retVal.unlockMessage = JSONTools.optString(offer, "unlock_message", null);

        Object data = offer.opt("data");
        if (data instanceof JSONObject) {
            retVal.data = (JSONObject) data;
        }

        retVal.conditions = parseConditions(offer.getJSONObject("conditions"));

        Object resources = offer.opt("resources");
        // No resources array is valid
        if (resources instanceof JSONArray) {
            final JSONArray resourcesArray = (JSONArray) resources;
            for (int i = 0; i < resourcesArray.length(); i++) {
                retVal.resources.add(parseResource(resourcesArray.getJSONObject(i)));
            }
        }

        Object features = offer.opt("features");
        // Same for features
        if (features instanceof JSONArray) {
            final JSONArray featuresArray = (JSONArray) features;
            for (int i = 0; i < featuresArray.length(); i++) {
                retVal.features.add(parseFeature(featuresArray.getJSONObject(i)));
            }
        }

        return retVal;
    }

    @NonNull
    @VisibleForTesting
    protected Feature parseFeature(@NonNull JSONObject feature) throws JSONException, DatasourceParsingException {
        final String name = JSONTools.optString(feature, "name", null);
        if (TextUtils.isEmpty(name)) {
            throw new DatasourceParsingException("Features must have a name.");
        }

        final Feature retVal = new Feature();
        retVal.name = name;
        retVal.initialLifetime = feature.optInt("ttl", Feature.UNLIMITED_LIFETIME);
        retVal.remainingLifetime = retVal.initialLifetime;
        return retVal;
    }

    @NonNull
    @VisibleForTesting
    protected Resource parseResource(@NonNull JSONObject resource) throws JSONException, DatasourceParsingException {
        final String name = JSONTools.optString(resource, "name", null);
        if (TextUtils.isEmpty(name)) {
            throw new DatasourceParsingException("Resources must have a name.");
        }

        if (!resource.has("quantity") || resource.isNull("quantity")) {
            throw new DatasourceParsingException("Resources must have a quantity.");
        }

        final int quantity = resource.getInt("quantity");
        if (quantity < 1) {
            throw new DatasourceParsingException("Resource quantity must be greater than zero.");
        }

        final Resource retVal = new Resource();
        retVal.name = name;
        retVal.quantity = quantity;
        return retVal;
    }

    @NonNull
    @VisibleForTesting
    protected BaseCondition parseConditions(@NonNull JSONObject condition) throws JSONException, DatasourceParsingException {
        BaseCondition retVal;

        Object repeat = condition.opt("repeat");
        if (repeat instanceof JSONObject) {
            JSONObject repeatJSON = (JSONObject) repeat;
            try {
                final RepeatCondition.Frequency frequency = RepeatCondition.Frequency.valueOf(JSONTools.optString(repeatJSON, "every", "").toUpperCase(Locale.US));
                if (frequency == RepeatCondition.Frequency.WEEK) {
                    retVal = new WeeklyRepeatCondition();
                    ((WeeklyRepeatCondition) retVal).weekday = WeeklyRepeatCondition.Weekday.valueOf(JSONTools.optString(repeatJSON, "weekday", "").toUpperCase(Locale.US));
                } else {
                    retVal = new RepeatCondition();
                }

                RepeatCondition repeatRetVal = (RepeatCondition) retVal;
                repeatRetVal.frequency = frequency;
                try {
                    repeatRetVal.startHour = repeatJSON.getInt("start_hour");
                    repeatRetVal.endHour = repeatJSON.getInt("end_hour");

                    if (repeatRetVal.startHour < 0 || repeatRetVal.startHour > 23) {
                        throw new DatasourceParsingException("'start_hour' cannot be lower than 0 or greater than 23");
                    }
                    if (repeatRetVal.endHour < 1 || repeatRetVal.endHour > 24) {
                        throw new DatasourceParsingException("'start_hour' cannot be lower than 1 or greater than 24");
                    }
                } catch (IllegalArgumentException e) {
                    throw new DatasourceParsingException("'start_hour' and 'end_hour' are mandatory.");
                }
            } catch (IllegalArgumentException e) {
                throw new DatasourceParsingException("Unknown offer repeat condition parameters.", e);
            }
        } else {
            retVal = new BaseCondition();
        }

        retVal.newUsersOnly = condition.optBoolean("only_new_users", false);

        final long startDate = condition.optLong("start_date", -1);

        if (startDate < 0) {
            throw new DatasourceParsingException("'start_date' must be a valid timestamp");
        }


        retVal.startDate = new Date(startDate * 1000);

        if (condition.has("end_date") && !condition.isNull("end_date")) {
            final long endDate = condition.optLong("end_date", -1);
            if (endDate < 0) {
                throw new DatasourceParsingException("If supplied, 'end_date' must be a valid timestamp");
            }

            retVal.endDate = new Date(endDate * 1000);
        } else {
            retVal.endDate = null;
        }

        return retVal;
    }
}
