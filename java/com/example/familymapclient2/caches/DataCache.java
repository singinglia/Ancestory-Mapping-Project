package com.example.familymapclient2.caches;

import com.example.familymapclient2.models.Relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import model.Event;
import model.Person;

public class DataCache {
    private static DataCache instance;



    private DataCache(){
        fatherSideMales = new HashSet<Person>();
        motherSideMales = new HashSet<Person>();
        fatherSideFemales = new HashSet<Person>();
        motherSideFemales = new HashSet<Person>();

        eventsFatherSideMales = new HashSet<Event>();
        eventsMotherSideMales = new HashSet<Event>();
        eventsFatherSideFemales = new HashSet<Event>();
        eventsMotherSideFemales = new HashSet<Event>();


        IDMap = new HashMap<String, Person>();
        eventMap = new HashMap<String, ArrayList<Event>>();
        IDEvent = new HashMap<String, Event>();
        relationMap = new HashMap<>();
        childMap = new HashMap<>();

        eventSearch = new ArrayList<>();

    }

    public static DataCache getInstance(){
        if (instance == null){
            instance = new DataCache();
        }
        return instance;
    }

    private String authToken = null;

    private boolean dataUnsorted = true;

    private ArrayList<Person> Persons;
    private ArrayList<Event>  Events;
    private String userPersonID;
    private Person User;
    private Person father;
    private Person mother;
    private Person spouse;

    private Event lastEvent = null;

    private ArrayList<Event> eventSearch;

    //Sorted Sets
    private HashSet<Person> fatherSideMales;
    private HashSet<Person> motherSideMales;
    private HashSet<Person> fatherSideFemales;
    private HashSet<Person> motherSideFemales;

    private HashSet<Event> eventsFatherSideMales;
    private HashSet<Event> eventsMotherSideMales;
    private HashSet<Event> eventsFatherSideFemales;
    private HashSet<Event> eventsMotherSideFemales;

    //Maps

    //PersonID -> Person
    private HashMap<String, Person> IDMap;
    //Key Person ID -> Events
    private HashMap<String, ArrayList<Event>> eventMap;
    //EventId -> Event
    private HashMap<String, Event> IDEvent;
    //PersonId -> Relations
    private HashMap<String, ArrayList<Relation>> relationMap;
    //PersonID -> Child Person
    private HashMap<String, Person> childMap;

    //Methods
    public void setUserPersonID(String userPersonID) {
        this.userPersonID = userPersonID;
        if(Persons != null) {
            if (User == null) {
                for (Person person : Persons) {
                    if (person.getPersonID().equals(userPersonID)) {
                        User = person;
                    }
                }
                //System.out.println("User not found");
            }

        }
        else {
            User = null;
        }
    }

    public void clearData(){
        instance = null;
    }

    /**
     * Sort Data
     *
     * Sort Data Includes Creating a person map;
     * setting spouse, father and mother;
     * sorting genders and family sides;
     * associating events with people;
     * sorting events by year;
     * and creating get event by id map
     */

    public void SortData(){

        //Create Person Map
        for (Person person : Persons){
            IDMap.put(person.getPersonID(), person);
        }

        //User
        if(User == null){
            getNameOfUser();
        }

        //Spouse
        if(User.getSpouseID() != null) {
            spouse = getSpouse(User);
        }

        //Sort be side
        father = getFather(User);
        mother = getMother(User);
        motherSideFemales.add(mother);
        sortGendersMother(mother);
        fatherSideMales.add(father);
        sortGendersFather(father);
        generateChildMap();

        associateEvents();
        sortEvents();
        for (Event event : Events){
            IDEvent.put(event.getEventID(), event);
        }

        dataUnsorted = false;
    }

    public Person getUser(){
        return User;
    }

    /**
     * Checks settings to see if the events and markers for this person are
     * currently visible
     *
     * @param personID person to test
     * @return true if visible, false otherwise
     */

    public boolean isCurrentlyVisible(String personID){
        if (personID == null){
            return false;
        }
        Person person = personFromID(personID);
        String gender = person.getGender();
        if(gender.equals("m")){
            if(!SettingsCache.getInstance().isVisibleMales()){
                return false;
            }
        }else{
            if(!SettingsCache.getInstance().isVisibleFemales()){
                return false;
            }
        }
        //If not User or Spouse
        if(personID.equals(User.getPersonID())) {
            return true;
        }
        if(personID.equals(User.getSpouseID())) {
            return true;
        }

        if(gender.equals("m")){
            if (!SettingsCache.getInstance().isVisibleFatherSide()) {
                for(Person p : fatherSideMales) {
                    if(p.getPersonID().equals(personID)) {
                        return false;
                    }
                }
            }
            if (!SettingsCache.getInstance().isVisibleMotherSide()) {
                for(Person p : motherSideMales) {
                    if(p.getPersonID().equals(personID)) {
                        return false;
                    }
                }
            }
        }else{
            if (!SettingsCache.getInstance().isVisibleFatherSide()) {
                for(Person p : fatherSideFemales) {
                    if(p.getPersonID().equals(personID)) {
                        return false;
                    }
                }
            }
            if (!SettingsCache.getInstance().isVisibleMotherSide()) {
                for(Person p : motherSideFemales) {
                    if(p.getPersonID().equals(personID)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public Person getUserSpouse(){
        return spouse;
    }

    public Person getSpouse(Person person){
        if(IDMap.containsKey(person.getSpouseID())){
            return IDMap.get(person.getSpouseID());
        }
        return null;
    }

    public Person getFather(Person child){
        if(IDMap.containsKey(child.getFatherID())){
            return IDMap.get(child.getFatherID());
        }
        return null;
    }

    public Person getMother(Person child){
        if(IDMap.containsKey(child.getMotherID())){
            return IDMap.get(child.getMotherID());
        }
        return null;
    }
    
    public Event getPersonFirstEvent(String personID){
        if(eventMap.containsKey(personID)) {
            return eventMap.get(personID).get(0);
        }
        return null;
    }

    private void sortGendersMother(Person base){

        if (base.getFatherID() != null){
            motherSideMales.add(IDMap.get(base.getFatherID()));
            sortGendersMother(IDMap.get(base.getFatherID()));
        }
        if(base.getMotherID() != null) {
            motherSideFemales.add(IDMap.get(base.getMotherID()));
            sortGendersMother(IDMap.get(base.getMotherID()));
        }
    }

    private void sortGendersFather(Person base){

        if (base.getFatherID() != null){
            fatherSideMales.add(IDMap.get(base.getFatherID()));
            sortGendersFather(IDMap.get(base.getFatherID()));
        }
        if(base.getMotherID() != null) {
            fatherSideFemales.add(IDMap.get(base.getMotherID()));
            sortGendersFather(IDMap.get(base.getMotherID()));
        }
    }

    private void associateEvents(){
        for(Event event : Events){
            if(eventMap.containsKey(event.getPersonID())){
                eventMap.get(event.getPersonID()).add(event);
            } else{
                ArrayList<Event> personEvents = new ArrayList<Event>();
                personEvents.add(event);
                eventMap.put(event.getPersonID(), personEvents);
            }
        }
        orderEvents();
    }

    private void orderEvents(){
        for (Person person : Persons){
            ArrayList<Event> orderedEvents = new ArrayList<Event>();
            ArrayList<Event> personEvent = eventMap.get(person.getPersonID());
            if(personEvent.size() > 1) {

                //Get Order
                Event death = null;
                ArrayList<Event> eventsToOrder = new ArrayList<Event>();
                ArrayList<Integer> eventYears = new ArrayList<Integer>();
                for (Event event : personEvent) {
                    if (event.getEventType().toLowerCase().equals("birth")) {
                        orderedEvents.add(0, event);
                    } else if (event.getEventType().toLowerCase().equals("death")) {
                        death = event;
                    } else {
                        eventsToOrder.add(event);
                        eventYears.add(event.getYear());
                    }
                }
                if (eventsToOrder.size() > 2) {
                    //Sort middle events with Insertion Sort
                    for (int i = 1; i < eventYears.size(); ++i) {
                        int key = eventYears.get(i);
                        Event keyEvent = eventsToOrder.get(i);
                        int j = i - 1;
                        while (j >= 0 && eventYears.get(j) > key) {
                            eventYears.set(j + 1, eventYears.get(j));
                            eventsToOrder.set(j + 1, eventsToOrder.get(j));
                            j = j - 1;
                        }
                        eventYears.set(j + 1, key);
                        eventsToOrder.set(j + 1, keyEvent);
                    }
                }
                if (eventsToOrder.size() == 2) {
                    if (eventYears.get(0) > eventYears.get(1)) {
                        Event temp = eventsToOrder.get(0);
                        eventsToOrder.set(0, eventsToOrder.get(1));
                        eventsToOrder.set(1, temp);
                    }
                }
                for (int i = 0; i < eventsToOrder.size(); ++i) {
                    orderedEvents.add(eventsToOrder.get(i));
                }
                if (death != null) {
                    orderedEvents.add(death);
                }

                eventMap.put(person.getPersonID(), orderedEvents);
            }

        }
    }

    private void sortEvents(){
        for(Person person : motherSideFemales){
            ArrayList<Event> personEvents = eventMap.get(person.getPersonID());
            assert personEvents != null;
            for (Event event : personEvents){
                eventsMotherSideFemales.add(event);
            }
        }

        for(Person person : fatherSideFemales){
            ArrayList<Event> personEvents = eventMap.get(person.getPersonID());
            assert personEvents != null;
            for (Event event : personEvents){
                eventsFatherSideFemales.add(event);
            }
        }

        for(Person person : motherSideMales){
            ArrayList<Event> personEvents = eventMap.get(person.getPersonID());
            assert personEvents != null;
            for (Event event : personEvents){
                eventsMotherSideMales.add(event);
            }
        }

        for(Person person : fatherSideMales){
            ArrayList<Event> personEvents = eventMap.get(person.getPersonID());
            assert personEvents != null;
            for (Event event : personEvents){
                eventsFatherSideMales.add(event);
            }
        }
    }

    public String getNameOfUser(){
        if(User == null) {
            for (Person person : Persons) {
                if (person.getPersonID().equals(userPersonID)) {
                    User = person;
                    return User.getFirstName() + " " + User.getLastName();
                }
            }
            System.out.println("User not found");
        }

        return User.getFirstName() + " " + User.getLastName();

    }

    /**
     * Finds relatives of an individual that are in the database
     * @param personID person to find relatives for
     * @return List of Relation items that have the person and relationship
     */

    public List<Relation> getRelatives(String personID){
        if(relationMap.containsKey(personID)){
            return relationMap.get(personID);
        }  else{
            return generateRelations(personID);
        }
    }

    private ArrayList<Relation> generateRelations(String personID){
        final String FATHER = "Father";
        final String MOTHER = "Mother";
        final String SPOUSE = "Spouse";
        final String CHILD = "Child";
        Person p = personFromID(personID);
        ArrayList<Relation> relations = new ArrayList<>();

        if(p.getFatherID() != null) {
            relations.add(new Relation(p, personFromID(p.getFatherID()), FATHER));
        }
        if(p.getMotherID() != null) {
            relations.add(new Relation(p, personFromID(p.getMotherID()), MOTHER));
        }
        if(p.getSpouseID() != null) {
            relations.add(new Relation(p, personFromID(p.getSpouseID()), SPOUSE));
        }
        if(childMap.containsKey(p.getPersonID())){
            relations.add(new Relation(p, childMap.get(p.getPersonID()), CHILD));
        }
        relationMap.put(p.getPersonID(), relations);
        return relations;
    }

    public ArrayList<Event> getPersonEvents(String personId){
        return eventMap.get(personId);
    }

    private void generateChildMap(){
        for (Person parent : Persons){
            for (Person child : Persons){
                if (parent.getPersonID().equals(child.getFatherID()) ||
                        parent.getPersonID().equals(child.getMotherID())){
                    childMap.put(parent.getPersonID(), child);
                }
            }
        }
    }

    public ArrayList<Person> searchPeople(String s){
        ArrayList<Person> inSearch = new ArrayList<>();
        for(Person person : Persons){
            if(person.getFirstName().toLowerCase().contains(s.toLowerCase())){
                inSearch.add(person);
            }
            if(person.getLastName().toLowerCase().contains(s.toLowerCase())){
                inSearch.add(person);
            }
        }
        return inSearch;
    }

    public ArrayList<Event> searchEvents(String s){
        eventSearch.clear();

        if(isCurrentlyVisible(User.getPersonID())){
            addPersonEventsToSearch(User, s);
        }
        if(spouse != null) {
            if (isCurrentlyVisible(spouse.getPersonID())) {
                addPersonEventsToSearch(spouse, s);
            }
        }
        if(SettingsCache.getInstance().isVisibleMales()){
            if (SettingsCache.getInstance().isVisibleFatherSide()) {
                for(Person p : fatherSideMales) {
                    addPersonEventsToSearch(p,s);
                }
            }
            if (SettingsCache.getInstance().isVisibleMotherSide()) {
                for(Person p : motherSideMales) {
                    addPersonEventsToSearch(p,s);
                }
            }
        }
        if(SettingsCache.getInstance().isVisibleFemales()){
            if (SettingsCache.getInstance().isVisibleFatherSide()) {
                for(Person p : fatherSideFemales) {
                    addPersonEventsToSearch(p,s);
                }
            }
            if (SettingsCache.getInstance().isVisibleMotherSide()) {
                for(Person p : motherSideFemales) {
                    addPersonEventsToSearch(p,s);
                }
            }
        }
        return eventSearch;
    }

    private void addPersonEventsToSearch(Person person, String s){
        if(person.getPersonID()==null){
            return;
        }
        if(eventMap.containsKey(person.getPersonID())){
            for(Event event : eventMap.get(person.getPersonID())){
                if(event.getCountry().toLowerCase().contains(s.toLowerCase())){
                    eventSearch.add(event);
                }
                if(event.getCity().toLowerCase().contains(s.toLowerCase())){
                    eventSearch.add(event);
                }
                if(event.getEventType().toLowerCase().contains(s.toLowerCase())){
                    eventSearch.add(event);
                }
                String year = Integer.toString(event.getYear());
                if(year.contains(s)){
                    eventSearch.add(event);
                }
            }
        }
    }

    public Event eventFromID(String eventId){
        return IDEvent.get(eventId);
    }

    public Person personFromID(String personID){
        return IDMap.get(personID);
    }

    public HashSet<Person> getFatherSideMales() {
        return fatherSideMales;
    }

    public HashSet<Person> getMotherSideMales() {
        return motherSideMales;
    }

    public HashSet<Person> getFatherSideFemales() {
        return fatherSideFemales;
    }

    public HashSet<Person> getMotherSideFemales() {
        return motherSideFemales;
    }

    public HashSet<Event> getEventsFatherSideMales() {
        return eventsFatherSideMales;
    }

    public HashSet<Event> getEventsMotherSideMales() {
        return eventsMotherSideMales;
    }

    public HashSet<Event> getEventsFatherSideFemales() {
        return eventsFatherSideFemales;
    }

    public HashSet<Event> getEventsMotherSideFemales() {
        return eventsMotherSideFemales;
    }

    public Event getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(Event lastEvent) {
        this.lastEvent = lastEvent;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUserID(){
        return User.getPersonID();
    }

    public String getUserFather(){
        return father.getPersonID();
    }

    public String getUserMother(){
        return mother.getPersonID();
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setPersons(ArrayList<Person> persons) {
        Persons = persons;
    }

    public void setEvents(ArrayList<Event> events) {
        Events = events;
    }

    public boolean getDataUnsorted(){
        return dataUnsorted;
    }
}
