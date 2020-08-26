package com.example.familymapclient2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class EventActivity extends AppCompatActivity {

    Fragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        String eventToPass = intent.getStringExtra("eventID");

        //Map Fragment
        FragmentManager fm = this.getSupportFragmentManager();
        mapFragment = fm.findFragmentById(R.id.eventFrameLayout);
        if (mapFragment == null) {
            mapFragment = createMapFragment(eventToPass);
            fm.beginTransaction()
                    .add(R.id.eventFrameLayout, mapFragment)
                    .commit();
        }
    }

    private MapFragment createMapFragment(String eventID){
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString("eventID", eventID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}
