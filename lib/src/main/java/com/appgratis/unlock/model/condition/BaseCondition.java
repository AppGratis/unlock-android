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

package com.appgratis.unlock.model.condition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.appgratis.unlock.internal.DateProvider;

import java.util.Date;

/**
 * BaseCondition represents the basic conditions of an offer, start/end date & code
 */
public class BaseCondition {

    /**
     * The date on which the offer starts. Not optional
     */
    @NonNull
    public Date startDate = new Date();

    /**
     * The date on which the offer ends
     */
    @Nullable
    public Date endDate;

    /**
     * Whether the campaign targets new users only or not.
     * A new user is a user who installed the app after the offer's start date.
     */
    public boolean newUsersOnly = false;

    /**
     * Checks if offer's conditions are currently satisfied.
     *
     * Subclasses can extend this method to add their checks.
     * @param dateProvider A date provider for date comparisons
     * @return Whether the offer conditions are satisfied or not
     */
    public boolean isSatisfied(final DateProvider dateProvider) {
        final Date currentDate = dateProvider.getCurrentDate();

        if (currentDate.compareTo(startDate) < 0) {
            // Offer hasn't started yet
            return false;
        }

        if (endDate != null && currentDate.compareTo(endDate) > 0) {
            // Offer is over
            return false;
        }

        if (newUsersOnly && dateProvider.getApplicationInstallationDate().compareTo(startDate) < 0) {
            // The user installed after the start date, and the offer is for new users only
            return false;
        }

        return true;
    }
}
