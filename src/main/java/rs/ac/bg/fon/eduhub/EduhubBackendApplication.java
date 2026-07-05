package rs.ac.bg.fon.eduhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Glavna klasa Spring Boot aplikacije koja pokreće EduHub backend
 * servis za onlajn platformu za učenje.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@SpringBootApplication
public class EduhubBackendApplication {

    /**
     * Ulazna tačka aplikacije.
     *
     * @param args argumenti komandne linije
     */
    public static void main(String[] args) {
        SpringApplication.run(EduhubBackendApplication.class, args);
    }

}