package com.madfree.capstoneproject.viewmodel;

import android.os.CountDownTimer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madfree.capstoneproject.data.Trivia;
import com.madfree.capstoneproject.data.FirebaseQueryLiveData;
import com.madfree.capstoneproject.data.User;
import com.madfree.capstoneproject.util.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class QuizViewModel extends ViewModel {

    private LiveData<List<Trivia>> mTriviaLiveData;
    private List<Trivia> mTriviaList;

    private CountDownTimer countDownTimer;

    private MutableLiveData<Integer> counterLiveData;
    private MutableLiveData<Integer> mTriviaDataListSize;

    // TODO: Variables need to get initialized with new MutableLiveData()
    private MutableLiveData<Integer> mQuizScoreLiveData;
    private MutableLiveData<Boolean> mQuizIsFinished;
    private MutableLiveData<Long> mTimeLeftinMilisLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isQuizFinished;

    private String mUid;
    private String mUserName;
    private String selectedCategory;
    private String selectedDifficulty;

    private int triviaNumber;
    private int mQuizScore;
    private int mTotalScore;
    private int mGamesPlayed;

    public QuizViewModel() {
        // initialize variables with new MutableLiveData()
        mQuizScoreLiveData = new MutableLiveData<>();
        Timber.d("Reset score");
        mQuizIsFinished = new MutableLiveData<>();
        Timber.d("Reset counter");
        isQuizFinished = new MutableLiveData<>();

        startCountDownTimer();
    }

    // Initial calls to database
    @NonNull
    public MutableLiveData<Integer> getTriviaListSize() {
        FirebaseQueryLiveData liveData = getTriviaData();
        mTriviaLiveData = Transformations.map(liveData, new Deserializer());
        mTriviaDataListSize = new MutableLiveData<>();
        mTriviaDataListSize.setValue(mTriviaList.size());
        return mTriviaDataListSize;
    }

    @NonNull
    public LiveData<List<Trivia>> getTriviaLiveData() {
        FirebaseQueryLiveData liveData = getTriviaData();
        mTriviaLiveData = Transformations.map(liveData, new Deserializer());
        return mTriviaLiveData;
    }

    private FirebaseQueryLiveData getTriviaData() {
        DatabaseReference TRIVIA_REF = FirebaseDatabase.getInstance().getReference().child(
                "trivia");
        Query categoryQuery = TRIVIA_REF.orderByChild("category").equalTo(selectedCategory);
        Timber.d("Query Firebase with category: %s", selectedCategory);
        return new FirebaseQueryLiveData(categoryQuery);
    }

    public void startCountDownTimer() {
        countDownTimer = new CountDownTimer(Constants.COUNTDOWN_IN_MILLIS, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftinMilisLiveData.setValue(millisUntilFinished);
                Timber.d("CountDownTimer onTick(");
            }

            @Override
            public void onFinish() {
                mTimeLeftinMilisLiveData.setValue(0L);
                Timber.d("CountDownTimer onFinish()");
                incrementCountLiveData();
            }
        }.start();
    }

    public void cancelCountDownTimer() {
        countDownTimer.cancel();
    }

    public void setTimeLeftInMiLlisLiveData(long time) {
        mTimeLeftinMilisLiveData.setValue(time);
    }

    public LiveData<Long> getTimeLeftInMillisLiveData() {
        return mTimeLeftinMilisLiveData;
    }

    public MutableLiveData<Long> finishCountdown() {
        mTimeLeftinMilisLiveData.setValue(null);
        countDownTimer.onFinish();
        return mTimeLeftinMilisLiveData;
    }

    public MutableLiveData<Long> cancelCountDown() {
        Timber.d("Cancel CountDownTimer");
        countDownTimer.cancel();
        return mTimeLeftinMilisLiveData;
    }

    public void setNewHighScore() {
        mTotalScore = mTotalScore + mQuizScore;
        mGamesPlayed = mGamesPlayed + 1;
        Constants.USER_REF.child(mUid).child(Constants.KEY_USER_HIGH_SCORE).setValue(mQuizScore);
        Constants.USER_REF.child(mUid).child(Constants.KEY_USER_GAMES_PLAYED).setValue(mGamesPlayed);
    }

    private class Deserializer implements Function<DataSnapshot, List<Trivia>> {
        @Override
        public List<Trivia> apply(DataSnapshot dataSnapshot) {
            if (mTriviaList == null) {
                mTriviaList = new ArrayList<>();
                Timber.d("Receiving data snapshot with this number of children: %s",
                        dataSnapshot.getChildrenCount());
                Timber.d("Initializing the trivia list");
                for (DataSnapshot triviaSnapshot : dataSnapshot.getChildren()) {
                    Trivia trivia = triviaSnapshot.getValue(Trivia.class);
                    if (trivia.getDifficulty().equals(selectedDifficulty)) {
                        mTriviaList.add(trivia);
                        Timber.d("This is the question: %s", trivia.getQuestion());
                        Timber.d("This is the number of trivia: %s", mTriviaList.size());
                    }
                }
                Timber.d("returning trivia list with items: %s", mTriviaList.size());
            }
            return mTriviaList;
        }
    }

    public MutableLiveData<Boolean> canIncrementCountLiveData() {
        int triviaNumber = counterLiveData.getValue() - 1;
        if (triviaNumber <= mTriviaList.size()) {
            Timber.d("To the next trivia: %s", triviaNumber);
            mQuizIsFinished.setValue(false);
        } else {
            Timber.d("Quiz is finished");
            mQuizIsFinished.setValue(true);
        }
        return mQuizIsFinished;
    }

    public MutableLiveData<Integer> incrementCountLiveData() {
        int number = counterLiveData.getValue();
        Timber.d("counter from MutableLiveData is: " + number);
        number++;
        Timber.d("Increment counter to: %s", number);
        counterLiveData.setValue(number);
        return counterLiveData;
    }

    public MutableLiveData<Integer> getCounterLiveData() {
        Timber.d("Get number for the counter");
        if (counterLiveData == null) {
            counterLiveData = new MutableLiveData<>();
            counterLiveData.setValue(0);
        }
        return counterLiveData;
    }

    public MutableLiveData<Boolean> getmQuizIsFinished() {
        if (isQuizFinished == null) {
            isQuizFinished = new MutableLiveData<>();
        }
        int number = counterLiveData.getValue();
        if (number < mTriviaList.size()) {
            Timber.d("MutableLiveData is now: " + counterLiveData.getValue());
            isQuizFinished.setValue(false);
        } else {
            isQuizFinished.setValue(true);
        }
        return isQuizFinished;
    }

    public MutableLiveData<Trivia> getCurrentTrivia() {
        MutableLiveData<Trivia> triviaLiveData = new MutableLiveData<>();
        int number = counterLiveData.getValue() - 1;

        Trivia trivia = mTriviaLiveData.getValue().get(number);
        triviaLiveData.setValue(trivia);
        return triviaLiveData;
    }


    public void setSelectedCategory(String selectedCategory) {
        Timber.d("Category delivered to ViewModel: %s", selectedCategory);
        this.selectedCategory = selectedCategory;
    }

    public void setSelectedDifficulty(String selectedDifficulty) {
        Timber.d("Difficulty delivered to ViewModel: %s", selectedDifficulty);
        this.selectedDifficulty = selectedDifficulty;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public String getSelectedDifficulty() {
        return selectedDifficulty;
    }

    // methods to deal with quiz score
    public MutableLiveData<Integer> getQuizScoreLiveData() {
        if (mQuizScoreLiveData == null) {
            mQuizScoreLiveData = new MutableLiveData<>();
            mQuizScoreLiveData.setValue(0);
            Timber.d("Init quiz score with 0");
        }
        return mQuizScoreLiveData;
    }

    public void updateQuizScoreLiveData() {
        if (mQuizScoreLiveData == null) {
            mQuizScoreLiveData = new MutableLiveData<>();
        }
        switch (selectedDifficulty) {
            case "Easy":
                mQuizScore += Constants.KEY_POINTS_DIFFICULTY_EASY;
                mQuizScoreLiveData.setValue(mQuizScore);
                Timber.d("Add 10 points to score - now: %s", mQuizScore);
                break;
            case "Medium":
                mQuizScore += Constants.KEY_POINTS_DIFFICULTY_MEDIUM;
                mQuizScoreLiveData.setValue(mQuizScore);
                Timber.d("Add 15 points to score - now: %s", mQuizScore);
                break;
            case "Hard":
                mQuizScore += Constants.KEY_POINTS_DIFFICULTY_HARD;
                mQuizScoreLiveData.setValue(mQuizScore);
                Timber.d("Add 20 points to score - now: %s", mQuizScore);
                break;
        }
    }

    // fetch user info from Firebase Auth
    public void getUserInfo() {
        mUid = FirebaseAuth.getInstance().getUid();
        Constants.USER_REF.child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //create new user
                    User newUser = new User(mUserName, 0, 0);
                    Constants.USER_REF.child(mUid).setValue(newUser);
                    Timber.d("Creating new user");
                } else {
                    // if user exists, get the info
                    Timber.d("User already exists, fetching current total score and games played");
                    User userData = dataSnapshot.getValue(User.class);
                    mTotalScore = userData.getTotalScore();
                    mGamesPlayed = userData.getTotalScore();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO: log error
                Timber.d("Firebase Database error: %s", databaseError);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        countDownTimer.cancel();
    }
}
