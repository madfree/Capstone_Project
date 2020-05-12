package com.madfree.capstoneproject;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SubmitFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "SubmitFragment";

    public static final int DEFAULT_QUESTION_LENGTH = 300;
    public static final int DEFAULT_ANSWER_LENGTH = 30;

    private String mSelectedCategory;
    private String mSelectedDifficulty;
    private String mImageUrl;

    private EditText mQuestionEditText;
    private EditText mAnswerEditText;
    private EditText mWrong1EditText;
    private EditText mWrong2EditText;
    private EditText mWrong3EditText;

    private Spinner mCategorySpinner;
    private Spinner mDifficultySpinner;

    private ImageButton mPhotoPickerButton;
    private Button mSubmitButton;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mTriviaDatabaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mTriviaDatabaseReference = mFirebaseDatabase.getReference().child("trivia");

        View view = inflater.inflate(R.layout.fragment_submit, container, false);

        mQuestionEditText = view.findViewById(R.id.edit_text_new_question);
        mAnswerEditText = view.findViewById(R.id.edit_text_correct_answer);
        mWrong1EditText = view.findViewById(R.id.edit_text_wrong_answer_1);
        mWrong2EditText = view.findViewById(R.id.edit_text_wrong_answer_2);
        mWrong3EditText = view.findViewById(R.id.edit_text_wrong_answer_3);
        mPhotoPickerButton = view.findViewById(R.id.photoPickerButton);
        mCategorySpinner = view.findViewById(R.id.spinner_category);
        mDifficultySpinner = view.findViewById(R.id.spinner_difficulty);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.
                createFromResource(view.getContext(), R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(categoryAdapter);
        mCategorySpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.
                createFromResource(view.getContext(), R.array.difficulty_array, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDifficultySpinner.setAdapter(difficultyAdapter);
        mDifficultySpinner.setOnItemSelectedListener(this);

        mQuestionEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_QUESTION_LENGTH)});
        mAnswerEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_ANSWER_LENGTH)});
        mWrong1EditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_ANSWER_LENGTH)});
        mWrong2EditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_ANSWER_LENGTH)});
        mWrong3EditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_ANSWER_LENGTH)});

        mSubmitButton = view.findViewById(R.id.button_submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trivia mNewTrivia = new Trivia(
                    mQuestionEditText.getText().toString(),
                    mAnswerEditText.getText().toString(),
                    mWrong1EditText.getText().toString(),
                    mWrong2EditText.getText().toString(),
                    mWrong3EditText.getText().toString(),
                    mImageUrl,
                    mSelectedCategory,
                    mSelectedDifficulty
                );
                mTriviaDatabaseReference.push().setValue(mNewTrivia);
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(adapterView.getId() == R.id.spinner_category)
        {
            mSelectedCategory = adapterView.getItemAtPosition(position).toString();
        }
        else if(adapterView.getId() == R.id.spinner_difficulty)
        {
            mSelectedDifficulty= adapterView.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Throw error message when clicking submit button
        Toast.makeText(getContext(), "Please select a category and/or difficulty", Toast.LENGTH_SHORT).show();
    }
}
