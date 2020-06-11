package com.madfree.capstoneproject.util;

import timber.log.Timber;

public class MyApplication extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
