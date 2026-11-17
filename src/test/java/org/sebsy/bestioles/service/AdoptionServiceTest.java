package org.sebsy.bestioles.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sebsy.bestioles.notif.Notifier;

class AdoptionServiceTest {

    @Test
    void adopterEnvoieLaDateDuJourDansLeFuseauDeLHorloge() {
        List<String> messages = new ArrayList<>();
        Notifier enregistreur = (to, message) -> messages.add(to + " <- " + message);
        Clock horloge = Clock.fixed(Instant.parse("2026-12-31T23:30:00Z"), ZoneId.of("Europe/Paris"));

        AdoptionService service = new AdoptionService(enregistreur, horloge);

        LocalDate date = service.adopter("Rex", "lea@example.org");

        assertEquals(LocalDate.of(2027, 1, 1), date);
        assertEquals(List.of("lea@example.org <- Adoption de Rex confirmée le 01/01/2027"), messages);
    }
}