package com.example.familymapclient2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymapclient2.caches.DataCache;
import com.example.familymapclient2.tools.ModelManipulator;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView searchRecyclerView;
    private SearchAdapter sAdapter;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.title_activity_search);

        searchRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchBar = (EditText) findViewById(R.id.searchTextEdit);
        searchBar.addTextChangedListener(mTextWatcher);
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

    private void updateUI(String s) {
        List<Person> persons;
        List<Event> events;

        if(s.equals("")){
            persons = new ArrayList<>();
            events = new ArrayList<>();

        } else{
            persons = DataCache.getInstance().searchPeople(s);
            events = DataCache.getInstance().searchEvents(s);
        }

        if (sAdapter == null) {
            sAdapter = new SearchAdapter(persons, events);
            searchRecyclerView.setAdapter(sAdapter);
        } else{

            // clear old list
            sAdapter.setData(persons,events);
            sAdapter.notifyDataSetChanged();
        }

    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String search = searchBar.getText().toString();
            updateUI(search);
        }
    };


    private class SearchHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Event event;
        private Person person;

        private TextView secondLineTextView;
        private TextView firstLineTextView;
        private ImageView iconView;

        public SearchHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_search, parent, false));
            itemView.setOnClickListener(this);

            firstLineTextView = (TextView) itemView.findViewById(R.id.topLineSearchItem);
            secondLineTextView = (TextView) itemView.findViewById(R.id.bottomLineSearchItem);
            iconView = (ImageView) itemView.findViewById(R.id.searchBarIcon);
        }

        public void bind(Person person) {
            this.person = person;
            ModelManipulator m = new ModelManipulator();
            firstLineTextView.setText(m.getFullName(person));
            secondLineTextView.setText("");
            changeGenderIcon(person, iconView);
        }

        public void bind(Event event) {
            this.event = event;
            person = DataCache.getInstance().personFromID(event.getPersonID());
            ModelManipulator m = new ModelManipulator();
            firstLineTextView.setText(m.eventToString(event));
            secondLineTextView.setText(m.getFullName(person));
            addMarkerIcon(iconView);
        }

        @Override
        public void onClick(View v) {
            if(event == null){
                //Person
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra("personID", person.getPersonID());
                startActivity(intent);
            } else{
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra("eventID", event.getEventID());
                startActivity(intent);
            }
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {

        private List<Person> people;
        private List<Event> events;

        public SearchAdapter(List<Person> people, List<Event> events){
            this.people = people;
            this.events = events;
        }

        public void setData(List<Person> people, List<Event> events){
            this.people = people;
            this.events = events;
        }

        @NonNull
        @Override
        public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(SearchActivity.this);
            return new SearchHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
            if(position < people.size()){
                Person person = people.get(position);
                holder.bind(person);
            } else {
                Event event = events.get(position - people.size());
                holder.bind(event);
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
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
}
