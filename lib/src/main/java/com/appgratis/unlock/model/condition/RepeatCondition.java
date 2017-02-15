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
 * RepeatCondition extends the base condition and adds a repeating condition feature on top of it
 */
public class RepeatCondition extends BaseCondition {

    /**
     * Hour at which the repeated condition starts.
     */
    public int startHour = 0;

    /**
     * Hour at which the repeated condition ends.
     * Use "24" to indicate midnight of the next day
     */
    public int endHour = 0;

    /**
     * Repetition frequency. Matches the "every" key in the JSON description
     */
    @NonNull
    public Frequency frequency = Frequency.DAY;

    @Override
    public boolean isSatisfied(DateProvider dateProvider) {
        if (!super.isSatisfied(dateProvider)) {
            return false;
        }

        final Calendar currentDate = dateProvider.getCurrentCalendar();
        final int hourOfDay = currentDate.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay < startHour || hourOfDay > endHour) {
            return false;
        }

        // The daily repeat frequency is always true
        // Weekly will be handled by a subclass, returning true will let it handle it
        if (frequency == Frequency.MONTH) {
            // The monthly frequency checks that the day of the month of the start date is the same as today's
            // For example, a start date of 2016/12/03 with a monthly req, will be valid on 2016/
            // If the last day of month is inferior to the start date's day of month, don't fail the condition
            final Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            int startDayOfMonth = startCalendar.get(Calendar.DAY_OF_MONTH);

            if (startDayOfMonth < currentDate.getActualMaximum(Calendar.DAY_OF_MONTH) &&
                    startDayOfMonth != currentDate.get(Calendar.DAY_OF_MONTH)) {
                return false;
            }
        }

        return true;
    }

    public enum Frequency {
        DAY,
        WEEK,
        MONTH
    }
}
