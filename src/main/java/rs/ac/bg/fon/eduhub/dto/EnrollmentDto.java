package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

/**
 * Prenosni objekat (DTO) koji predstavlja podatke o prijavi studenta na
 * kurs vraćene klijentu.
 *
 * @param enrollmentId jedinstveni identifikator prijave
 * @param enrolledAt datum i vreme prijave na kurs
 * @param progressPercentage procenat napretka studenta kroz kurs
 * @param completedAt datum i vreme završetka kursa, ako je kurs završen
 * @param studentId identifikator studenta
 * @param studentFullName puno ime i prezime studenta
 * @param courseId identifikator kursa
 * @param courseTitle naslov kursa
 * @param enrollmentStatusId identifikator statusa prijave
 * @param enrollmentStatusName naziv statusa prijave
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record EnrollmentDto(
        Long enrollmentId,
        LocalDateTime enrolledAt,
        Integer progressPercentage,
        LocalDateTime completedAt,
        Long studentId,
        String studentFullName,
        Long courseId,
        String courseTitle,
        Long enrollmentStatusId,
        String enrollmentStatusName
) {
}