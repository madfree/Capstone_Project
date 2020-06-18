package com.madfree.capstoneproject.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;

import timber.log.Timber;

public class FirebaseRepository {

    private OnFirebaseDownloadTaskComplete onFirebaseTaskComplete;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private DatabaseReference triviaReference = firebaseDatabase.getReference().child(
            "trivia");
    private static final DatabaseReference userReference =
            FirebaseDatabase.getInstance().getReference("users");

    public FirebaseRepository(OnFirebaseDownloadTaskComplete onFirebaseTaskComplete) {
        this.onFirebaseTaskComplete = onFirebaseTaskComplete;
    }

    public void getQuizData(String category) {
        Query categoryQuery = triviaReference.orderByChild("category").equalTo(category);
        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onFirebaseTaskComplete.QuizDataAdded(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e("Firebase loading problem: %s", databaseError);
            }
        });
    }

    public void getUserRankingData() {
        Query userQuery = userReference.orderByChild("totalScore").limitToLast(100);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onFirebaseTaskComplete.QuizDataAdded(dataSnapshot);
                Timber.d("Returning player data from Firebase");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e("Firebase loading problem: %s", databaseError);
            }
        });
    }

    public interface OnFirebaseDownloadTaskComplete {
        void QuizDataAdded(DataSnapshot dataSnapshot);
    }

}
