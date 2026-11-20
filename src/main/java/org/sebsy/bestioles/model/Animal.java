package org.sebsy.bestioles.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private Sex sex;

    @Column(length = 50)
    private String color;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Species species;

    protected Animal() {
    }

    public Animal(String name, Sex sex, String color, Species species) {
        this.name = name;
        this.sex = sex;
        this.color = color;
        this.species = species;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Sex getSex() {
        return sex;
    }

    public String getColor() {
        return color;
    }

    public Species getSpecies() {
        return species;
    }

    @Override
    public String toString() {
        return "Animal{id=" + id + ", name='" + name + "'}";
    }
}