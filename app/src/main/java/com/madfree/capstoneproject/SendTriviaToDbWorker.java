package com.madfree.capstoneproject;

import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class SendTriviaToDbWorker extends Worker {

    private static final String KEY_FIREBASE_IMAGE_URL = "firebase_image_url";
    private static final String KEY_QUESTION_STRING = "question_string";
    private static final String KEY_CORRECT_ANSWER_STRING = "correct_answer_string";
    private static final String KEY_WRONG_ANSWER_1_STRING = "wrong_answer_1_string";
    private static final String KEY_WRONG_ANSWER_2_STRING = "wrong_answer_2_string";
    private static final String KEY_WRONG_ANSWER_3_STRING = "wrong_answer_3_string";
    private static final String KEY_CATEGORY_STRING = "category_string";
    private static final String KEY_DIFFICULTY_STRING = "difficulty_string";

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
        Timber.d("This is the data received: " + triviaData.getString(KEY_QUESTION_STRING) + triviaData.getString(KEY_FIREBASE_IMAGE_URL));

        Trivia mNewTrivia = new Trivia(
                            triviaData.getString(KEY_QUESTION_STRING),
                            triviaData.getString(KEY_CORRECT_ANSWER_STRING),
                            triviaData.getString(KEY_WRONG_ANSWER_1_STRING),
                            triviaData.getString(KEY_WRONG_ANSWER_2_STRING),
                            triviaData.getString(KEY_WRONG_ANSWER_3_STRING),
                            triviaData.getString(KEY_FIREBASE_IMAGE_URL),
                            triviaData.getString(KEY_CATEGORY_STRING),
                            triviaData.getString(KEY_DIFFICULTY_STRING)
                    );

        mTriviaDatabaseReference.push().setValue(mNewTrivia).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Timber.d("Trivia send successfully to Firebase Realtime Database");
            }
        });
        return Result.success();
    }
}
