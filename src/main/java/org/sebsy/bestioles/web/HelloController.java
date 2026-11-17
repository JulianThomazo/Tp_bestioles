package org.sebsy.bestioles.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HelloController {

    @GetMapping("/hello")
    String hello(@RequestParam(name = "name", defaultValue = "monde") String name) {
        return "Bonjour, " + name + " !";
    }
}