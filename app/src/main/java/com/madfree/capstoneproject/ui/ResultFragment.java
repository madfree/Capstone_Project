package com.madfree.capstoneproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.madfree.capstoneproject.R;
import com.madfree.capstoneproject.viewmodel.QuizViewModel;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import timber.log.Timber;

public class ResultFragment extends Fragment {

    private TextView mScoreTextView;
    private TextView mCategoryTextView;
    private TextView mDifficultyTextView;
    private Button mHomeButton;

    private QuizViewModel quizViewModel;
    private int mQuizScore;
    private String mQuizCategory;
    private String mQuizDifficulty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("ResultFragment onCreate");
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                returnHome();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Timber.d("onCreateView");
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_result, container, false);
        mScoreTextView = view.findViewById(R.id.text_view_score);
        mCategoryTextView = view.findViewById(R.id.category_text_view);
        mDifficultyTextView = view.findViewById(R.id.difficulty_text_view);
        mHomeButton = view.findViewById(R.id.button_home);

        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        quizViewModel.setHighScore();

        mQuizScore = quizViewModel.getQuizScore();
        mQuizCategory = quizViewModel.getSelectedCategory();
        mQuizDifficulty = quizViewModel.getSelectedDifficulty();

        mScoreTextView.setText(String.valueOf(mQuizScore));
        mCategoryTextView.setText(mQuizCategory);
        mDifficultyTextView.setText(mQuizDifficulty);

        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnHome();
            }
        });

        return view;
    }

    private void returnHome() {
        requireActivity().getViewModelStore().clear();
        Fragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.commit();
    }
}
