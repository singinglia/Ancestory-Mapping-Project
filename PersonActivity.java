package com.example.familymapclient2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymapclient2.caches.DataCache;
import com.example.familymapclient2.models.Relation;
import com.example.familymapclient2.tools.ModelManipulator;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    Person displayPerson;
    String personID = null;
    final String PERSON_ID = "personID";

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        setTitle(R.string.title_activity_person);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        Intent intent = getIntent();
        personID = intent.getStringExtra(PERSON_ID);
        displayPerson = DataCache.getInstance().personFromID(personID);


        TextView personNameView = (TextView) findViewById(R.id.nameFirstTextPersonBar);
        personNameView.setText(displayPerson.getFirstName());

        TextView personLastNameView = (TextView) findViewById(R.id.nameLastTextPersonBar);
        personLastNameView.setText(displayPerson.getLastName());

        TextView personGenderView = (TextView) findViewById(R.id.GenderTextPersonBar);
        if(displayPerson.getGender().equals("m")) {
            personGenderView.setText("Male");
        } else{
            personGenderView.setText("Female");
        }

        List<Relation> relatives = DataCache.getInstance().getRelatives(personID);
        List<Event> personEvents = getPersonEvents();

        expandableListView.setAdapter(new ExpandableListAdapter(relatives, personEvents));

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


    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int EVENT_GROUP_POSITION = 0;
        private static final int PERSON_GROUP_POSITION = 1;

        private final List<Relation> relatives;
        private final List<Event> personEvents;


        ExpandableListAdapter(List<Relation> relatives, List<Event>personEvents) {
            this.relatives = relatives;
            this.personEvents = personEvents;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return relatives.size();
                case EVENT_GROUP_POSITION:
                    return personEvents.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return getString(R.string.personRelativesTitle);
                case EVENT_GROUP_POSITION:
                    return getString(R.string.personEventsTitle);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return relatives.get(childPosition);
                case EVENT_GROUP_POSITION:
                    return personEvents.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    titleView.setText(R.string.personRelativesTitle);
                    break;
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.personEventsTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item_bar, parent, false);
                    initializePersonEventsView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            return itemView;
        }

        private void initializeFamilyView(View relativeBarView, final int childPosition) {
            TextView relationshipView = relativeBarView.findViewById(R.id.relationTextPersonBar);
            relationshipView.setText(relatives.get(childPosition).getRelationshipToBase());

            TextView relativeNameView = relativeBarView.findViewById(R.id.nameTextPersonBar);
            relativeNameView.setText(relatives.get(childPosition).getRelativeName());

            ImageView personIconView = relativeBarView.findViewById(R.id.personBarIcon);
            changeGenderIcon(relatives.get(childPosition).getRelative(), personIconView);

            relativeBarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newPersonActivity(relatives.get(childPosition).getRelative().getPersonID());
                }
            });
        }

        private void initializePersonEventsView(View eventsBarView, final int childPosition) {
            ModelManipulator m = new ModelManipulator();
            TextView eventDetailsView = eventsBarView.findViewById(R.id.eventLinePersonEventBar);
            eventDetailsView.setText(m.eventToString(personEvents.get(childPosition)));

            TextView relativeNameView = eventsBarView.findViewById(R.id.nameLinePersonEventBar);
            Person relative = DataCache.getInstance().personFromID(personEvents.get(childPosition).getPersonID());
            relativeNameView.setText(m.getFullName(relative));

            ImageView eventIconView = eventsBarView.findViewById(R.id.eventBarIcon);
           addMarkerIcon(eventIconView);

            eventsBarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newEventActivity(personEvents.get(childPosition).getEventID());
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private void changeGenderIcon(Person eventPerson, ImageView icon){
        Drawable genderIcon = null;
        if(eventPerson.getGender().equals("m")) {
            genderIcon = new IconDrawable(this, FontAwesomeIcons.fa_male).
                    colorRes(R.color.male_icon).sizeDp(40);
        } else{
            genderIcon = new IconDrawable(this, FontAwesomeIcons.fa_female).
                    colorRes(R.color.female_icon).sizeDp(40);
        }
        icon.setImageDrawable(genderIcon);
    }

    private void addMarkerIcon(ImageView icon){

        Drawable markerIcon = new IconDrawable(this, FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.map_marker_icon).sizeDp(40);

        icon.setImageDrawable(markerIcon);
    }

    private void newPersonActivity(String personID){
        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra("personID", personID);
        startActivity(intent);
    }

    private void newEventActivity(String eventID){
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("eventID", eventID);
        startActivity(intent);
    }

    private ArrayList<Event> getPersonEvents(){
        if(DataCache.getInstance().isCurrentlyVisible(personID)) {
            return DataCache.getInstance().getPersonEvents(personID);
        }
        return new ArrayList<Event>();
    }
}
