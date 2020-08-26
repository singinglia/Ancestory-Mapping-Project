package com.example.familymapclient2.tools;

import model.Event;
import model.Person;

public class ModelManipulator {

    public String eventToString(Event event){
        return event.getEventType().toUpperCase() + ": " + event.getCity() + ", " +
                event.getCountry() + " (" + event.getYear() + ")";
    }

    public String getFullName(Person person){
        return person.getFirstName() + " " + person.getLastName();
    }
}
