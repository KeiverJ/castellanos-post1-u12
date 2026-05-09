package co.edu.udes.castellanos.post1u12.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String inicio() {
        return "API Castellanos Post1 U12\n" +
                "Health: /actuator/health\n" +
                "Productos: /api/productos";
    }
}