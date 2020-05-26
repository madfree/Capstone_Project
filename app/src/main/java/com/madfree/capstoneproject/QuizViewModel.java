package com.madfree.capstoneproject;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class QuizViewModel extends ViewModel {
    private String selectedCategory;
    private String selectedDifficulty;

    private FirebaseQueryLiveData getTriviaData() {
        DatabaseReference TRIVIA_REF = FirebaseDatabase.getInstance().getReference().child("trivia");
        Query categoryQuery = TRIVIA_REF.orderByChild("category").equalTo(selectedCategory);
        Timber.d("Query Firebase with category: %s", selectedCategory);
        return new FirebaseQueryLiveData(categoryQuery);
    };

    @NonNull
    public LiveData<List<Trivia>> getTriviaLiveData() {
        FirebaseQueryLiveData liveData = getTriviaData();
        return Transformations.map(liveData, new Deserializer());
    }

    class Deserializer implements Function<DataSnapshot, List<Trivia>> {
        @Override
        public List<Trivia> apply(DataSnapshot dataSnapshot) {
            Timber.d("Receiving data snapshot with this number of children: %s", dataSnapshot.getChildrenCount());
            List<Trivia> triviaList = new ArrayList<>();
            Timber.d("Initializing the trivia list");
            for (DataSnapshot triviaSnapshot : dataSnapshot.getChildren()) {
                Trivia trivia = triviaSnapshot.getValue(Trivia.class);
                if (trivia.getDifficulty().equals(selectedDifficulty)) {
                    triviaList.add(trivia);
                    Timber.d("This is the question: %s", trivia.getQuestion());
                    Timber.d("This is the number of trivia: %s", triviaList.size());
                }
            }
            Timber.d("returning trivia list with items: %s", triviaList.size());
            return triviaList;
        }
    }

    public void setSelectedCategory(String selectedCategory) {
        Timber.d("Category delivered to ViewModel: %s", selectedCategory);
        this.selectedCategory = selectedCategory;
    }

    public void setSelectedDifficulty(String selectedDifficulty) {
        Timber.d("Difficulty delivered to ViewModel: %s", selectedDifficulty);
        this.selectedDifficulty = selectedDifficulty;
    }
}
