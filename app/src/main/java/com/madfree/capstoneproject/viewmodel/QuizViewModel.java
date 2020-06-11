package com.madfree.capstoneproject.viewmodel;

import android.os.CountDownTimer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.madfree.capstoneproject.data.FirebaseRepository;
import com.madfree.capstoneproject.data.Trivia;
import com.madfree.capstoneproject.data.User;
import com.madfree.capstoneproject.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class QuizViewModel extends ViewModel implements FirebaseRepository.OnFirebaseTaskComplete {

    private static final String TAG = QuizViewModel.class.getSimpleName();

    private MutableLiveData<List<Trivia>> mTriviaLiveData = new MutableLiveData<>();
    private List<Trivia> mTriviaList;

    private User mUser;

    private CountDownTimer countDownTimer;

    private int mCurrentTriviaNumber = 0;
    private int mTriviaDataListSize;

    // TODO: Variables need to get initialized with new MutableLiveData()
    private MutableLiveData<Integer> mTriviaCounter = new MutableLiveData<>();

    private MutableLiveData<Boolean> mQuizIsFinished = new MutableLiveData<>();
    private MutableLiveData<Long> mTimeLeftinMilisLiveData = new MutableLiveData<>();
    private Boolean isQuizFinished;
    private MutableLiveData<Boolean> hasTriviaLiveData = new MutableLiveData<>();
    private MutableLiveData<Trivia> triviaLiveData = new MutableLiveData<>();

    private FirebaseRepository mFirebaseRepository = new FirebaseRepository(this);

    private String mUid;
    private String mUserName;
    private String selectedCategory;
    private String selectedDifficulty;

    private int triviaNumber;
    private int mQuizScore;
    private int mTotalScore;
    private int mGamesPlayed;

    public QuizViewModel(String selectedCategory, String selectedDifficulty) {
        Timber.d("Initialize QuizViewModel");
        this.selectedDifficulty = selectedDifficulty;
        mFirebaseRepository.getQuizData(selectedCategory);

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
                mTriviaCounter.setValue(-1);
            }
        };
    }

    @Override
    public void QuizDataAdded(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
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
            mTriviaDataListSize = mTriviaList.size();
//            mTriviaLiveData.setValue(mTriviaList);
            incrementTriviaCount();
            mTriviaCounter.setValue(1);
            countDownTimer.start();
        }
    }

    public MutableLiveData<List<Trivia>> getTriviaLiveData() {
        return mTriviaLiveData;
    }

    public int getTriviaDataListSize() {
        return mTriviaDataListSize;
    }

    public void incrementTriviaCount() {
        if (mCurrentTriviaNumber < mTriviaDataListSize) {
            mCurrentTriviaNumber+=1;
            Timber.d("Set trivia counter to: %s", mCurrentTriviaNumber);
        } else {
            Timber.d("This was the last question: %s", mCurrentTriviaNumber);
            mCurrentTriviaNumber = -1;
        }
        mTriviaCounter.setValue(mCurrentTriviaNumber);
    }


    public MutableLiveData<Integer> getTriviaNumber() {
        Timber.d("Getting the current trivia number: %s", mCurrentTriviaNumber);
        return mTriviaCounter;
    }

    public Trivia getCurrentTrivia() {
        Trivia trivia = mTriviaList.get(mCurrentTriviaNumber-1);
        Timber.d("Getting the current trivia with question: %s", trivia.getQuestion());
        return trivia;
    }

    public void updateQuizScoreLiveData() {
        switch (selectedDifficulty) {
            case "Easy":
                mQuizScore += Constants.KEY_POINTS_DIFFICULTY_EASY;
                Timber.d("Add 10 points to score - now: %s", mQuizScore);
                break;
            case "Medium":
                mQuizScore += Constants.KEY_POINTS_DIFFICULTY_MEDIUM;
                Timber.d("Add 15 points to score - now: %s", mQuizScore);
                break;
            case "Hard":
                mQuizScore += Constants.KEY_POINTS_DIFFICULTY_HARD;
                Timber.d("Add 20 points to score - now: %s", mQuizScore);
                break;
        }
    }

    public int getQuizScore() {
        return mQuizScore;
    }

    public void setHighScore() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUid = firebaseAuth.getUid();

        Constants.USER_REF.child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //create new user
                    mUserName = firebaseAuth.getCurrentUser().getDisplayName();
                    mUser = new User(mUserName, 1, mQuizScore);
                    Constants.USER_REF.child(mUid).setValue(mUser);
                    Timber.d("Creating new user");
                } else {
                    // if user exists, get player info and update
                    Timber.d("User already exists, fetching current total score and games played");
                    mUser = dataSnapshot.getValue(User.class);
                    mTotalScore = mUser.getTotalScore();
                    mGamesPlayed = mUser.getGamesPlayed();
                    updateHighScore();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO: log error
                Timber.d("Firebase Database error: %s", databaseError);
            }
        });
    }

    private void updateHighScore() {
        mTotalScore = mTotalScore + mQuizScore;
        mGamesPlayed = mGamesPlayed +1;

        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.KEY_USER_GAMES_PLAYED, mGamesPlayed);
        result.put(Constants.KEY_USER_HIGH_SCORE, mTotalScore);
        Constants.USER_REF.child(mUid).updateChildren(result);
    }

    public void cancelCountDown() {
        Timber.d("Countdown timer canceled!");
        countDownTimer.cancel();
    }

    public LiveData<Long> getTimeLeftInMillisLiveData() {
        if (mTimeLeftinMilisLiveData == null) {
            mTimeLeftinMilisLiveData.setValue(0L);
        }
        return mTimeLeftinMilisLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        countDownTimer.cancel();
    }
}
