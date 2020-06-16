package com.madfree.capstoneproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.madfree.capstoneproject.util.Constants;
import com.madfree.capstoneproject.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

public class HomeFragment extends Fragment {

    private Button playButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        playButton = view.findViewById(R.id.button_play);

        Spinner categorySpinner = view.findViewById(R.id.spinner_category);
        ArrayAdapter categoryAdapter = ArrayAdapter.
                createFromResource(view.getContext(), R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        Spinner difficultySpinner = view.findViewById(R.id.spinner_difficulty);
        ArrayAdapter difficultyAdapter = ArrayAdapter.
                createFromResource(view.getContext(), R.array.difficulty_array, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getViewModelStore().clear();
                Fragment quizFragment = new QuizFragment();
                Bundle args = new Bundle();
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                String selectedDifficulty = difficultySpinner.getSelectedItem().toString();

                if (selectedCategory.equals("Random category")) {
                    String[] categories = getResources().getStringArray(R.array.category_array);
                    int maxNum = categories.length;
                    int minNum = 1;
                    int range = maxNum - minNum;
                    int randomNum = (int) (Math.random() * range) + minNum;
                    String randomCategory = categories[randomNum];
                    args.putString(Constants.KEY_CATEGORY_STRING, randomCategory);
                    Timber.d("Selected random category: %s", randomCategory);
                } else {
                    args.putString(Constants.KEY_CATEGORY_STRING, selectedCategory);
                }

                if (selectedDifficulty.equals("Random difficulty")) {
                    String[] difficulties = getResources().getStringArray(R.array.difficulty_array);
                    int maxNum = difficulties.length;
                    int minNum = 1;
                    int range = maxNum - minNum;
                    int randomNum = (int) (Math.random() * range) + minNum;
                    String randomDifficulty = difficulties[randomNum];
                    args.putString(Constants.KEY_DIFFICULTY_STRING, randomDifficulty);
                    Timber.d("Selected random difficulty: %s", randomDifficulty);
                } else {
                    args.putString(Constants.KEY_DIFFICULTY_STRING, selectedDifficulty);
                }
                quizFragment.setArguments(args);

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager()
                        .beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.fragment_container, quizFragment);
                transaction.addToBackStack(null).commit();
            }
        });

        return view;
    }
}
