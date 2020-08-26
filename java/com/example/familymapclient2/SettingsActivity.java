package com.example.familymapclient2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.familymapclient2.caches.DataCache;
import com.example.familymapclient2.caches.SettingsCache;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.title_activity_settings);
        setChecks();
        getSettings();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("updated", true);
            startActivity(intent);
        }
        return true;
    }

    public void setChecks(){
        Switch storyLineSwitch = (Switch) findViewById(R.id.storyLineSwitch);
        storyLineSwitch.setChecked(SettingsCache.getInstance().ShowLifeStoryLines());

        Switch familyLineSwitch = (Switch) findViewById(R.id.familyLineSwitch);
        familyLineSwitch.setChecked(SettingsCache.getInstance().ShowFamilyTreeLines());

        Switch spouseLineSwitch = (Switch) findViewById(R.id.spouseLineSwitch);
        spouseLineSwitch.setChecked(SettingsCache.getInstance().ShowSpouseLines());

        Switch fatherSideSwitch = (Switch) findViewById(R.id.fatherSideSwitch);
        fatherSideSwitch.setChecked(SettingsCache.getInstance().isVisibleFatherSide());

        Switch motherSideSwitch = (Switch) findViewById(R.id.motherSideSwitch);
        motherSideSwitch.setChecked(SettingsCache.getInstance().isVisibleMotherSide());

        Switch maleSwitch = (Switch) findViewById(R.id.maleSwitch);
        maleSwitch.setChecked(SettingsCache.getInstance().isVisibleMales());

        Switch femaleSwitch = (Switch) findViewById(R.id.femaleSwitch);
        femaleSwitch.setChecked(SettingsCache.getInstance().isVisibleFemales());


    }

    private void getSettings(){
        Switch storyLineSwitch = (Switch) findViewById(R.id.storyLineSwitch);
        storyLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsCache.getInstance().addLineChange();
                if(isChecked){
                    SettingsCache.getInstance().setShowLifeStoryLines(true);
                } else{
                    SettingsCache.getInstance().setShowLifeStoryLines(false);
                }
            }
        });

        Switch familyLineSwitch = (Switch) findViewById(R.id.familyLineSwitch);
        familyLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsCache.getInstance().addLineChange();
                if(isChecked){
                    SettingsCache.getInstance().setShowFamilyTreeLines(true);
                } else{
                    SettingsCache.getInstance().setShowFamilyTreeLines(false);
                }
            }
        });

        Switch spouseLineSwitch = (Switch) findViewById(R.id.spouseLineSwitch);
        spouseLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsCache.getInstance().addLineChange();
                if(isChecked){
                    SettingsCache.getInstance().setShowSpouseLines(true);
                } else{
                    SettingsCache.getInstance().setShowSpouseLines(false);
                }
            }
        });

        Switch fatherSideSwitch = (Switch) findViewById(R.id.fatherSideSwitch);
        fatherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsCache.getInstance().addChange();
                if(isChecked){
                    SettingsCache.getInstance().setVisibleFatherSide(true);
                } else{
                    SettingsCache.getInstance().setVisibleFatherSide(false);
                }
            }
        });

        Switch motherSideSwitch = (Switch) findViewById(R.id.motherSideSwitch);
        motherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsCache.getInstance().addChange();
                if(isChecked){
                    SettingsCache.getInstance().setVisibleMotherSide(true);
                } else{
                    SettingsCache.getInstance().setVisibleMotherSide(false);
                }
            }
        });

        Switch maleSwitch = (Switch) findViewById(R.id.maleSwitch);
        maleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsCache.getInstance().addChange();
                if(isChecked){
                    SettingsCache.getInstance().setVisibleMales(true);
                } else{
                    SettingsCache.getInstance().setVisibleMales(false);
                }
            }
        });

        Switch femaleSwitch = (Switch) findViewById(R.id.femaleSwitch);
        femaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsCache.getInstance().addChange();
                if(isChecked){
                    SettingsCache.getInstance().setVisibleFemales(true);
                } else{
                    SettingsCache.getInstance().setVisibleFemales(false);
                }
            }
        });

        TableRow logOutRow = (TableRow) findViewById(R.id.logOutSection);
        logOutRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache.getInstance().setAuthToken(null);
                DataCache.getInstance().clearData();
                SettingsCache.getInstance().toDefault();
                startMain();
            }
        });

    }

    private void startMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
