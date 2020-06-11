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
    private int mAppWidgetId;

    private FirebaseDatabase db;
    private FirebaseAuth mFirebaseAuth;

    private List<User> mUserList;
    private CountDownLatch countDown;


    public WidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
//        this.mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID);
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
        Timber.d("Set user at position " + mUserList.get(position));
        User user = mUserList.get(position);
        CharSequence rank = String.valueOf(position+1);
        CharSequence name = user.getUserName();
        CharSequence score = String.valueOf(user.getTotalScore());
        CharSequence gamesPlayed = String.valueOf(user.getGamesPlayed());

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        view.setTextViewText(R.id.widget_player_rank_text_view, rank);
        Timber.d("position: " + rank);

        view.setTextViewText(R.id.widget_player_name_text_view, name);
        Timber.d("name: %s", name);
        view.setTextViewText(R.id.score_text_view, score);
        Timber.d("score: %s", score);
        view.setTextViewText(R.id.games_played_text_view, gamesPlayed);
        Timber.d("games played: %s", gamesPlayed);
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
