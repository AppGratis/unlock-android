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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;

/**
 * Helper for package related methods
 */
public class AppPackageHelper {
    private AppPackageHelper() {}

    private static Date cachedInstallationDate = null;

    /**
     * Get the app installation date from the package manager.
     * The result will be cached for better performance.
     * @param context Context to call the package manager.
     * @return The app installation date
     */
    @NonNull
    public synchronized static Date getInstallationDate(@NonNull Context context) {
        if (cachedInstallationDate != null) {
            return cachedInstallationDate;
        }

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            cachedInstallationDate = new Date(info.firstInstallTime);
            return cachedInstallationDate;
        } catch (PackageManager.NameNotFoundException e) {
            // Since we're querying the current package, we'll probably never end up here
            // That's not an excuse to badly handle this
            Log.e(AppPackageHelper.class.getSimpleName(), "Unexpected error when getting the application installation date.", e);
        }

        return new Date();
    }
}
