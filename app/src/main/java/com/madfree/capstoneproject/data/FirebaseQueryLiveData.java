package com.madfree.capstoneproject.data;

import android.os.Handler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.lifecycle.LiveData;
import timber.log.Timber;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    // Code was taken from this Firebase tutorial: https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private boolean listenerRemovePending = false;
    private final Handler handler = new Handler();

    public FirebaseQueryLiveData(Query query) {
        this.query = query;
        Timber.d("Accessing Firebase with query");
    }

    // not needed so far
    public FirebaseQueryLiveData(DatabaseReference ref) {
        Timber.d("Accessing Firebase with reference");
        this.query = ref;
    }

    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            query.removeEventListener(listener);
            listenerRemovePending = false;
        }
    };

    @Override
    protected void onActive() {
        Timber.d("onActive");
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        }
        else {
            query.addValueEventListener(listener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        Timber.d("onInActive");
        // Listener removal is schedule on a two second delay
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
            Timber.d("Deliver data snapshot from Firebase");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e(databaseError.toException(), "Can't listen to query %s", query);
        }
    }
}
