package com.madfree.capstoneproject.viewmodel;

import android.app.Application;
import android.content.Intent;

import com.madfree.capstoneproject.util.Constants;
import com.madfree.capstoneproject.worker.CollectTriviaDataWorker;
import com.madfree.capstoneproject.worker.SendTriviaToDbWorker;
import com.madfree.capstoneproject.worker.UploadImageWorker;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OverwritingInputMerger;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import timber.log.Timber;

public class SubmitViewModel extends ViewModel {

    private WorkManager mWorkManager;

    private Data mImageUploadInputData;

    public SubmitViewModel(@NonNull Application application) {
        mWorkManager = WorkManager.getInstance(application.getApplicationContext());
        Timber.d("Initialize SubmitViewModel");
    }

    public void setImageUploadInputData(Intent data) {
        mImageUploadInputData = new Data.Builder()
                .putString(Constants.KEY_IMAGE_URI, data.getData().toString())
                .build();
    }

    public void resetImageUploadInputData() {
        mImageUploadInputData = null;
        Timber.d("Reset image image data");
    }

    public LiveData<WorkInfo> submitTriviaToDb(String question, String answer, String wrong_answer_1, String wrong_answer_2, String wrong_answer_3,
                                 String selectedCategory, String selectedDifficulty) {

        Data triviaTextData = new Data.Builder()
                .putString(Constants.KEY_QUESTION_STRING, question)
                .putString(Constants.KEY_CORRECT_ANSWER_STRING, answer)
                .putString(Constants.KEY_WRONG_ANSWER_1_STRING, wrong_answer_1)
                .putString(Constants.KEY_WRONG_ANSWER_2_STRING, wrong_answer_2)
                .putString(Constants.KEY_WRONG_ANSWER_3_STRING, wrong_answer_3)
                .putString(Constants.KEY_CATEGORY_STRING, selectedCategory)
                .putString(Constants.KEY_DIFFICULTY_STRING, selectedDifficulty)
                .build();

        OneTimeWorkRequest collectTriviaDataRequest = new OneTimeWorkRequest.Builder(CollectTriviaDataWorker.class)
                .setInputData(triviaTextData)
                .build();
        Timber.d("Workmanager: setup collectTriviaData");

        OneTimeWorkRequest sendTriviaToDatabaseRequest = new OneTimeWorkRequest.Builder(SendTriviaToDbWorker.class)
                .setInputMerger(OverwritingInputMerger.class)
                .build();
        Timber.d("Workmanager: setup sendTriviaDataToDB");

        if (mImageUploadInputData == null) {
            mWorkManager.beginWith(collectTriviaDataRequest)
                    .then(sendTriviaToDatabaseRequest)
                    .enqueue();
            Timber.d("Workmanager: start chain without image");
        } else {
            OneTimeWorkRequest uploadImageRequest = new OneTimeWorkRequest.Builder(UploadImageWorker.class)
                    .setInputData(mImageUploadInputData)
                    .build();
            Timber.d("Workmanager: setup UploadImageRequest");

            mWorkManager.beginWith(Arrays.asList(uploadImageRequest, collectTriviaDataRequest))
                    .then(sendTriviaToDatabaseRequest)
                    .enqueue();
            Timber.d("Workmanager: start chain with image");
        }
        return mWorkManager.getWorkInfoByIdLiveData(sendTriviaToDatabaseRequest.getId());
    }
}
