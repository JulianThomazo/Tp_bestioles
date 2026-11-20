package org.sebsy.bestioles.demo;

import java.util.List;

import org.sebsy.bestioles.model.Animal;
import org.sebsy.bestioles.model.Sex;
import org.sebsy.bestioles.model.Species;
import org.sebsy.bestioles.repository.AnimalRepository;
import org.sebsy.bestioles.repository.PersonRepository;
import org.sebsy.bestioles.repository.SpeciesRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("requetes")
public class DemoRequetes implements ApplicationRunner {

    private final SpeciesRepository species;
    private final AnimalRepository animals;
    private final PersonRepository persons;

    public DemoRequetes(SpeciesRepository species, AnimalRepository animals, PersonRepository persons) {
        this.species = species;
        this.animals = animals;
        this.persons = persons;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Étape 3 : requêtes dérivées
        afficher("findFirstByCommonName(Chat)", species.findFirstByCommonName("Chat"));
        afficher("findByLatinNameContainingIgnoreCase(LUPUS)", species.findByLatinNameContainingIgnoreCase("LUPUS"));
        afficher("findByLastnameOrFirstname(Martin, Karim)", persons.findByLastnameOrFirstname("Martin", "Karim"));
        afficher("findByAgeGreaterThanEqual(30)", persons.findByAgeGreaterThanEqual(30));
        afficher("findByActiveTrue()", persons.findByActiveTrue());

        Species chat = species.findFirstByCommonName("Chat").orElseThrow();
        afficher("findBySpecies(Chat)", animals.findBySpecies(chat));
        afficher("findBySpeciesCommonName(Lapin)", animals.findBySpeciesCommonName("Lapin"));
        afficher("findByColorIn([Roux, Gris])", animals.findByColorIn(List.of("Roux", "Gris")));
        afficher("countBySex(F)", animals.countBySex(Sex.F));

        // Étape 6 : @Query en JPQL
        afficher("toutesTrieesParNomCommun()", species.toutesTrieesParNomCommun());
        afficher("nomCommunCommeMotif(C%)", species.nomCommunCommeMotif("C%"));
        afficher("findAgedBetween(25, 40)", persons.findAgedBetween(25, 40));

        // Étape 11 : SQL natif
        afficher("commencantPar(Ca)", animals.commencantPar("Ca"));

        // Étape 7 : trois critères
        afficher("findBySexAndSpeciesCommonNameAndColorIsNotNull(M, Chien)",
                animals.findBySexAndSpeciesCommonNameAndColorIsNotNull(Sex.M, "Chien"));
        afficher("malesColoresDUneEspece(M, Chien)", animals.malesColoresDUneEspece(Sex.M, "Chien"));

        // Étape 8 : propriétaires de Mia
        Animal mia = animals.findById(2).orElseThrow();
        afficher("findOwnersOf(Mia)", persons.findOwnersOf(mia));

        // Étape 9 : animaux sans propriétaire
        afficher("sansProprietaire()", animals.sansProprietaire());
        Animal caramel = animals.findById(3).orElseThrow();
        afficher("aUnProprietaire(Caramel)", animals.aUnProprietaire(caramel));
        afficher("aUnProprietaire(Mia)", animals.aUnProprietaire(mia));
    }

    private static void afficher(String appel, Object resultat) {
        System.out.println(">>> " + appel + " = " + resultat);
    }
}