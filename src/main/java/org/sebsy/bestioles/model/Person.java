package org.sebsy.bestioles.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String firstname;

    @Column(nullable = false, length = 50)
    private String lastname;

    private Integer age;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false, length = 100)
    private String mdp;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToMany
    private Set<Animal> animals = new HashSet<>();

    protected Person() {
    }

    public Person(String firstname, String lastname, Integer age, String login, String mdp) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.login = login;
        this.mdp = mdp;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Integer getAge() {
        return age;
    }

    public String getLogin() {
        return login;
    }

    public boolean isActive() {
        return active;
    }

    public Set<Animal> getAnimals() {
        return animals;
    }

    @Override
    public String toString() {
        return "Person{id=" + id + ", firstname='" + firstname + "', lastname='" + lastname + "'}";
    }
}