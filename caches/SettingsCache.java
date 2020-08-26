package com.example.familymapclient2.caches;

public class SettingsCache {

    private static SettingsCache instance;

    private SettingsCache(){

    }

    public static SettingsCache getInstance(){
        if (instance == null){
            instance = new SettingsCache();
        }
        return instance;
    }

    public void toDefault(){
        showSpouseLines = true;
        showFamilyTreeLines = true;
        showLifeStoryLines = true;
        isVisibleFatherSide = true;
        isVisibleMotherSide = true;
        isVisibleMales = true;
        isVisibleFemales = true;
        markerSettingChangeCount = 0;
    }

    private int markerSettingChangeCount = 0;
    private int lineSettingChangeCount = 0;


    public void addLineChange(){
        lineSettingChangeCount++;
    }

    public int getLineChangeCount(){
        return lineSettingChangeCount;
    }



    public void addChange(){
        markerSettingChangeCount++;
    }

    public int getMarkerChangeCount(){
        return markerSettingChangeCount;
    }



    private boolean showSpouseLines = true;
    private boolean showFamilyTreeLines = true;
    private boolean showLifeStoryLines = true;


    private boolean isVisibleFatherSide = true;
    private boolean isVisibleMotherSide = true;
    private boolean isVisibleMales = true;
    private boolean isVisibleFemales = true;

    public boolean ShowSpouseLines() {
        return showSpouseLines;
    }

    public void setShowSpouseLines(boolean showSpouseLines) {
        this.showSpouseLines = showSpouseLines;
    }

    public boolean ShowFamilyTreeLines() {
        return showFamilyTreeLines;
    }

    public void setShowFamilyTreeLines(boolean showFamilyTreeLines) {
        this.showFamilyTreeLines = showFamilyTreeLines;
    }

    public boolean ShowLifeStoryLines() {
        return showLifeStoryLines;
    }

    public void setShowLifeStoryLines(boolean showLifeStoryLines) {
        this.showLifeStoryLines = showLifeStoryLines;
    }

    public boolean isVisibleFatherSide() {
        return isVisibleFatherSide;
    }

    public boolean isVisibleMotherSide() {
        return isVisibleMotherSide;
    }

    public boolean isVisibleMales() {
        return isVisibleMales;
    }

    public boolean isVisibleFemales() {
        return isVisibleFemales;
    }

    public void setVisibleFatherSide(boolean visibleFatherSide) {
        isVisibleFatherSide = visibleFatherSide;
    }

    public void setVisibleMotherSide(boolean visibleMotherSide) {
        isVisibleMotherSide = visibleMotherSide;
    }

    public void setVisibleMales(boolean visibleMales) {
        isVisibleMales = visibleMales;
    }

    public void setVisibleFemales(boolean visibleFemales) {
        isVisibleFemales = visibleFemales;
    }
}
