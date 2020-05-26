package com.madfree.capstoneproject;

import android.os.Handler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class FirebaseQueryLiveData extends MutableLiveData<List<Trivia>> {
    // Code was taken from this Firebase tutorial: https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private boolean listenerRemovePending = false;
    private final Handler handler = new Handler();
    private MutableLiveData<List<Trivia>> mTriviaList = new MutableLiveData<>();
    private String selectedDifficulty;

    public FirebaseQueryLiveData(Query query, String difficulty) {
        this.query = query;
        this.selectedDifficulty = difficulty;
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
            List<Trivia> triviaList = new ArrayList<>();
                for (DataSnapshot triviaSnapshot : dataSnapshot.getChildren()) {
                    Trivia trivia = triviaSnapshot.getValue(Trivia.class);
                    if (trivia.getDifficulty().equals(selectedDifficulty))
                        triviaList.add(trivia);
                    Timber.d("This is the question: %s", trivia.getQuestion());
                    Timber.d("This is the number of trivia: %s", triviaList.size());
                }
                mTriviaList.setValue(triviaList);
            Timber.d("Successfully delivered data from Firebase");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e(databaseError.toException(), "Can't listen to query %s", query);
        }
    }
}
