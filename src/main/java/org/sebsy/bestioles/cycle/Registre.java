package org.sebsy.bestioles.cycle;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Registre {

    public List<String> pensionnaires() {
        return List.of("Rex", "Mistigri", "Caramel");
    }
}