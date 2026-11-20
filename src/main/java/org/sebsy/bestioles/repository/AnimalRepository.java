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

    // Étape 7 : trois critères, en requête dérivée…
    List<Animal> findBySexAndSpeciesCommonNameAndColorIsNotNull(Sex sex, String commonName);

    // … puis en @Query
    @Query("""
            select a from Animal a
            where a.sex = :sexe
              and a.species.commonName = :espece
              and a.color is not null""")
    List<Animal> malesColoresDUneEspece(@Param("sexe") Sex sexe, @Param("espece") String espece);

    // Étape 9 : animaux sans aucun propriétaire
    @Query("select a from Animal a where not exists (select p from Person p where a member of p.animals)")
    List<Animal> sansProprietaire();

    // Étape 9 : cet animal a-t-il au moins un propriétaire ?
    @Query("select case when count(p) > 0 then true else false end from Person p where :animal member of p.animals")
    boolean aUnProprietaire(@Param("animal") Animal animal);
}