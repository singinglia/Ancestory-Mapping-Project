package com.example.familymapclient2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.familymapclient2.caches.DataCache;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginSuccessListener {

    private LoginFragment loginFragment;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Login Fragment
        if(DataCache.getInstance().getAuthToken() == null) {
            FragmentManager fm = this.getSupportFragmentManager();
            loginFragment = (LoginFragment) fm.findFragmentById(R.id.mainFrameLayout);
            if (loginFragment == null) {
                loginFragment = createLoginFragment();
                fm.beginTransaction()
                    .add(R.id.mainFrameLayout, loginFragment)
                    .commit();
            }


        }
        else {
            //Map Fragment
            FragmentManager fm = this.getSupportFragmentManager();
            mapFragment = (MapFragment) fm.findFragmentById(R.id.mainFrameLayout);
            if (mapFragment == null) {
                mapFragment = createMapFragment();
                fm.beginTransaction()
                        .add(R.id.mainFrameLayout, mapFragment)
                        .commit();
            }


        }

    }

    @Override
    public void onBackPressed() {
        if (DataCache.getInstance().getAuthToken() == null) {
            //Do nothing
        } else {
            super.onBackPressed();
        }
    }


    private LoginFragment createLoginFragment(){
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private MapFragment createMapFragment(){
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSuccessfulLogin() {
        //Map Fragment
        FragmentManager fm = this.getSupportFragmentManager();
        if (mapFragment == null) {
            mapFragment = createMapFragment();
            fm.beginTransaction()
                    .replace(R.id.mainFrameLayout, mapFragment)
                    .commit();
        }
    }
}



