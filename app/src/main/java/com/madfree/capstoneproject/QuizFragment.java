package com.madfree.capstoneproject;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
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
    private List<Trivia> mTriviaList = new ArrayList<>();
    private int triviaCountTotal;
    private int triviaCounter;
    private Trivia currentTrivia;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private String selectedCategory;
    private String selectedDifficulty;
    private int score;

    private boolean isAnswered;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        selectedCategory = getArguments().getString(Constants.KEY_CATEGORY_STRING);
        quizViewModel.setSelectedCategory(selectedCategory);
        Timber.d("This is the category: %s", selectedCategory);
        selectedDifficulty = getArguments().getString(Constants.KEY_DIFFICULTY_STRING);
        quizViewModel.setSelectedDifficulty(selectedDifficulty);
        Timber.d("This is the category: %s", selectedDifficulty);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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

        quizViewModel.getTriviaLiveData().observe(this, new Observer<List<Trivia>>() {
            @Override
            public void onChanged(List<Trivia> trivias) {
                Timber.d("Get size of trivia list from ViewModel: %s", trivias.size());
                questions_count_text_view_total.setText(String.valueOf(trivias.size()));
            }
        });

        quizViewModel.getCountLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Timber.d("Get number from ViewModel: " + integer);
                String number = integer.toString();
                questions_count_text_view.setText(number);
            }
        });


//        LiveData<Trivia> triviaLiveData = quizViewModel.getmCurrentTriviaItem(triviaCounter);
//        triviaLiveData.observe(getViewLifecycleOwner(), new Observer<Trivia>() {
//            @Override
//            public void onChanged(Trivia trivia) {
//                updateUi(trivia);
//            }
//
//        });

        return view;
    }

    private void updateUi(Trivia currentTrivia) {
        triviaCounter++;

        Timber.d("Current trivia number is: " + triviaCounter);

        List<String> answerList = new ArrayList<>();
        answerList.add(currentTrivia.getAnswer());
        answerList.add(currentTrivia.getWrong_answer_1());
        answerList.add(currentTrivia.getWrong_answer_2());
        answerList.add(currentTrivia.getWrong_answer_3());
        Collections.shuffle(answerList);

        questions_count_text_view.setText(triviaCounter + "/" + mTriviaList.size());

        question_image_view.setVisibility(View.GONE);
        question_text_view.setText(currentTrivia.getQuestion());
        question_answer_1.setText(answerList.get(0));
        question_answer_2.setText(answerList.get(1));
        question_answer_3.setText(answerList.get(2));
        question_answer_4.setText(answerList.get(3));
        timeLeftInMillis = Constants.COUNTDOWN_IN_MILLIS;
        startCountDown();
        isAnswered = false;
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
                finishQuiz();
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
        Toast.makeText(getContext(), "Quiz is finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
//        isAnswerCorrect(view);
        quizViewModel.incrementCountLiveData();

//        if (triviaCounter < triviaCountTotal) {
//            Timber.d("Getting the next question");
//        } else {
//            finishQuiz();
//        }
    }

    private boolean isAnswerCorrect(View view) {
        isAnswered = true;

        Button selectedButton = (Button) view;
        String answerText = selectedButton.getText().toString();

        if (answerText.equals(currentTrivia.getAnswer())) {
            score += 10;
            Timber.d("Score is now: " + score);
            return true;
        } else {
            return false;
        }
    }
}
