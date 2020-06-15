package com.madfree.capstoneproject.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SubmitViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;

    public SubmitViewModelFactory(Application application) {
        this.mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SubmitViewModel(mApplication);
    }
}
