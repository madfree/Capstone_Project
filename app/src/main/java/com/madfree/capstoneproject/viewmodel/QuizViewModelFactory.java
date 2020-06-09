package com.madfree.capstoneproject.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class QuizViewModelFactory implements ViewModelProvider.Factory {

    private String category;
    private String difficulty;

    public QuizViewModelFactory(String category, String difficulty) {
        this.category = category;
        this.difficulty = difficulty;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new QuizViewModel(category, difficulty);
    }
}
