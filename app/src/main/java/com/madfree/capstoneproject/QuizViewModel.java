package com.madfree.capstoneproject;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class QuizViewModel extends ViewModel {

    private MutableLiveData<List<Trivia>> mTriviaList;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mTriviaDatabaseReference;

    public LiveData<List<Trivia>> getTrivia() {
        if (mTriviaList == null) {
            mTriviaList = new MutableLiveData<List<Trivia>>();
            loadTrivia();
        }
        return mTriviaList;
    }

    private void loadTrivia() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mTriviaDatabaseReference = mFirebaseDatabase.getReference().child("trivia");

        ValueEventListener mTriviaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Trivia> triviaList = new ArrayList<>();
                for (DataSnapshot triviaSnapshot : dataSnapshot.getChildren()) {
                    Trivia trivia = triviaSnapshot.getValue(Trivia.class);
                    triviaList.add(trivia);
                    Timber.d("This is the number of trivia in the db: " + triviaList.size());
                }
                mTriviaList.setValue(triviaList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e("onCancelled: " + databaseError.toException());
            }
        };
        mTriviaDatabaseReference.addValueEventListener(mTriviaListener);
    }
}
