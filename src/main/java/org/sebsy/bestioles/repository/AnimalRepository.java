package org.sebsy.bestioles.repository;

import java.util.List;

import org.sebsy.bestioles.model.Animal;
import org.sebsy.bestioles.model.Sex;
import org.sebsy.bestioles.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnimalRepository extends JpaRepository<Animal, Integer> {

    List<Animal> findBySpecies(Species species);

    List<Animal> findBySpeciesCommonName(String commonName);

    List<Animal> findByColorIn(List<String> colors);

    long countBySex(Sex sex);

    @Query(value = "select * from animal where name like concat(:debut, '%')", nativeQuery = true)
    List<Animal> commencantPar(@Param("debut") String debut);
}