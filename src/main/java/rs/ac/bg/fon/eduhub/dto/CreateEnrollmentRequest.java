package rs.ac.bg.fon.eduhub.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Zahtev za prijavu studenta na kurs (SO10).
 *
 * @param courseId identifikator kursa na koji se student prijavljuje
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record CreateEnrollmentRequest(
        @NotNull Long courseId
) {
}