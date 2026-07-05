package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

/**
 * Prenosni objekat (DTO) koji predstavlja podatke o korisniku vraćene
 * klijentu, bez osetljivih polja poput lozinke.
 *
 * @param userId jedinstveni identifikator korisnika
 * @param userEmail email adresa korisnika
 * @param firstName ime korisnika
 * @param lastName prezime korisnika
 * @param roleName naziv uloge dodeljene korisniku
 * @param isActive da li je nalog korisnika aktivan
 * @param createdAt datum i vreme registracije korisnika
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record UserDto(
        Long userId,
        String userEmail,
        String firstName,
        String lastName,
        String roleName,
        Boolean isActive,
        LocalDateTime createdAt
) {
}