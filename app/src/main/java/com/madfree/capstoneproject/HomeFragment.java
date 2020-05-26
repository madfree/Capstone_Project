package com.madfree.capstoneproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    private Button playButton;
    private String selectedCategory;
    private String selectedDifficulty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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
                Fragment quizFragment = new QuizFragment();
                selectedCategory = categorySpinner.getSelectedItem().toString();
                selectedDifficulty = difficultySpinner.getSelectedItem().toString();

                Bundle args = new Bundle();
                args.putString(Constants.KEY_CATEGORY_STRING, selectedCategory);
                args.putString(Constants.KEY_DIFFICULTY_STRING, selectedDifficulty);
                quizFragment.setArguments(args);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, quizFragment);
                transaction.commit();
            }
        });

        return view;
    }
}
