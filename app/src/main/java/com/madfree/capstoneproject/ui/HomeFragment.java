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
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                String selectedDifficulty = difficultySpinner.getSelectedItem().toString();

                Bundle args = new Bundle();
                args.putString(Constants.KEY_CATEGORY_STRING, selectedCategory);
                args.putString(Constants.KEY_DIFFICULTY_STRING, selectedDifficulty);
                quizFragment.setArguments(args);

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, quizFragment);
                transaction.addToBackStack(null).commit();
            }
        });

        return view;
    }
}
