package com.madfree.capstoneproject.worker;

import android.content.Context;

import com.madfree.capstoneproject.util.Constants;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class CollectTriviaDataWorker extends Worker {

    public CollectTriviaDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data triviaData = getInputData();
        Timber.d("This is the data received: " + triviaData.getString(Constants.KEY_QUESTION_STRING)
                + triviaData.getString(Constants.KEY_CORRECT_ANSWER_STRING));

        Timber.d("Forwarding all trivia text data");
        return Result.success(triviaData);
    }
}
