package com.madfree.capstoneproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OverwritingInputMerger;
import androidx.work.WorkManager;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class SubmitFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String KEY_IMAGE_URI = "image_uri";
    private static final String KEY_QUESTION_STRING = "question_string";
    private static final String KEY_CORRECT_ANSWER_STRING = "correct_answer_string";
    private static final String KEY_WRONG_ANSWER_1_STRING = "wrong_answer_1_string";
    private static final String KEY_WRONG_ANSWER_2_STRING = "wrong_answer_2_string";
    private static final String KEY_WRONG_ANSWER_3_STRING = "wrong_answer_3_string";
    private static final String KEY_CATEGORY_STRING = "category_string";
    private static final String KEY_DIFFICULTY_STRING = "difficulty_string";

    private static final int DEFAULT_QUESTION_LENGTH = 300;
    private static final int DEFAULT_ANSWER_LENGTH = 30;
    private static final int RC_PHOTO_PICKER = 2;

    private String mSelectedCategory;
    private String mSelectedDifficulty;

    // TODO: Implement method to check if all fields are filled before submit button is activated
    private boolean isEveryFieldFilled;

    private EditText mQuestionEditText;
    private EditText mAnswerEditText;
    private EditText mWrong1EditText;
    private EditText mWrong2EditText;
    private EditText mWrong3EditText;

    private Spinner mCategorySpinner;
    private Spinner mDifficultySpinner;

    private ImageButton mPhotoPickerButton;
    private Button mSubmitButton;

    private Data mImageUploadInputData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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

        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // method to upload photo for a trivia question
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Pick an image"), RC_PHOTO_PICKER);
                Timber.d("Image was selected");
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Data triviaTextData = new Data.Builder()
                        .putString(KEY_QUESTION_STRING, mQuestionEditText.getText().toString())
                        .putString(KEY_CORRECT_ANSWER_STRING, mAnswerEditText.getText().toString())
                        .putString(KEY_WRONG_ANSWER_1_STRING, mWrong1EditText.getText().toString())
                        .putString(KEY_WRONG_ANSWER_2_STRING, mWrong2EditText.getText().toString())
                        .putString(KEY_WRONG_ANSWER_3_STRING, mWrong3EditText.getText().toString())
                        .putString(KEY_CATEGORY_STRING, mSelectedCategory)
                        .putString(KEY_DIFFICULTY_STRING, mSelectedDifficulty)
                        .build();

                OneTimeWorkRequest uploadImageRequest = new OneTimeWorkRequest.Builder(UploadImageWorker.class)
                        .setInputData(mImageUploadInputData)
                        .build();

                OneTimeWorkRequest collectTriviaDataRequest = new OneTimeWorkRequest.Builder(CollectTriviaDataWorker.class)
                        .setInputData(triviaTextData)
                        .build();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            mImageUploadInputData = new Data.Builder()
                    .putString(KEY_IMAGE_URI, data.getData().toString())
                    .build();
        }
    }
}
