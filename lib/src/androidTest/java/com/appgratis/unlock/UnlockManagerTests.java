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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.appgratis.unlock.internal.DatasourceParsingException;
import com.appgratis.unlock.internal.DateProvider;
import com.appgratis.unlock.internal.DateProviderImpl;
import com.appgratis.unlock.model.Offer;
import com.appgratis.unlock.model.condition.RepeatCondition;
import com.appgratis.unlock.persistence.UnlockPersistenceLayer;
import com.appgratis.unlock.persistence.sql.SQLUnlockPersistenceLayer;
import com.appgratis.unlock.source.OfferSource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class UnlockManagerTests {

    @Test
    public void testOfferParsing() throws DatasourceParsingException {
        final DateProvider dateProvider = new DateProviderImpl();
        // Simply test that we can instantiate the unlock manager with a valid json
        new UnlockManager(new DummyStringOfferSource(validTestJSON), new MockUnlockPersistenceLayer(), dateProvider);

        new UnlockManager(new DummyStringOfferSource("{\"version\": 1,\"offers\": []}"), new MockUnlockPersistenceLayer(), dateProvider);

        new UnlockManager(new DummyStringOfferSource("{\n" +
                "  \"version\": 1,\n" +
                "  \"offers\": [\n" +
                "    {\n" +
                "      \"token\": \"ec6e7033-a785-4d6a-adf5-14d72bf8490b\",\n" +
                "      \"unlock_message\": \"Expired\",\n" +
                "      \"conditions\": {\n" +
                "        \"start_date\": 1473233825,\n" +
                "        \"end_date\": 1473233826\n" +
                "      },\n" +
                "      \"features\": [\n" +
                "      ],\n" +
                "      \"resources\": [\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n"), new MockUnlockPersistenceLayer(), dateProvider);

        try {
            // Invalid offers
            new UnlockManager(new DummyStringOfferSource("{\n" +
                    "  \"version\": 1,\n" +
                    "  \"offers\": [\n" +
                    "    {\n" +
                    "      \"features\": [\n" +
                    "        {\"name\": \"dummy\", \"ttl\": 86400}\n" +
                    "      ],\n" +
                    "      \"resources\": [\n" +
                    "        {\"name\": \"dummy\", \"quantity\": 170}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n"), new MockUnlockPersistenceLayer(), dateProvider);
            Assert.fail("A DatasourceParsingException should have been thrown (no offer token).");
        } catch (DatasourceParsingException ignored) {
        }

        try {
            // Invalid offers
            new UnlockManager(new DummyStringOfferSource("{\n" +
                    "  \"version\": 1,\n" +
                    "  \"offers\": [\n" +
                    "    {\n" +
                    "      \"token\": \"ec6e7033-a785-4d6a-adf5-14d72bf8490b\",\n" +
                    "      \"unlock_message\": \"Expired\",\n" +
                    "      \"features\": [\n" +
                    "        {\"name\": \"dummy\", \"ttl\": 86400}\n" +
                    "      ],\n" +
                    "      \"resources\": [\n" +
                    "        {\"name\": \"dummy\", \"quantity\": 170}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n"), new MockUnlockPersistenceLayer(), dateProvider);
            Assert.fail("A DatasourceParsingException should have been thrown (no offer conditions).");
        } catch (DatasourceParsingException ignored) {
        }
    }

    @Test
    public void testOfferValidity() throws Exception {
        final UnlockManager manager = new UnlockManager(new DummyStringOfferSource(validTestJSON), new MockUnlockPersistenceLayer(), new DateProviderImpl());

        List<Offer> pendingOffers = manager.getPendingOffers();
        Assert.assertEquals(1, pendingOffers.size());

        List<Offer> parsedOffers = manager.offers;
        Assert.assertEquals(3, parsedOffers.size());
    }

    private void baseTestOfferConsumption(UnlockPersistenceLayer persistenceLayer) throws Exception {
        final UnlockManager manager = new UnlockManager(new DummyStringOfferSource(validTestJSON), persistenceLayer, new DateProviderImpl());
        manager.wipeData();

        Assert.assertEquals(0, manager.getFeatures().size());

        final Offer offer = manager.getPendingOffers().get(0);
        Assert.assertNotNull(offer);
        manager.consumeOffer(offer);

        Assert.assertEquals(1, manager.getFeatures().size());
        Assert.assertEquals(0, manager.getPendingOffers().size());
    }

    @Test
    public void testOfferConsumption() throws Exception {
        baseTestOfferConsumption(new MockUnlockPersistenceLayer());
        baseTestOfferConsumption(new SQLUnlockPersistenceLayer(InstrumentationRegistry.getTargetContext()));
    }

    private void baseTestOfferOccurrenceConsumption(UnlockPersistenceLayer persistenceLayer) throws Exception {
        // This shouldn't really be tested that way, but rather in dedicated PersistenceLayer tests
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final MockDateProvider mockDateProvider = new MockDateProvider(df.parse("2016-02-03"), null);

        UnlockManager manager = new UnlockManager(new DummyStringOfferSource("{version:1, offers:[]}"), persistenceLayer, mockDateProvider);
        manager.wipeData();

        Assert.assertEquals(0, manager.offers.size());
        final Offer testOffer = new Offer();
        testOffer.token = "test";
        final RepeatCondition testCondition = new RepeatCondition();
        testCondition.startDate = mockDateProvider.getCurrentDate();
        testCondition.startHour = 0;
        testCondition.endHour = 24;
        testCondition.frequency = RepeatCondition.Frequency.DAY;
        testOffer.conditions = testCondition;
        manager.offers.add(testOffer);

        Assert.assertEquals(1, manager.getPendingOffers().size());
        manager.consumeOffer(testOffer);

        Assert.assertEquals(0, manager.getPendingOffers().size());

        mockDateProvider.setMockedCurrentDate(df.parse("2016-02-04"));
        Assert.assertEquals(1, manager.getPendingOffers().size());

        manager.consumeOffer(testOffer);
        Assert.assertEquals(0, manager.getPendingOffers().size());

        manager.wipeData();
    }

    @Test
    public void testOfferOccurrenceConsumption() throws Exception {
        baseTestOfferOccurrenceConsumption(new MockUnlockPersistenceLayer());
        baseTestOfferOccurrenceConsumption(new SQLUnlockPersistenceLayer(InstrumentationRegistry.getTargetContext()));
    }

    //region Helper methods

    private class DummyStringOfferSource implements OfferSource {

        private String json;

        public DummyStringOfferSource(String json) {
            this.json = json;
        }

        @Override
        public String getOffersJSON() {
            return json;
        }
    }

    private static final String validTestJSON = "{\n" +
            "  \"version\": 1,\n" +
            "  \"offers\": [\n" +
            "    {\n" +
            "      \"token\": \"ec6e7033-a785-4d6a-adf5-14d72bf8490b\",\n" +
            "      \"unlock_message\": \"Expired\",\n" +
            "      \"conditions\": {\n" +
            "        \"start_date\": 1473233825,\n" +
            "        \"end_date\": 1473233826\n" +
            "      },\n" +
            "      \"features\": [\n" +
            "        {\"name\": \"dummy\", \"ttl\": 86400}\n" +
            "      ],\n" +
            "      \"resources\": [\n" +
            "        {\"name\": \"dummy\", \"quantity\": 170}\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"token\": \"8c87e4b4-399a-4067-a175-e093b12f9ecd\",\n" +
            "      \"unlock_message\": \"Welcome!\",\n" +
            "      \"data\": {\"foo\": \"bar\"},\n" +
            "      \"conditions\": {\n" +
            "        \"start_date\": 1473233825,\n" +
            "        \"end_date\": null\n" +
            "      },\n" +
            "      \"features\": [\n" +
            "        {\"name\": \"pro\", \"ttl\": 86400}\n" +
            "      ],\n" +
            "      \"resources\": [\n" +
            "        {\"name\": \"gold\", \"quantity\": 170}\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"token\": \"c2a4fe03-9d82-4cd4-9658-fe95894ebdaa\",\n" +
            "      \"unlock_message\": \"Happy hour!\",\n" +
            "      \"conditions\": {\n" +
            "        \"start_date\": 1473233825,\n" +
            "        \"end_date\": 1473233826,\n" +
            "        \"only_new_users\": false,\n" +
            "        \"repeat\": {\n" +
            "          \"every\": \"week\",\n" +
            "          \"weekday\": \"friday\",\n" +
            "          \"start_hour\": 18,\n" +
            "          \"end_hour\": 20\n" +
            "        }\n" +
            "      },\n" +
            "      \"resources\": [\n" +
            "        {\"name\": \"gold\", \"quantity\": 20}\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    //endregion
}