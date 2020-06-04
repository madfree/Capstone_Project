package com.madfree.capstoneproject.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madfree.capstoneproject.R;
import com.madfree.capstoneproject.data.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {

    private List<User> userData;

    public RankAdapter(List<User> data) {
        userData = data;
    }

    public static class RankViewHolder extends RecyclerView.ViewHolder {

        private TextView playerName;
        private TextView totalScore;
        private TextView gamesPlayed;

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.player_name_text_view);
            totalScore = itemView.findViewById(R.id.score_text_view);
            gamesPlayed = itemView.findViewById(R.id.games_played_text_view);
        }
    }

    @NonNull
    @Override
    public RankAdapter.RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rankView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, parent, false);
        RankViewHolder rankViewHolder = new RankViewHolder(rankView);
        return rankViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        holder.playerName.setText(userData.get(position).getUserName());
        holder.totalScore.setText(String.valueOf(userData.get(position).getTotalScore()));
        holder.gamesPlayed.setText(String.valueOf(userData.get(position).getGamesPlayed()));
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }
}
