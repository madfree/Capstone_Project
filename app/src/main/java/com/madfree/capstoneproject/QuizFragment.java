package com.madfree.capstoneproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import timber.log.Timber;

public class QuizFragment extends Fragment implements View.OnClickListener {

    private ImageView question_image_view;

    private TextView questions_count;
    private TextView countdown_timer;
    private TextView question_text_view;
    private Button question_answer_1;
    private Button question_answer_2;
    private Button question_answer_3;
    private Button question_answer_4;

    private List<Trivia> mTriviaList = new ArrayList<>();
    private int triviaCountTotal;
    private int triviaCounter;
    private Trivia currentTrivia;

    private int score;

    private boolean isAnswered;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        questions_count = view.findViewById(R.id.text_view_question_count);
        countdown_timer = view.findViewById(R.id.text_view_timer_countdown);

        question_text_view = view.findViewById(R.id.text_view_question);
        question_image_view = view.findViewById(R.id.image_view_question);

        question_answer_1 = view.findViewById(R.id.button_answer_1);
        question_answer_2 = view.findViewById(R.id.button_answer_2);
        question_answer_3 = view.findViewById(R.id.button_answer_3);
        question_answer_4 = view.findViewById(R.id.button_answer_4);

        QuizViewModel quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        quizViewModel.getTrivia().observe(this, new Observer<List<Trivia>>() {
            @Override
            public void onChanged(List<Trivia> trivias) {
                mTriviaList.addAll(trivias);
                triviaCountTotal = mTriviaList.size();
                Timber.d("There are " + mTriviaList.size() + " trivia in the list.");
                Collections.shuffle(mTriviaList);
                showNextQuestion();
            }
        });
        return view;
    }

    private void showNextQuestion() {
        Timber.d("Getting the next question");

        if (triviaCounter < triviaCountTotal) {
            Timber.d("Current trivia number is: " + triviaCounter);
            currentTrivia = mTriviaList.get(triviaCounter);

            List<String> answerList = new ArrayList<>();
            answerList.add(currentTrivia.getAnswer());
            answerList.add(currentTrivia.getWrong_answer_1());
            answerList.add(currentTrivia.getWrong_answer_2());
            answerList.add(currentTrivia.getWrong_answer_3());
            Collections.shuffle(answerList);

            questions_count.setText(triviaCounter+1 + "/10");

            question_image_view.setVisibility(View.GONE);
            question_text_view.setText(currentTrivia.getQuestion());
            question_answer_1.setText(answerList.get(0));
            question_answer_2.setText(answerList.get(1));
            question_answer_3.setText(answerList.get(2));
            question_answer_4.setText(answerList.get(3));

            question_answer_1.setOnClickListener(this);
            question_answer_2.setOnClickListener(this);
            question_answer_3.setOnClickListener(this);
            question_answer_4.setOnClickListener(this);

            triviaCounter++;
            isAnswered = false;
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        // show result screen
    }

    @Override
    public void onClick(View view) {
        isAnswerCorrect(view);
        showNextQuestion();
    }

    private boolean isAnswerCorrect(View view) {
        isAnswered = true;

        Button selectedButton = (Button) view;
        String answerText = selectedButton.getText().toString();

        if (answerText.equals(currentTrivia.getAnswer())) {
            score+=10;
            Timber.d("Score is now: " + score);
            return true;
        } else {
            return false;
        }
    }
}
