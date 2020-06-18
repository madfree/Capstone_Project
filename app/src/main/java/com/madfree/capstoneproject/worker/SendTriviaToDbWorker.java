package com.madfree.capstoneproject.worker;

import android.content.Context;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madfree.capstoneproject.util.Constants;
import com.madfree.capstoneproject.data.Trivia;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class SendTriviaToDbWorker extends Worker {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mTriviaDatabaseReference;

    public SendTriviaToDbWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mTriviaDatabaseReference = mFirebaseDatabase.getReference().child("trivia");

        Data triviaData = getInputData();
        Timber.d("This is the data received: " + triviaData.getString(Constants.KEY_QUESTION_STRING) + triviaData.getString(Constants.KEY_FIREBASE_IMAGE_URL));

        Trivia mNewTrivia = new Trivia(
                            triviaData.getString(Constants.KEY_QUESTION_STRING),
                            triviaData.getString(Constants.KEY_CORRECT_ANSWER_STRING),
                            triviaData.getString(Constants.KEY_WRONG_ANSWER_1_STRING),
                            triviaData.getString(Constants.KEY_WRONG_ANSWER_2_STRING),
                            triviaData.getString(Constants.KEY_WRONG_ANSWER_3_STRING),
                            triviaData.getString(Constants.KEY_FIREBASE_IMAGE_URL),
                            triviaData.getString(Constants.KEY_CATEGORY_STRING),
                            triviaData.getString(Constants.KEY_DIFFICULTY_STRING)
                    );

        mTriviaDatabaseReference.push().setValue(mNewTrivia).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Timber.d("Trivia send successfully to Firebase Realtime Database");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "Failed to send trivia to database ");
            }
        });
        return Result.success();
    }
}
