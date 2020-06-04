package com.madfree.capstoneproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madfree.capstoneproject.R;
import com.madfree.capstoneproject.data.User;
import com.madfree.capstoneproject.viewmodel.QuizViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ResultFragment extends Fragment {

    private TextView mScoreTextView;

    private QuizViewModel quizViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Timber.d("QuizFragment onCreateView");

        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);
        Timber.d("Initialize QuizViewModel");

        View view = inflater.inflate(R.layout.fragment_result, container, false);
        mScoreTextView = view.findViewById(R.id.text_view_score);

        quizViewModel.getmQuizScoreLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mScoreTextView.setText(String.valueOf(integer));
                String category = quizViewModel.getSelectedCategory();
                Timber.d("ResultScreen: %s", category);
            }
        });

        return view;
    }
}
