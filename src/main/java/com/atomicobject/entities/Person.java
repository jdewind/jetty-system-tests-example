package com.atomicobject.entities;

public class Person {

    private Integer id;
    private String firstName;
    private String lastName;
    private String githubUsername;

    public Person() {
    }

    public Person(Integer id, String firstName, String lastName, String githubUsername) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.githubUsername = githubUsername;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }
}
