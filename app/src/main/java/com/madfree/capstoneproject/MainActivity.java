package com.madfree.capstoneproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import timber.log.Timber;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.madfree.capstoneproject.ui.HomeFragment;
import com.madfree.capstoneproject.ui.SubmitFragment;
import com.madfree.capstoneproject.util.Constants;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String mUserName;
    private String mUserEmail;
    private Uri mUserImageUrl;

    // Firebase Instance Variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DrawerLayout drawer;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
    private ImageView mUserImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderLayout = navigationView.getHeaderView(0);
        mUserNameTextView = navHeaderLayout.findViewById(R.id.user_name_text_view);
        mUserEmailTextView = navHeaderLayout.findViewById(R.id.user_email_text_view);
        mUserImageView = navHeaderLayout.findViewById(R.id.user_image_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
               R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    // the user is signed in
                    mUserImageUrl = user.getPhotoUrl();
                    Glide.with(MainActivity.this)
                            .load(mUserImageUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(mUserImageView);
                    mUserName = user.getDisplayName();
                    mUserNameTextView.setText(mUserName);
                    mUserEmail = user.getEmail();
                    mUserEmailTextView.setText(mUserEmail);
                } else {
                    // user is not signed in
                    mUserName = Constants.ANONYMOUS;
                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(true)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()))
                            .build(),
                            Constants.RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                Timber.d("Sign in successful");
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in cancelled!", Toast.LENGTH_SHORT).show();
                Timber.d("Sign in cancelled");
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("MainActivity onResume");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.d("MainActivity onPause");
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_submit:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SubmitFragment()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
