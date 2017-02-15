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

import java.util.Date;

/**
 * Feature represents an unlockable feature: the UnlockManager will remember that the user unlocked it for its configured lifetime (which can be unlimited).
 */
public class Feature extends Item {

    /**
     * Lifetime value for unlimited features.
     */
    // Do not change this value without bumping the SQL version
    public static final int UNLIMITED_LIFETIME = -1;

    /**
     * Configured lifetime of the feature when it was consumed, in seconds.
     * Will be {@link #UNLIMITED_LIFETIME} if unlimited.
     */
    public int initialLifetime = UNLIMITED_LIFETIME;

    /**
     * Time left until the user loses the feature, in seconds.
     * You might wanna schedule a recheck of the feature availability some time after this.
     * Will be null if unlimited.
     */
    public Integer remainingLifetime;

    /**
     * Updates the remaining lifetime
     */
    public void updateRemainingLifetime(Date currentDate, Date redeemDate) {
        if (initialLifetime != Feature.UNLIMITED_LIFETIME) {
            long elapsedTimeSinceRedeem = currentDate.getTime() - redeemDate.getTime();
            remainingLifetime = initialLifetime - (int) (elapsedTimeSinceRedeem / 1000);
        } else {
            remainingLifetime = null;
        }
    }

    /**
     * Returns whether this feature is time limited or not.
     * @return whether this feature is time limited or not.
     */
    public boolean isTimeLimited() {
        return initialLifetime != UNLIMITED_LIFETIME;
    }

    /**
     * Return whether this feature is expired or not.
     * @return whether this feature is expired or not.
     */
    public boolean isExpired() {
        return isTimeLimited() && remainingLifetime <= 0;
    }
}
