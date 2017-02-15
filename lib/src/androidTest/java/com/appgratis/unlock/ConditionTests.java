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

package com.appgratis.unlock;

import android.support.test.runner.AndroidJUnit4;

import com.appgratis.unlock.model.condition.BaseCondition;
import com.appgratis.unlock.model.condition.RepeatCondition;
import com.appgratis.unlock.model.condition.WeeklyRepeatCondition;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Test offers content
 */
@RunWith(AndroidJUnit4.class)
public class ConditionTests {

    private final DateFormat TEST_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @Test
    public void testStartCondition() throws Exception {
        final BaseCondition mockCondition = new BaseCondition();

        mockCondition.startDate = TEST_DATE_FORMAT.parse("2016-02-01 18:00:00");
        mockCondition.endDate = null;
        mockCondition.newUsersOnly = false;

        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-02-01 18:00:00"), null)));
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-02-01 18:00:01"), null)));

        Assert.assertFalse(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-02-01 17:59:59"), null)));
    }

    @Test
    public void testEndCondition() throws Exception {
        final BaseCondition mockCondition = new BaseCondition();

        mockCondition.startDate = TEST_DATE_FORMAT.parse("2000-01-01 18:00:00");
        mockCondition.endDate = null;
        mockCondition.newUsersOnly = false;

        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-02-02 18:00:00"), null)));

        mockCondition.endDate = TEST_DATE_FORMAT.parse("2016-02-02 15:00:00");

        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(mockCondition.endDate, null)));
        Assert.assertFalse(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-02-02 15:00:01"), null)));
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-02-01 18:00:00"), null)));
    }

    @Test
    public void testNewUsersCondition() throws Exception {
        final MockDateProvider dateProvider = new MockDateProvider(TEST_DATE_FORMAT.parse("2016-02-02 18:00:00"), TEST_DATE_FORMAT.parse("2010-01-01 18:00:00"));
        final MockDateProvider dateProviderAfterStart = new MockDateProvider(TEST_DATE_FORMAT.parse("2016-02-02 18:00:00"), TEST_DATE_FORMAT.parse("2016-02-02 18:00:00"));
        final BaseCondition mockCondition = new BaseCondition();

        mockCondition.startDate = TEST_DATE_FORMAT.parse("2016-02-02 18:00:00");
        mockCondition.endDate = null;
        mockCondition.newUsersOnly = false;

        Assert.assertTrue(mockCondition.isSatisfied(dateProvider));
        Assert.assertTrue(mockCondition.isSatisfied(dateProviderAfterStart));

        mockCondition.newUsersOnly = true;

        Assert.assertFalse(mockCondition.isSatisfied(dateProvider));
        Assert.assertTrue(mockCondition.isSatisfied(dateProviderAfterStart));
    }

    @Test
    public void testDailyRepeatCondition() throws Exception {
        final RepeatCondition mockCondition = new RepeatCondition();

        mockCondition.startDate = TEST_DATE_FORMAT.parse("2010-02-02 18:00:00");
        mockCondition.endDate = null;
        mockCondition.newUsersOnly = false;
        mockCondition.frequency = RepeatCondition.Frequency.DAY;
        mockCondition.startHour = 18;
        mockCondition.endHour = 20;

        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2010-02-02 19:00:00"), null)));

        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-03-04 18:00:00"), null)));
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-05-08 20:00:00"), null)));

        Assert.assertFalse(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-05-08 17:59:59"), null)));
        Assert.assertFalse(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-05-08 21:00:00"), null)));
    }

    @Test
    public void testMonthlyRepeatCondition() throws Exception {
        final RepeatCondition mockCondition = new RepeatCondition();

        mockCondition.startDate = TEST_DATE_FORMAT.parse("2010-02-02 18:00:00");
        mockCondition.endDate = null;
        mockCondition.newUsersOnly = false;
        mockCondition.frequency = RepeatCondition.Frequency.MONTH;
        mockCondition.startHour = 18;
        mockCondition.endHour = 20;

        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2010-02-02 18:00:00"), null)));

        Assert.assertFalse(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-03-04 18:00:00"), null)));
        Assert.assertFalse(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-05-08 20:00:00"), null)));

        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-03-02 18:00:00"), null)));
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-05-02 20:00:00"), null)));

        mockCondition.startDate = TEST_DATE_FORMAT.parse("2016-08-31 18:00:00");
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(mockCondition.startDate, null)));
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-09-30 18:00:00"), null)));
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2017-02-28 18:00:00"), null)));

        Assert.assertFalse(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2015-09-30 18:00:00"), null)));
    }

    @Test
    public void testWeeklyRepeatCondition() throws Exception {
        final WeeklyRepeatCondition mockCondition = new WeeklyRepeatCondition();

        mockCondition.startDate = TEST_DATE_FORMAT.parse("2010-02-02 18:00:00");
        mockCondition.endDate = null;
        mockCondition.newUsersOnly = false;
        mockCondition.frequency = RepeatCondition.Frequency.WEEK;
        mockCondition.weekday = WeeklyRepeatCondition.Weekday.FRIDAY;
        mockCondition.startHour = 18;
        mockCondition.endHour = 20;

        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-09-09 18:00:00"), null)));
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-09-16 18:00:00"), null)));
        Assert.assertFalse(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-09-15 18:00:00"), null)));
        Assert.assertFalse(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-09-17 18:00:00"), null)));

        mockCondition.weekday = WeeklyRepeatCondition.Weekday.THURSDAY;
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-09-15 18:00:00"), null)));

        mockCondition.weekday = WeeklyRepeatCondition.Weekday.SATURDAY;
        Assert.assertTrue(mockCondition.isSatisfied(new MockDateProvider(TEST_DATE_FORMAT.parse("2016-09-17 18:00:00"), null)));
    }
}
