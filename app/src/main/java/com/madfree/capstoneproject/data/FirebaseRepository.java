package com.madfree.capstoneproject.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import timber.log.Timber;

public class FirebaseRepository {

    private OnFirebaseTaskComplete onFirebaseTaskComplete;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private DatabaseReference triviaReference = firebaseDatabase.getReference().child(
            "trivia");
    public static final DatabaseReference userReference =
            FirebaseDatabase.getInstance().getReference("users");

    public FirebaseRepository(OnFirebaseTaskComplete onFirebaseTaskComplete) {
        this.onFirebaseTaskComplete = onFirebaseTaskComplete;
    }

    public void getQuizData(String category) {
        Query categoryQuery = triviaReference.orderByChild("category").equalTo(category);
        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onFirebaseTaskComplete.QuizListDataAdded(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e("Firebase loading problem: %s", databaseError);
            }
        });
    }

    public void getUserData() {

    }

    public interface OnFirebaseTaskComplete {
        void QuizListDataAdded(DataSnapshot dataSnapshot);
    }

}
