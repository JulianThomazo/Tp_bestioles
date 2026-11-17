package org.sebsy.bestioles.cycle;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Refuge {

    private final Registre registre;
    private final Veterinaire veterinaire;

    public Refuge(Registre registre, Veterinaire veterinaire) {
        this.registre = registre;
        this.veterinaire = veterinaire;
    }

    public List<String> pensionnaires() {
        return registre.pensionnaires();
    }

    public String admettre(String animal) {
        return veterinaire.examiner(animal);
    }
}