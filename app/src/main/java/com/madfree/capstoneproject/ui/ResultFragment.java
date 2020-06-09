package com.madfree.capstoneproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.madfree.capstoneproject.R;
import com.madfree.capstoneproject.viewmodel.QuizViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import timber.log.Timber;

public class ResultFragment extends Fragment {

    private TextView mScoreTextView;
    private Button mHomeButton;

    private QuizViewModel quizViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Timber.d("onCreateView");
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_result, container, false);
        mScoreTextView = view.findViewById(R.id.text_view_score);
        mHomeButton = view.findViewById(R.id.button_home);

        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);
        Timber.d("Initialize QuizViewModel");

        //quizViewModel.setNewHighScore();

        quizViewModel.getQuizScoreLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mScoreTextView.setText(String.valueOf(integer));
            }
        });

        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, homeFragment);
                transaction.commit();
            }
        });

        return view;
    }
}
