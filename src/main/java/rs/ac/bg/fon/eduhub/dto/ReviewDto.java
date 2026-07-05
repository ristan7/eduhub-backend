package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

/**
 * Prenosni objekat (DTO) koji predstavlja podatke o oceni kursa vraćene
 * klijentu.
 *
 * @param reviewId jedinstveni identifikator ocene
 * @param rating numerička ocena kursa, u opsegu od 1 do 5
 * @param comment tekstualni komentar, ako postoji
 * @param createdAt datum i vreme kreiranja ocene
 * @param enrollmentId identifikator prijave na koju se ocena odnosi
 * @param courseId identifikator ocenjenog kursa
 * @param courseTitle naslov ocenjenog kursa
 * @param studentId identifikator studenta koji je ostavio ocenu
 * @param studentFullName puno ime i prezime studenta
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record ReviewDto(
        Long reviewId,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        Long studentId,
        String studentFullName
) {
}