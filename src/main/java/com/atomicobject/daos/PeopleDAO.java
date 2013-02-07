package com.atomicobject.daos;

import com.atomicobject.entities.Person;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class PeopleDAO {

    private final ArrayList<Person> people;
    private final ArrayList<Person> originalList;

    public PeopleDAO() {
        people = Lists.newArrayList(
                new Person(1, "Justin", "DeWind", "dewind"),
                new Person(2, "David", "Crosby", "dcrosby42")
        );
        originalList = new ArrayList<Person>(people);
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public ArrayList<Person> findAll() {
        return people;
    }

    public Person findById(Integer id) {
        for (Person person : people) {
            if(person.getId().equals(id)) {
                return person;
            }
        }
        return null;
    }

    public Integer count() {
        return people.size();
    }

    public void reset() {
        people.clear();
        people.addAll(originalList);
    }
}
