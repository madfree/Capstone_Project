package com.madfree.capstoneproject.util;

import com.google.firebase.BuildConfig;
import com.google.firebase.database.FirebaseDatabase;

import timber.log.Timber;

public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
