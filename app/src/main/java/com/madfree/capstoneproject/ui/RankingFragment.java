package com.madfree.capstoneproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madfree.capstoneproject.R;
import com.madfree.capstoneproject.data.User;
import com.madfree.capstoneproject.viewmodel.RankingViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class RankingFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private RankingViewModel rankingViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        rankingViewModel = new ViewModelProvider(requireActivity()).get(RankingViewModel.class);
        Timber.d("Initialize QuizViewModel");

        mRecyclerView = view.findViewById(R.id.recycler_view);

        rankingViewModel.getUserRankLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                layoutManager = new LinearLayoutManager(requireActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                mAdapter = new RankAdapter(users);
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        return view;
    }


}
