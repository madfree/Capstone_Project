package com.madfree.capstoneproject.viewmodel;

import android.app.Application;
import android.content.Intent;

import com.madfree.capstoneproject.data.FirebaseRepository;
import com.madfree.capstoneproject.data.Trivia;
import com.madfree.capstoneproject.util.Constants;
import com.madfree.capstoneproject.worker.CollectTriviaDataWorker;
import com.madfree.capstoneproject.worker.SendTriviaToDbWorker;
import com.madfree.capstoneproject.worker.UploadImageWorker;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OverwritingInputMerger;
import androidx.work.WorkManager;
import timber.log.Timber;

public class SubmitViewModel extends ViewModel implements FirebaseRepository.OnTriviaUploadTaskComplete {

    private FirebaseRepository mFirebaseRepository = new FirebaseRepository(this);
    private WorkManager mWorkManager;

    private MutableLiveData<Boolean> mIsUploadComplete = new MutableLiveData<>();

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

    public Data getImageUploadInputData() {
        return mImageUploadInputData;
    }

    public void submitTriviaToDb(String question, String answer, String wrong_answer_1, String wrong_answer_2, String wrong_answer_3,
                                 String selectedCategory, String selectedDifficulty) {
        if (mImageUploadInputData != null) {
            Data triviaTextData = new Data.Builder()
                    .putString(Constants.KEY_QUESTION_STRING, question)
                    .putString(Constants.KEY_CORRECT_ANSWER_STRING, answer)
                    .putString(Constants.KEY_WRONG_ANSWER_1_STRING, wrong_answer_1)
                    .putString(Constants.KEY_WRONG_ANSWER_2_STRING, wrong_answer_2)
                    .putString(Constants.KEY_WRONG_ANSWER_3_STRING, wrong_answer_3)
                    .putString(Constants.KEY_CATEGORY_STRING, selectedCategory)
                    .putString(Constants.KEY_DIFFICULTY_STRING, selectedDifficulty)
                    .build();

            OneTimeWorkRequest uploadImageRequest = new OneTimeWorkRequest.Builder(UploadImageWorker.class)
                    .setInputData(mImageUploadInputData)
                    .build();

            OneTimeWorkRequest collectTriviaDataRequest = new OneTimeWorkRequest.Builder(CollectTriviaDataWorker.class)
                    .setInputData(triviaTextData)
                    .build();

            OneTimeWorkRequest sendTriviaToDatabaseRequest = new OneTimeWorkRequest.Builder(SendTriviaToDbWorker.class)
                    .setInputMerger(OverwritingInputMerger.class)
                    .build();

            mWorkManager.beginWith(Arrays.asList(uploadImageRequest, collectTriviaDataRequest))
                    .then(sendTriviaToDatabaseRequest)
                    .enqueue();
        } else {
            // push trivia without image to Firebase Realtime database
            Trivia newTrivia = new Trivia(question, answer, wrong_answer_1, wrong_answer_2, wrong_answer_3, selectedCategory, selectedDifficulty);
            mFirebaseRepository.addTriviaToDb(newTrivia);
        }
    }

    public MutableLiveData<Boolean> getIsUploadComplete() {
        Timber.d("TriviaAdded: Returning success for trivia upload");
        return mIsUploadComplete;

    }

    @Override
    public void TriviaAdded(boolean isSuccessful) {
        Timber.d("TriviaAdded success");
        this.mIsUploadComplete.setValue(isSuccessful);
    }

    public void resetIsUploadComplete() {
        mIsUploadComplete = new MutableLiveData<>();
    }
}
