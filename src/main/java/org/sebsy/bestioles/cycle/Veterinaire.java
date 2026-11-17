package org.sebsy.bestioles.cycle;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Veterinaire {

    private final Registre registre;

    public Veterinaire(Registre registre) {
        this.registre = registre;
    }

    public String examiner(String animal) {
        return animal + " examiné";
    }

    public List<String> tourneeDeVaccination() {
        return registre.pensionnaires().stream()
                .map(nom -> nom + " vacciné")
                .toList();
    }
}