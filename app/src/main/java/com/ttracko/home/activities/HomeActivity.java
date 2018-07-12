package com.ttracko.home.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.ttracko.R;
import com.ttracko.home.fragments.DashboardFragment;
import com.ttracko.home.fragments.SignInFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by root on 26/6/18.
 */

public class HomeActivity extends AppCompatActivity {

    @InjectView(R.id.layoutContainer)
    FrameLayout layoutContainer;
    SignInFragment signInFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_toolbar, null);
        getSupportActionBar().setCustomView(view);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        ButterKnife.inject(this);
        _init();
    }

    private void _init() {
        signInFragment = new SignInFragment();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            getSupportFragmentManager().beginTransaction().replace(layoutContainer.getId(), new DashboardFragment()).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(layoutContainer.getId(), signInFragment).commit();
    }


}
