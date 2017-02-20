package com.appgratis.unlock.sample.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Locale;

public class ResourceManager {

    private static final String SHARED_PREFS_NAME = "sample_resources";

    private Context context;

    public ResourceManager(@NonNull Context context) {
        this.context = context;
    }

    public long get(@NonNull String resourceName) {
        return readQuantity(resourceName);
    }

    public void add(@NonNull String resourceName, long quantity) {
        persist(resourceName, get(resourceName) + quantity);
    }

    private void persist(@NonNull String resourceName, long quantity) {
        getSharedPreferences().edit().putLong(resourceName.toUpperCase(Locale.US), quantity).apply();
    }

    private long readQuantity(@NonNull String resourceName) {
        return getSharedPreferences().getLong(resourceName.toUpperCase(Locale.US), 0);
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }
}
