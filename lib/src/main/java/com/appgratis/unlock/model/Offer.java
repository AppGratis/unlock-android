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

package com.appgratis.unlock.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.appgratis.unlock.model.condition.BaseCondition;
import com.appgratis.unlock.model.condition.RepeatCondition;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Offer represents a promotion.
 */
public class Offer {

    /**
     * Offer token, uniquely representing it. Changing the offer's content without changing the token can have undesirable effects on the library's job.
     */
    @NonNull
    public String token = "";

    /**
     * Optional unlock message that you should display to your used when consuming the offer.
     */
    @Nullable
    public String unlockMessage;

    /**
     * Offer conditions.
     */
    @NonNull
    public BaseCondition conditions;

    /**
     * Raw additional offer data.
     */
    @Nullable
    public JSONObject data;

    /**
     * Features you should give to the user upon consuming.
     */
    @NonNull
    public List<Feature> features = new LinkedList<>();

    /**
     * Resources you should give to the user upon consuming.
     */
    @NonNull
    public List<Resource> resources = new LinkedList<>();

    /**
     * Returns whether the campaign has a repeating condition
     * @return whether the campaign has a repeating condition
     */
    public boolean isRepeating() {
        return conditions instanceof RepeatCondition;
    }
}
