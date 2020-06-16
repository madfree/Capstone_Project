package com.madfree.capstoneproject.viewmodel;

import com.google.firebase.database.DataSnapshot;
import com.madfree.capstoneproject.data.FirebaseRepository;
import com.madfree.capstoneproject.data.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class RankingViewModel extends ViewModel implements FirebaseRepository.OnFirebaseDownloadTaskComplete {

    private FirebaseRepository mFirebaseRepository = new FirebaseRepository(this);

    private MutableLiveData<List<User>> mUserLiveDataList = new MutableLiveData<>();
    private List<User> mUserList;

    public RankingViewModel() {
        mFirebaseRepository.getUserData();
    }

    @Override
    public void QuizDataAdded(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            mUserList = new ArrayList<>();
            Timber.d("Widget: Returning data from Firebase");
            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                User user = userSnapshot.getValue(User.class);
                mUserList.add(user);
                Timber.d("Widget: Add user to list: %s", user.getUserName());
            }
            Collections.reverse(mUserList);
            Timber.d("Widget: Return user list with size: %s", mUserList.size());
        }
        mUserLiveDataList.setValue(mUserList);
    }

    public LiveData<List<User>> getUserList() {
        return mUserLiveDataList;
    }
}
