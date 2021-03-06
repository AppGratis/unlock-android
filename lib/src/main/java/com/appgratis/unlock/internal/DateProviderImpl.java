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

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

/**
 * Standard implementation of {@link DateProvider} that works as you would expect it to
 */
public class DateProviderImpl implements DateProvider {

    private Date installationDate;

    public DateProviderImpl() {
        installationDate = null;
    }

    public DateProviderImpl(Context context) {
        installationDate = AppPackageHelper.getInstallationDate(context);
    }

    @Override
    public Date getCurrentDate() {
        return new Date();
    }

    @Override
    public Calendar getCurrentCalendar() {
        return Calendar.getInstance();
    }

    @Override
    public Date getApplicationInstallationDate() {
        return installationDate;
    }
}
