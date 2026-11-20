package org.sebsy.bestioles.repository;

import java.util.List;

import org.sebsy.bestioles.model.Animal;
import org.sebsy.bestioles.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    List<Person> findByLastnameOrFirstname(String lastname, String firstname);

    List<Person> findByAgeGreaterThanEqual(int age);

    List<Person> findByActiveTrue();

    @Query("select p from Person p where p.age between :min and :max order by p.age")
    List<Person> findAgedBetween(@Param("min") int min, @Param("max") int max);

    // Étape 8 : les propriétaires d'un animal
    @Query("select p from Person p where :animal member of p.animals")
    List<Person> findOwnersOf(@Param("animal") Animal animal);
}