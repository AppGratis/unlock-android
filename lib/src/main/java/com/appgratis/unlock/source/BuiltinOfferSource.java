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

package com.appgratis.unlock.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Offer source that reads
 */
public class BuiltinOfferSource implements OfferSource {

    private String offersJSON = "{}";

    public BuiltinOfferSource(@NonNull Context context, @RawRes int rawResourceID) throws IOException {
        InputStream rawFileInputStream = context.getResources().openRawResource(rawResourceID);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(rawFileInputStream));

            //Stupid Android Studio forgetting try with resources is only kitkat+
            //noinspection TryFinallyCanBeTryWithResources
            try {
                StringBuilder fileContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line);
                }
                offersJSON = fileContent.toString();
            } finally {
                //noinspection ThrowFromFinallyBlock
                reader.close();
            }
        } finally {
            if (rawFileInputStream != null) {
                //noinspection ThrowFromFinallyBlock
                rawFileInputStream.close();
            }
        }
    }

    @Override
    public String getOffersJSON() {
        return offersJSON;
    }
}
