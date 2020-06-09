package com.madfree.capstoneproject.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.madfree.capstoneproject.util.Constants;
import com.madfree.capstoneproject.R;
import com.madfree.capstoneproject.data.Trivia;
import com.madfree.capstoneproject.viewmodel.QuizViewModel;
import com.madfree.capstoneproject.viewmodel.QuizViewModelFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import timber.log.Timber;

public class QuizFragment extends Fragment implements View.OnClickListener {

    private ProgressBar progressBar;
    private ImageView question_image_view;

    private TextView questions_count_text_view;
    private TextView separator_text_view;
    private TextView questions_count_text_view_total;
    private ImageView clock_image_view;
    private TextView countdown_timer_text_view;
    private TextView question_text_view;
    private Button question_answer_1;
    private Button question_answer_2;
    private Button question_answer_3;
    private Button question_answer_4;

    private QuizViewModel quizViewModel;

    private List<Trivia> mTriviaList;
    private Trivia currentTrivia;
    private boolean isQuizFinished;

    private int mTriviaCount;
    private int mTriviaTotalCount;

    private CountDownTimer countDownTimer;
    private long mTimeLeft;

    private String selectedCategory;
    private String selectedDifficulty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("QuizFragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Timber.d("QuizFragment onCreateView");
//        requireActivity().getActionBar().hide();

        selectedCategory = getArguments().getString(Constants.KEY_CATEGORY_STRING);
        Timber.d("This is the category: %s", selectedCategory);
        selectedDifficulty = getArguments().getString(Constants.KEY_DIFFICULTY_STRING);
        Timber.d("This is the category: %s", selectedDifficulty);

        quizViewModel = new ViewModelProvider(requireActivity(), new QuizViewModelFactory(selectedCategory, selectedDifficulty)).get(QuizViewModel.class);

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        questions_count_text_view = view.findViewById(R.id.text_view_question_count);
        separator_text_view = view.findViewById(R.id.text_view_separator);
        questions_count_text_view_total = view.findViewById(R.id.text_view_question_count_total);

        clock_image_view = view.findViewById(R.id.icon_clock);
        countdown_timer_text_view = view.findViewById(R.id.text_view_timer_countdown);

        question_text_view = view.findViewById(R.id.text_view_question);
        question_image_view = view.findViewById(R.id.image_view_question);

        question_answer_1 = view.findViewById(R.id.button_answer_1);
        question_answer_2 = view.findViewById(R.id.button_answer_2);
        question_answer_3 = view.findViewById(R.id.button_answer_3);
        question_answer_4 = view.findViewById(R.id.button_answer_4);

        question_answer_1.setOnClickListener(this);
        question_answer_2.setOnClickListener(this);
        question_answer_3.setOnClickListener(this);
        question_answer_4.setOnClickListener(this);

        disableUI();

        quizViewModel.getTriviaNumber().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == -1) {
                    Timber.d("Quiz finished: %s", integer);
                    quizViewModel.cancelCountDown();
                    showResult();
                } else {
                    isQuizFinished = false;
                    Timber.d("Receiving the new trivia number from ViewModel: %s", integer);
                    mTriviaCount = integer;
                    questions_count_text_view.setText(String.valueOf(mTriviaCount));
                    showNextTrivia();
                }
            }
        });

        quizViewModel.getTimeLeftInMillisLiveData().observe(this,
                new Observer<Long>() {
                    @Override
                    public void onChanged(Long aLong) {
                        if (aLong != null) {
                            updateCountDownText(aLong);
                        } else {
                            updateCountDownText(0);
                        }
                    }
                });
        return view;
    }

    private void showNextTrivia() {
        currentTrivia = quizViewModel.getCurrentTrivia();
        List<String> answerList = new ArrayList<>();
        answerList.add(currentTrivia.getAnswer());
        answerList.add(currentTrivia.getWrong_answer_1());
        answerList.add(currentTrivia.getWrong_answer_2());
        answerList.add(currentTrivia.getWrong_answer_3());
        Collections.shuffle(answerList);

        question_image_view.setVisibility(View.GONE);
        question_text_view.setText(currentTrivia.getQuestion());
        question_answer_1.setText(answerList.get(0));
        question_answer_2.setText(answerList.get(1));
        question_answer_3.setText(answerList.get(2));
        question_answer_4.setText(answerList.get(3));
        showTriviaUI();
    }

    @Override
    public void onClick(View view) {
        checkAnswer(view);
    }

    private void checkAnswer(View view) {
        if (view != null) {
            Button selectedButton = (Button) view;
            String answerText = selectedButton.getText().toString();
            if (answerText.equals(currentTrivia.getAnswer())) {
                quizViewModel.updateQuizScoreLiveData();
                Timber.d("Updating score");
                selectedButton.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            } else {
                selectedButton.setBackgroundColor(getResources().getColor(R.color.colorRed));
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Timber.d("isQuizFinished is: %s", isQuizFinished);
                        resetButtonColor();
                        quizViewModel.incrementTriviaCount();
                }
            }, 500);   //1 second
        }
    }

    public void resetButtonColor() {
        question_answer_1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        question_answer_2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        question_answer_3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        question_answer_4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void showTriviaUI() {
        int listSize = quizViewModel.getTriviaDataListSize();
        questions_count_text_view_total.setText(String.valueOf(listSize));
        questions_count_text_view.setVisibility(View.VISIBLE);
        separator_text_view.setVisibility(View.VISIBLE);
        questions_count_text_view_total.setVisibility(View.VISIBLE);

        clock_image_view.setVisibility(View.VISIBLE);
        countdown_timer_text_view.setVisibility(View.VISIBLE);

        question_text_view.setVisibility(View.VISIBLE);
        question_image_view.setVisibility(View.VISIBLE);

        question_answer_1.setVisibility(View.VISIBLE);
        question_answer_2.setVisibility(View.VISIBLE);
        question_answer_3.setVisibility(View.VISIBLE);
        question_answer_4.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void disableUI() {
        questions_count_text_view.setVisibility(View.INVISIBLE);
        separator_text_view.setVisibility(View.INVISIBLE);
        questions_count_text_view_total.setVisibility(View.INVISIBLE);

        clock_image_view.setVisibility(View.INVISIBLE);
        countdown_timer_text_view.setVisibility(View.INVISIBLE);

        question_text_view.setVisibility(View.INVISIBLE);
        question_image_view.setVisibility(View.INVISIBLE);

        question_answer_1.setVisibility(View.INVISIBLE);
        question_answer_2.setVisibility(View.INVISIBLE);
        question_answer_3.setVisibility(View.INVISIBLE);
        question_answer_4.setVisibility(View.INVISIBLE);
    }

    private void updateCountDownText(long timeLeftInMillis) {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        countdown_timer_text_view.setText(timeFormatted);

        if (timeLeftInMillis < 10000) {
            countdown_timer_text_view.setTextColor(Color.RED);
        } else {
            countdown_timer_text_view.setTextColor(Color.BLACK);
        }
    }

    private void showResult() {
        Fragment resultFragment = new ResultFragment();
        FragmentTransaction transaction =
                getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, resultFragment);
        transaction.commit();
    }

}
