package com.example.familymapclient2.models;

import com.example.familymapclient2.tools.ModelManipulator;

import model.Person;

public class Relation {
    Person base;
    Person relative;
    String relationshipToBase;

    public Relation(Person base, Person relative, String relationshipToBase) {
        this.base = base;
        this.relative= relative;
        this.relationshipToBase = relationshipToBase;
    }

    public Person getBase() {
        return base;
    }

    public String getRelativeName() {
        ModelManipulator m = new ModelManipulator();
        return m.getFullName(relative);
    }

    public Person getRelative(){
        return relative;
    }

    public String getRelationshipToBase() {
        return relationshipToBase;
    }
}
