package com.madfree.capstoneproject;

import timber.log.Timber;

public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
