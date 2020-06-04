package com.madfree.capstoneproject.viewmodel;

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
    private MutableLiveData<Integer> mTriviaCountLiveData;
    private MutableLiveData<Integer> mQuizScoreLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mQuizIsFinished = new MutableLiveData<>();
    private int mQuizScore;
    private List<Trivia> mTriviaList;
    private int triviaNumber;
    private String selectedCategory;
    private String selectedDifficulty;
    private String mUid;
    private String mUserName;
    private int mTotalScore;
    private int mGamesPlayed;

    private FirebaseQueryLiveData getTriviaData() {
        DatabaseReference TRIVIA_REF = FirebaseDatabase.getInstance().getReference().child(
                "trivia");
        Query categoryQuery = TRIVIA_REF.orderByChild("category").equalTo(selectedCategory);
        Timber.d("Query Firebase with category: %s", selectedCategory);
        return new FirebaseQueryLiveData(categoryQuery);
    }

    @NonNull
    public LiveData<List<Trivia>> getTriviaLiveData() {
        FirebaseQueryLiveData liveData = getTriviaData();
        mTriviaLiveData = Transformations.map(liveData, new Deserializer());
        return mTriviaLiveData;
    }

    public void getUserInfo() {
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
            }
        });
    }

    public void setNewHighScore() {
        mTotalScore = mTotalScore + mQuizScore;
        mGamesPlayed = mGamesPlayed+1;
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

    public LiveData<Integer> getCountLiveData() {
        Timber.d("Getting the current trivia number");
        if (mTriviaCountLiveData == null) {
            mTriviaCountLiveData = new MutableLiveData<>(0);
        }
        return mTriviaCountLiveData;
    }

    public MutableLiveData<Boolean> canIncrementCountLiveData() {
        triviaNumber = mTriviaCountLiveData.getValue();
        Timber.d("number from MutableLiveData is: %s", triviaNumber);
        if (triviaNumber < mTriviaList.size() - 1) {
            triviaNumber++;
            Timber.d("Increment number count to: %s", triviaNumber);
            mTriviaCountLiveData.setValue(triviaNumber);
            Timber.d("MutableLiveData is now: %s", mTriviaCountLiveData.getValue());
            mQuizIsFinished.setValue(false);
        } else {
            mQuizIsFinished.setValue(true);
        }
        return mQuizIsFinished;
    }

    public MutableLiveData<Integer> getmTriviaCountLiveData() {
        return mTriviaCountLiveData;
    }

    public Trivia getCurrentTrivia() {
        return mTriviaList.get(triviaNumber);
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

    public MutableLiveData<Integer> getmQuizScoreLiveData() {
        return mQuizScoreLiveData;
    }

    public void updatemQuizeScoreLiveData() {
        mQuizScore += 10;
        mQuizScoreLiveData.setValue(mQuizScore);
        Timber.d("Score in QuizViewModel is now: " + mQuizScore);
    }

    public void setmUser(String Uid, String name) {
        this.mUid = Uid;
        this.mUserName = name;
    }
}
