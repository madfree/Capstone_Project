package com.madfree.capstoneproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class CollectTriviaDataWorker extends Worker {

    private static final String KEY_QUESTION_STRING = "question_string";
    private static final String KEY_CORRECT_ANSWER_STRING = "correct_answer_string";

    public CollectTriviaDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data triviaData = getInputData();
        Timber.d("This is the data received: " + triviaData.getString(KEY_QUESTION_STRING)
                + triviaData.getString(KEY_CORRECT_ANSWER_STRING));

        Timber.d("Forwarding all trivia text data");
        return Result.success(triviaData);
    }
}
