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

import com.appgratis.unlock.internal.DateProvider;

import java.util.Calendar;

/**
 * Specialized {@link RepeatCondition} for weekly offers
 */
public class WeeklyRepeatCondition extends RepeatCondition {

    /**
     * Weekday on which this condition will be triggered
     */
    @NonNull
    public Weekday weekday = Weekday.MONDAY;

    @Override
    public boolean isSatisfied(DateProvider dateProvider) {
        if (!super.isSatisfied(dateProvider)) {
            return false;
        }

        //noinspection WrongConstant
        return dateProvider.getCurrentCalendar().get(Calendar.DAY_OF_WEEK) == weekday.toCalendarInteger();
    }

    @SuppressWarnings("unused")
    public enum Weekday {
        MONDAY(Calendar.MONDAY),
        TUESDAY(Calendar.TUESDAY),
        WEDNESDAY(Calendar.WEDNESDAY),
        THURSDAY(Calendar.THURSDAY),
        FRIDAY(Calendar.FRIDAY),
        SATURDAY(Calendar.SATURDAY),
        SUNDAY(Calendar.SUNDAY);

        private int calendarValue = 0;

        Weekday(int calendarValue) {
            this.calendarValue = calendarValue;
        }

        /**
         * Returns the {@link Calendar} value for this Weekday
         * @return the {@link Calendar} value for this Weekday
         */
        public int toCalendarInteger() {
            return calendarValue;
        }
    }
}
