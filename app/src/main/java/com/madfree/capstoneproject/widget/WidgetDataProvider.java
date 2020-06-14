package com.madfree.capstoneproject.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madfree.capstoneproject.R;
import com.madfree.capstoneproject.data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class WidgetDataProvider implements RemoteViewsFactory {

    private Context context;
    private Intent intent;

    private FirebaseDatabase db;
    private FirebaseAuth mFirebaseAuth;

    private List<User> mUserList;
    private CountDownLatch countDown;


    public WidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    public void fetchData() {
        countDown = new CountDownLatch(1);
        try {
            DatabaseReference userRef = db.getReference("users");
            Query rankingQuery = userRef.orderByChild("totalScore");
            rankingQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUserList = new ArrayList<>();
                    Timber.d("Widget: Returning data from Firebase");
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        mUserList.add(user);
                        Timber.d("Widget: Add user to list: %s", user.getUserName());

                    }
                    Timber.d("Widget: Return user list with size: %s", mUserList.size());
                    countDown.countDown();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Timber.e("Firebase loading problem: %s", databaseError);
                }
            });
            Timber.d("All data loaded successfully");
            countDown.await();
        } catch (Throwable throwable) {
            Timber.e(throwable, "Error loading data");
        }
    }

    @Override
    public void onCreate() {
        Timber.d("Widget onCreate");
        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
    }

    @Override
    public void onDataSetChanged() {
        Timber.d("Widget onDataSetChanged");
        fetchData();
    }

    @Override
    public void onDestroy() {
        Timber.d("Widget onDestroy");
    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("Widget: Setting up the views in the list");
        Timber.d("Set user at position %s", position);
        User user = mUserList.get(position);

        CharSequence name = user.getUserName();
        CharSequence score = (CharSequence) String.valueOf(user.getTotalScore());
        CharSequence gamesPlayed = (CharSequence) String.valueOf(user.getGamesPlayed());
        CharSequence rank = (CharSequence) String.valueOf(position + 1);

        Timber.d("position: %s", rank);
        Timber.d("name: %s", name);
        Timber.d("score: %s", score);
        Timber.d("games played: %s", gamesPlayed);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rank).append("\t");
        stringBuilder.append(name).append("\t\t");
        stringBuilder.append(score).append("\t");

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        view.setTextViewText(R.id.widget_player_info_text_view, stringBuilder);

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
