package com.example.familymapclient2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.familymapclient2.caches.DataCache;
import com.example.familymapclient2.caches.SettingsCache;
import com.example.familymapclient2.tools.ModelManipulator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import model.Event;
import model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private HashSet<Polyline> currLines;
    private HashSet<Event> personEvents;
    private HashSet<Person> visiblePersons;
    private boolean isVisibleFatherSide = true;
    private boolean isVisibleMotherSide = true;
    private boolean isVisibleMales = true;
    private boolean isVisibleFemales = true;
    int currSettingChangeCount;
    int currLinesChangesCount;
    int colorCounter = 0;
    int genWidthCounterMother = 0;
    int genWidthCounterFather = 0;
    View mapView;
    Person currPerson = null;
    String currEventID;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if(currEventID == null) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.main_menu, menu);

            MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuItem);
            settingsMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear)
                    .colorRes(R.color.menu_icon)
                    .actionBarSize());
            MenuItem searchMenuItem = menu.findItem(R.id.searchMenuItem);
            searchMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                    .colorRes(R.color.menu_icon)
                    .actionBarSize());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(map != null) {
            if (currSettingChangeCount != SettingsCache.getInstance().getMarkerChangeCount()) {
                isVisibleFatherSide = SettingsCache.getInstance().isVisibleFatherSide();
                isVisibleMotherSide = SettingsCache.getInstance().isVisibleMotherSide();
                isVisibleMales = SettingsCache.getInstance().isVisibleMales();
                isVisibleFemales = SettingsCache.getInstance().isVisibleFemales();
                getVisibleMarkers();
                map.clear();
                addMarkers();
            }
            if(currLinesChangesCount != SettingsCache.getInstance().getLineChangeCount()){
                Event lastEvent = DataCache.getInstance().getLastEvent();
                if(lastEvent != null) {
                    addLines(lastEvent);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsMenuItem:
                Intent intentSettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.searchMenuItem:
                Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                startActivity(intentSearch);
                return true;
            case android.R.id.home:
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        mapView = view;

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(DataCache.getInstance().getDataUnsorted()) {
            DataCache.getInstance().SortData();
        }

        isVisibleFatherSide = SettingsCache.getInstance().isVisibleFatherSide();
        isVisibleMotherSide = SettingsCache.getInstance().isVisibleMotherSide();
        isVisibleMales = SettingsCache.getInstance().isVisibleMales();
        isVisibleFemales = SettingsCache.getInstance().isVisibleFemales();

        personEvents = new HashSet<Event>();
        visiblePersons = new HashSet<>();
        currLines = new HashSet<>();
        currSettingChangeCount = SettingsCache.getInstance().getMarkerChangeCount();
        currLinesChangesCount = SettingsCache.getInstance().getLineChangeCount();

        Iconify.with(new FontAwesomeModule());

        Bundle bundle = this.getArguments();
        currEventID = bundle.getString("eventID");

        if(currEventID == null) {
            setHasOptionsMenu(true);
            Drawable androidIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).
                    colorRes(R.color.android_icon).sizeDp(40);
            ImageView icon = view.findViewById(R.id.EventIcon);
            icon.setImageDrawable(androidIcon);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);


    }

    @Override
    public void onMapLoaded() {
        addMarkers();

        if(currEventID != null){
            Event currEvent = DataCache.getInstance().eventFromID(currEventID);
            addLines(currEvent);
            LoadEventWindow(currEvent);
            LatLng place = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(place, 5));
            //Set for Restore
            DataCache.getInstance().setLastEvent(currEvent);
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String eventID = (String) marker.getTag();
                Event currEvent = DataCache.getInstance().eventFromID(eventID);
                addLines(currEvent);
                LoadEventWindow(currEvent);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 10));
                //Set for Restore
                DataCache.getInstance().setLastEvent(currEvent);
                return false;
            }
        });

        RelativeLayout EventBox = mapView.findViewById(R.id.EventGrid);
        EventBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                intent.putExtra("personID", currPerson.getPersonID());
                startActivity(intent);
            }
        });



    }

    private void LoadEventWindow(Event event) {
        //Display Event Data
        Person eventPerson = DataCache.getInstance().personFromID(event.getPersonID());
        currPerson = eventPerson;
        changeIcon(eventPerson);
        setNameText(eventPerson);
        setEventData(event);

    }

    private void changeIcon(Person eventPerson){
        Drawable genderIcon = null;
        if(eventPerson.getGender().equals("m")) {
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                    colorRes(R.color.male_icon).sizeDp(40);
        } else{
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                    colorRes(R.color.female_icon).sizeDp(40);
        }
        ImageView icon = mapView.findViewById(R.id.EventIcon);
        icon.setImageDrawable(genderIcon);
    }

    private void setNameText(Person eventPerson){
        String name = eventPerson.getFirstName() + " " + eventPerson.getLastName();
        TextView personForEvent = mapView.findViewById(R.id.TextWindowName);
        personForEvent.setText(name);
    }

    private void setEventData(Event event){
        ModelManipulator manipulator = new ModelManipulator();
        String eventString = manipulator.eventToString(event);
        TextView eventText = mapView.findViewById(R.id.EventInfo);
        eventText.setText(eventString);
    }

    private void addMarkers(){
        getVisibleMarkers();

        HashMap<String, Float> typeToColor = new HashMap<String,Float>();

        for(Event event : personEvents) {
            LatLng placeCoor = new LatLng(event.getLatitude(), event.getLongitude());
            float markerColor = 0;
            if (typeToColor.containsKey(event.getEventType().toLowerCase())) {
                markerColor = typeToColor.get(event.getEventType().toLowerCase());
            }else {
                markerColor = getNextColor(event);
                typeToColor.put(event.getEventType().toLowerCase(), markerColor);
            }
            Marker place = map.addMarker(new MarkerOptions()
                    .position(placeCoor)
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                    .title(event.getCity() + ", " + event.getCountry()));
            place.setTag(event.getEventID());

        }
    }

    private void addLines(Event event) {
        removeLines();
        Person person = DataCache.getInstance().personFromID(event.getPersonID());
        if (SettingsCache.getInstance().ShowSpouseLines()) {
            if (isVisibleMales && isVisibleFemales) {
                if ((person.getPersonID().equals(DataCache.getInstance().getUserFather())) ||
                        (person.getPersonID().equals(DataCache.getInstance().getUserMother()))) {
                    if (isVisibleFatherSide && isVisibleMotherSide) {
                        addSpouseLine(person, event);
                    }
                } else {
                    addSpouseLine(person, event);
                }
            }
        }
        if (SettingsCache.getInstance().ShowLifeStoryLines()){
            addLifeStoryLine(person);
        }
        if (SettingsCache.getInstance().ShowFamilyTreeLines()) {
            addFamilyTreeLines(person, event);
            genWidthCounterMother = 0;
            genWidthCounterFather = 0;
        }
        currLinesChangesCount = SettingsCache.getInstance().getLineChangeCount();
    }

    private void addFamilyTreeLines(Person person, Event event){
        if( person.getPersonID().equals(DataCache.getInstance().getUserID())){
            if(isVisibleMotherSide){
                if(isVisibleFemales) {
                    if (person.getMotherID() != null) {
                        firstGenMotherLine(person, event);
                    }
                }
            }
            if(isVisibleFatherSide){
                if(isVisibleMales) {
                    if (person.getFatherID() != null) {
                        firstGenFatherLine(person, event);
                    }
                }
            }
        }
        else{
            if(isVisibleMales) {
                if (person.getFatherID() != null) {
                    firstGenFatherLine(person, event);
                }
            }
            //Else no recursive call
            if(isVisibleFemales){
                if (person.getMotherID() != null) {
                    firstGenMotherLine(person, event);
                }
            }
        }
    }

    private void firstGenMotherLine(Person base, Event event){
        LatLng firstPlace = new LatLng(event.getLatitude(), event.getLongitude());
        LatLng secondPlace = new LatLng(DataCache.getInstance().getPersonFirstEvent(base.getMotherID()).getLatitude(),
                DataCache.getInstance().getPersonFirstEvent(base.getMotherID()).getLongitude());
        Polyline line = map.addPolyline(new PolylineOptions()
                .add(firstPlace, secondPlace)
                .width(getWidth(genWidthCounterMother))
                .color(Color.BLUE));
        currLines.add(line);
        genWidthCounterMother++;
        addGenLineR(DataCache.getInstance().personFromID(base.getMotherID()), genWidthCounterMother);
        genWidthCounterMother--;
    }

    private void firstGenFatherLine(Person person, Event event){
        LatLng firstPlace = new LatLng(event.getLatitude(), event.getLongitude());
        LatLng secondPlace = new LatLng(DataCache.getInstance().getPersonFirstEvent(person.getFatherID()).getLatitude(),
                DataCache.getInstance().getPersonFirstEvent(person.getFatherID()).getLongitude());
        Polyline line = map.addPolyline(new PolylineOptions()
                .add(firstPlace, secondPlace)
                .width(getWidth(genWidthCounterFather))
                .color(Color.BLUE));
        currLines.add(line);
        genWidthCounterFather++;
        addGenLineR(DataCache.getInstance().personFromID(person.getFatherID()), genWidthCounterFather);
        genWidthCounterFather--;
    }

    private void addGenLineR(Person base, int sideGenCounter){

        if ((base.getFatherID() != null) && isVisibleMales){
            LatLng firstPlace = new LatLng(DataCache.getInstance().getPersonFirstEvent(base.getPersonID()).getLatitude(),
                    DataCache.getInstance().getPersonFirstEvent(base.getPersonID()).getLongitude());
            LatLng secondPlace = new LatLng(DataCache.getInstance().getPersonFirstEvent(base.getFatherID()).getLatitude(),
                    DataCache.getInstance().getPersonFirstEvent(base.getFatherID()).getLongitude());
            Polyline line = map.addPolyline(new PolylineOptions()
                    .add(firstPlace, secondPlace)
                    .width(getWidth(sideGenCounter))
                    .color(Color.BLUE));
            currLines.add(line);
            sideGenCounter++;
            addGenLineR(DataCache.getInstance().personFromID(base.getFatherID()), sideGenCounter);
            sideGenCounter--;
        }
        //Else no recursive call
        if ((base.getMotherID() != null) && isVisibleFemales) {
            LatLng firstPlace = new LatLng(DataCache.getInstance().getPersonFirstEvent(base.getPersonID()).getLatitude(),
                    DataCache.getInstance().getPersonFirstEvent(base.getPersonID()).getLongitude());
            LatLng secondPlace = new LatLng(DataCache.getInstance().getPersonFirstEvent(base.getMotherID()).getLatitude(),
                    DataCache.getInstance().getPersonFirstEvent(base.getMotherID()).getLongitude());
            Polyline line = map.addPolyline(new PolylineOptions()
                    .add(firstPlace, secondPlace)
                    .width(getWidth(sideGenCounter))
                    .color(Color.BLUE));
            currLines.add(line);
            sideGenCounter++;
            addGenLineR(DataCache.getInstance().personFromID(base.getMotherID()), sideGenCounter);
            sideGenCounter--;
        }
        //Else no recursive call
    }

    private int getWidth(int counter) {
        if (counter == 0) {
            return (20);
        } else if (counter < 4){
            return (20- (counter * 5));
        } else {
            return 1;
        }
    }

    private void removeLines(){
        for(Polyline line : currLines){
            line.remove();
        }
    }

    private void addLifeStoryLine(Person person){
        ArrayList<Event> personEvents = DataCache.getInstance().getPersonEvents(person.getPersonID());
        if((personEvents != null) ||(personEvents.size() == 1)) {

            for (int i = 0; i < (personEvents.size() - 1); i++) {
                LatLng firstPlace = new LatLng(personEvents.get(i).getLatitude(), personEvents.get(i).getLongitude());
                LatLng secondPlace = new LatLng(personEvents.get(i + 1).getLatitude(), personEvents.get(i + 1).getLongitude());
                Polyline line = map.addPolyline(new PolylineOptions()
                        .add(firstPlace, secondPlace)
                        .width(8)
                        .color(Color.YELLOW));
                currLines.add(line);

            }


        }
    }

    private void addSpouseLine(Person person, Event event) {

        if (person.getSpouseID() != null) {
            LatLng personPlace = new LatLng(event.getLatitude(), event.getLongitude());
            LatLng spousePlace = new LatLng(DataCache.getInstance().getPersonFirstEvent(person.getSpouseID()).getLatitude(),
                    DataCache.getInstance().getPersonFirstEvent(person.getSpouseID()).getLongitude());
            Polyline line = map.addPolyline(new PolylineOptions()
                    .add(personPlace, spousePlace)
                    .width(8)
                    .color(Color.RED));
            currLines.add(line);
        }

    }

    private void getVisibleMarkers(){
        personEvents.clear();
        Person user = DataCache.getInstance().getUser();
        if(DataCache.getInstance().isCurrentlyVisible(user.getPersonID())) {
            ArrayList<Event> userEvents = DataCache.getInstance().getPersonEvents(user.getPersonID());
            for (Event event : userEvents) {
                personEvents.add(event);
            }
        }

        //Spouse
        if (DataCache.getInstance().personFromID(user.getSpouseID()) != null){
            if(DataCache.getInstance().isCurrentlyVisible(user.getSpouseID())) {
                ArrayList<Event> spouseEvents = DataCache.getInstance().getPersonEvents(user.getSpouseID());
                for (Event event : spouseEvents) {
                    personEvents.add(event);
                }
            }
        }

        if(isVisibleFatherSide){
            if(isVisibleMales){
                HashSet<Event> fatherMales = DataCache.getInstance().getEventsFatherSideMales();
                for(Event event : fatherMales){
                    personEvents.add(event);
                }

                HashSet<Person> visiblePeople = DataCache.getInstance().getFatherSideMales();
                for(Person person: visiblePeople){
                    visiblePeople.add(person);
                }
            }
            if(isVisibleFemales){
                HashSet<Event> fatherFemales = DataCache.getInstance().getEventsFatherSideFemales();
                for(Event event : fatherFemales){
                    personEvents.add(event);
                }

                HashSet<Person> visiblePeople = DataCache.getInstance().getFatherSideFemales();
                for(Person person: visiblePeople){
                    visiblePeople.add(person);
                }
            }
        }
        if(isVisibleMotherSide){
            if(isVisibleMales){
                HashSet<Event> maleEvents = DataCache.getInstance().getEventsMotherSideMales();
                for(Event event : maleEvents){
                    personEvents.add(event);
                }

                HashSet<Person> visiblePeople = DataCache.getInstance().getMotherSideMales();
                for(Person person: visiblePeople){
                    visiblePeople.add(person);
                }
            }
            if(isVisibleFemales){
                HashSet<Event> femaleEvents = DataCache.getInstance().getEventsMotherSideFemales();
                for(Event event : femaleEvents){
                    personEvents.add(event);
                }

                HashSet<Person> visiblePeople = DataCache.getInstance().getMotherSideFemales();
                for(Person person: visiblePeople){
                    visiblePeople.add(person);
                }
            }
        }
        currSettingChangeCount = SettingsCache.getInstance().getMarkerChangeCount();
    }

    private float getNextColor(Event event) {

        if (event.getEventType().toLowerCase().equals("birth")){
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (event.getEventType().toLowerCase().equals("marriage")){
            return BitmapDescriptorFactory.HUE_ROSE;
        } else if (event.getEventType().toLowerCase().equals("death")){
            return BitmapDescriptorFactory.HUE_ORANGE;
        } else{
            if(colorCounter < 6){
                colorCounter++;
            } else{
                colorCounter = 0;
            }
            final float[] colors = new float[]{
                    BitmapDescriptorFactory.HUE_YELLOW,
                    BitmapDescriptorFactory.HUE_AZURE,
                    BitmapDescriptorFactory.HUE_MAGENTA,
                    BitmapDescriptorFactory.HUE_RED,
                    BitmapDescriptorFactory.HUE_BLUE,
                    BitmapDescriptorFactory.HUE_VIOLET,
                    BitmapDescriptorFactory.HUE_CYAN
            };
            return colors[colorCounter];
        }

    }


}
