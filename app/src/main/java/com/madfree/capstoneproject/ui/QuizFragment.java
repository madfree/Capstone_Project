package com.madfree.capstoneproject.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.madfree.capstoneproject.util.Constants;
import com.madfree.capstoneproject.viewmodel.QuizViewModel;
import com.madfree.capstoneproject.R;
import com.madfree.capstoneproject.data.Trivia;

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

    private ImageView question_image_view;

    private TextView questions_count_text_view;
    private TextView questions_count_text_view_total;
    private TextView countdown_timer_text_view;
    private TextView question_text_view;
    private Button question_answer_1;
    private Button question_answer_2;
    private Button question_answer_3;
    private Button question_answer_4;

    private QuizViewModel quizViewModel;
    private Trivia currentTrivia;

    private int mTriviaQuizSize;
    private int mCurrentTriviaNumber;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

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

        quizViewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);
        Timber.d("Initialize QuizViewModel");
        selectedCategory = getArguments().getString(Constants.KEY_CATEGORY_STRING);
        quizViewModel.setSelectedCategory(selectedCategory);
        Timber.d("This is the category: %s", selectedCategory);
        selectedDifficulty = getArguments().getString(Constants.KEY_DIFFICULTY_STRING);
        quizViewModel.setSelectedDifficulty(selectedDifficulty);
        Timber.d("This is the category: %s", selectedDifficulty);

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        questions_count_text_view = view.findViewById(R.id.text_view_question_count);
        questions_count_text_view_total = view.findViewById(R.id.text_view_question_count_total);
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

        if (savedInstanceState == null) {
            quizViewModel.getTriviaLiveData().observe(this, new Observer<List<Trivia>>() {
                @Override
                public void onChanged(List<Trivia> trivias) {
                    Timber.d("Get size of trivia list from ViewModel: %s", trivias.size());
                    mTriviaQuizSize = trivias.size();
                    questions_count_text_view_total.setText(String.valueOf(mTriviaQuizSize));
                    updateUi();
                }
            });
        } else {
            timeLeftInMillis = savedInstanceState.getLong(Constants.KEY_MILLIS_LEFT);
            Timber.d("Restoring state " + timeLeftInMillis);
            currentTrivia = quizViewModel.getCurrentTrivia();
            startCountDown();
        }

        quizViewModel.getCountLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Timber.d("Get number from ViewModel: %s", integer);
                questions_count_text_view.setText(String.valueOf(integer+1));
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d("Saving state " + timeLeftInMillis);
        outState.putLong(Constants.KEY_MILLIS_LEFT, timeLeftInMillis);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            Timber.d("Cancel the countdown timer");
        }
    }

    private void updateUi() {
        Timber.d("Setting up UI");
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
        timeLeftInMillis = Constants.COUNTDOWN_IN_MILLIS;
        startCountDown();
        Timber.d("Initialize count down timer");
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer(null);
            }
        }.start();
    }

    private void updateCountDownText() {
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

    private void finishQuiz() {
        // show result screen
        Toast.makeText(requireView().getContext(), "Quiz is finished", Toast.LENGTH_SHORT).show();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new ResultFragment()).commit();
    }

    @Override
    public void onClick(View view) {
        countDownTimer.cancel();
        updateCountDownText();
        checkAnswer(view);
    }

    private void checkAnswer(View view) {
        if (view != null) {
            Button selectedButton = (Button) view;
            String answerText = selectedButton.getText().toString();

            if (answerText.equals(currentTrivia.getAnswer())) {
                quizViewModel.updatemQuizeScoreLiveData();
                Timber.d("Updating score");
            }
        }
        quizViewModel.canIncrementCountLiveData().observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean quizIsFinished) {
                if (quizIsFinished) {
                    finishQuiz();
                } else {
                    updateUi();
                }
            }
        });
    }
}
