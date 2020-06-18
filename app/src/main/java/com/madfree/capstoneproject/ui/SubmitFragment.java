package com.madfree.capstoneproject.ui;

import android.app.Application;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.madfree.capstoneproject.util.Constants;
import com.madfree.capstoneproject.R;
import com.madfree.capstoneproject.viewmodel.SubmitViewModel;
import com.madfree.capstoneproject.viewmodel.SubmitViewModelFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class SubmitFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private SubmitViewModel submitViewModel;
    private Application mApplication;

    private String mSelectedCategory;
    private String mSelectedDifficulty;

    private TextView mNewTriviaTitleTextView;
    private EditText mQuestionEditText;
    private EditText mAnswerEditText;
    private EditText mWrong1EditText;
    private EditText mWrong2EditText;
    private EditText mWrong3EditText;
    private TextView mImageHint;
    private TextView mUploadSuccessTextView;
    private ImageView mCheckSuccessImageView;
    private ImageButton mPhotoPickerButton;
    private Button mSubmitButton;
    private Button mNewTriviaButton;

    private Spinner mCategorySpinner;
    private Spinner mDifficultySpinner;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.fragment_submit, container, false);

        mNewTriviaTitleTextView = view.findViewById(R.id.text_view_new_trivia_title);
        mQuestionEditText = view.findViewById(R.id.edit_text_new_question);
        mAnswerEditText = view.findViewById(R.id.edit_text_correct_answer);
        mWrong1EditText = view.findViewById(R.id.edit_text_wrong_answer_1);
        mWrong2EditText = view.findViewById(R.id.edit_text_wrong_answer_2);
        mWrong3EditText = view.findViewById(R.id.edit_text_wrong_answer_3);
        mPhotoPickerButton = view.findViewById(R.id.photoPickerButton);
        mCategorySpinner = view.findViewById(R.id.spinner_category);
        mDifficultySpinner = view.findViewById(R.id.spinner_difficulty);
        mImageHint = view.findViewById(R.id.text_view_image_hint);

        mUploadSuccessTextView = view.findViewById(R.id.upload_success_text_view);
        mCheckSuccessImageView = view.findViewById(R.id.upload_success_image_view);
        mNewTriviaButton = view.findViewById(R.id.new_trivia_button);

        mUploadSuccessTextView.setVisibility(View.GONE);
        mCheckSuccessImageView.setVisibility(View.GONE);
        mNewTriviaButton.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

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

        mQuestionEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.DEFAULT_QUESTION_LENGTH)});
        mAnswerEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.DEFAULT_ANSWER_LENGTH)});
        mWrong1EditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.DEFAULT_ANSWER_LENGTH)});
        mWrong2EditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.DEFAULT_ANSWER_LENGTH)});
        mWrong3EditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.DEFAULT_ANSWER_LENGTH)});
        mSubmitButton = view.findViewById(R.id.button_submit);

        mApplication = getActivity().getApplication();
        submitViewModel = new ViewModelProvider(requireActivity(), new SubmitViewModelFactory(mApplication)).get(SubmitViewModel.class);

        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // method to upload photo for a trivia question
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Pick an image"), Constants.RC_PHOTO_PICKER);
                Timber.d("Image was selected");
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (mSelectedCategory.equals(Constants.KEY_CATEGORY_RANDOM) || mSelectedDifficulty.equals(Constants.KEY_DIFFICULTY_RANDOM) ||
                        mQuestionEditText.getText().toString().equals("") || mAnswerEditText.getText().toString().equals("") ||
                        mWrong1EditText.getText().toString().equals("") || mWrong2EditText.getText().toString().equals("") ||
                        mWrong3EditText.getText().toString().equals(""))
                {
                    Toast.makeText(requireContext(), "Please fill all fields and select a category and difficulty", Toast.LENGTH_LONG).show();
                } else {
                    String question = mQuestionEditText.getText().toString();
                    String answer = mAnswerEditText.getText().toString();
                    String wrong_answer_1 = mWrong1EditText.getText().toString();
                    String wrong_answer_2 = mWrong2EditText.getText().toString();
                    String wrong_answer_3 = mWrong3EditText.getText().toString();
                    submitViewModel.submitTriviaToDb(question, answer, wrong_answer_1, wrong_answer_2, wrong_answer_3, mSelectedCategory, mSelectedDifficulty)
                            .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
                                @Override
                                public void onChanged(WorkInfo workInfo) {
                                    // Check if the current work's state is "successfully finished"
                                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                        progressBar.setVisibility(View.GONE);
                                        showSuccessUI();
                                    }
                                }
                            });
                }
            }
        });

        mNewTriviaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInitialUI();
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(adapterView.getId() == R.id.spinner_category) { mSelectedCategory = adapterView.getItemAtPosition(position).toString(); }
        else if(adapterView.getId() == R.id.spinner_difficulty) { mSelectedDifficulty= adapterView.getItemAtPosition(position).toString(); }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Throw error message when clicking submit button
        Toast.makeText(getContext(), "Please select a category and/or difficulty", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            submitViewModel.setImageUploadInputData(data);
            mPhotoPickerButton.setBackground(getResources().getDrawable(R.drawable.ic_check_green));
        }
    }

    private void clearAll() {
        mQuestionEditText.getText().clear();
        mAnswerEditText.getText().clear();
        mWrong1EditText.getText().clear();
        mWrong2EditText.getText().clear();
        mWrong3EditText.getText().clear();
        mCategorySpinner.setSelection(0);
        mDifficultySpinner.setSelection(0);
        mPhotoPickerButton.setBackground(getResources().getDrawable(R.drawable.ic_add_image));
    }

    private void showSuccessUI() {
        mUploadSuccessTextView.setVisibility(View.VISIBLE);
        mCheckSuccessImageView.setVisibility(View.VISIBLE);
        mNewTriviaButton.setVisibility(View.VISIBLE);

        mNewTriviaTitleTextView.setVisibility(View.GONE);
        mQuestionEditText.setVisibility(View.GONE);
        mAnswerEditText.setVisibility(View.GONE);
        mWrong1EditText.setVisibility(View.GONE);
        mWrong2EditText.setVisibility(View.GONE);
        mWrong3EditText.setVisibility(View.GONE);
        mPhotoPickerButton.setVisibility(View.GONE);
        mCategorySpinner.setVisibility(View.GONE);
        mDifficultySpinner.setVisibility(View.GONE);
        mImageHint.setVisibility(View.GONE);
        mSubmitButton.setVisibility(View.GONE);
        clearAll();
        submitViewModel.resetImageUploadInputData();
    }

    private void showInitialUI() {
        mUploadSuccessTextView.setVisibility(View.GONE);
        mCheckSuccessImageView.setVisibility(View.GONE);
        mNewTriviaButton.setVisibility(View.GONE);

        mNewTriviaTitleTextView.setVisibility(View.VISIBLE);
        mQuestionEditText.setVisibility(View.VISIBLE);
        mAnswerEditText.setVisibility(View.VISIBLE);
        mWrong1EditText.setVisibility(View.VISIBLE);
        mWrong2EditText.setVisibility(View.VISIBLE);
        mWrong3EditText.setVisibility(View.VISIBLE);
        mPhotoPickerButton.setVisibility(View.VISIBLE);
        mCategorySpinner.setVisibility(View.VISIBLE);
        mDifficultySpinner.setVisibility(View.VISIBLE);
        mImageHint.setVisibility(View.VISIBLE);
        mSubmitButton.setVisibility(View.VISIBLE);
    }
}
