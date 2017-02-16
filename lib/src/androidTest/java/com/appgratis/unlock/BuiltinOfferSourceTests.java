package com.appgratis.unlock;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.appgratis.unlock.source.BuiltinOfferSource;

import com.appgratis.unlock.test.R;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test builtin offer source (reads from res/raw)
 *
 * We're not testing invalid contexts or resource type as this is caught by annotations
 */
@RunWith(AndroidJUnit4.class)
public class BuiltinOfferSourceTests {

    @Test
    public void testValidRawJson() throws Exception {
        BuiltinOfferSource offerSource = new BuiltinOfferSource(InstrumentationRegistry.getTargetContext(), R.raw.offers);
        Assert.assertNotNull(offerSource);
        Assert.assertEquals("{\"version\":1,\"offers\":[{\"name\":\"New Offer\",\"token\":\"f4cc8d86-35c5-4df7-83e9-ba0be82ce43b\",\"unlock_message\":\"Test\",\"features\":[{\"lifetime\":-1,\"name\":\"NEW_FEATURE\"}],\"resources\":[{\"quantity\":1,\"name\":\"NEW_RESOURCE\"}],\"data\":null,\"conditions\":{\"start_date\":1485903600,\"end_date\":null,\"only_new_users\":false}}]}",
                offerSource.getOffersJSON());
    }
}
