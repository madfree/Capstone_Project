package com.madfree.capstoneproject.viewmodel;

import com.google.firebase.database.DataSnapshot;
import com.madfree.capstoneproject.data.FirebaseQueryLiveData;
import com.madfree.capstoneproject.data.User;
import com.madfree.capstoneproject.util.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class RankingViewModel extends ViewModel {

    private LiveData<List<User>> mUserLiveData;
    private List<User> mUserList;

    public LiveData<List<User>> getUserRankLiveData() {
        // fetch all users
        FirebaseQueryLiveData userListLiveData = new FirebaseQueryLiveData(Constants.USER_REF);
        mUserLiveData = Transformations.map(userListLiveData, new UserDeserializer());
        return mUserLiveData;
    }

    private class UserDeserializer implements Function<DataSnapshot, List<User>> {
        @Override
        public List<User> apply(DataSnapshot dataSnapshot) {
            if (mUserList == null) {
                mUserList = new ArrayList<>();
                Timber.d("Receiving data snapshot with this number of children: %s",
                        dataSnapshot.getChildrenCount());
                Timber.d("Initializing the user list");
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    mUserList.add(user);
                }
                Timber.d("returning user list with items: %s", mUserList.size());
            }
            return mUserList;
        }
    }
}
