package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

/**
 * Prenosni objekat (DTO) koji predstavlja podatke o sertifikatu vraćene
 * klijentu.
 *
 * @param certificateId jedinstveni identifikator sertifikata
 * @param code jedinstveni kod sertifikata
 * @param issuedAt datum i vreme izdavanja sertifikata
 * @param certificateUrl URL adresa dokumenta sertifikata, ako postoji
 * @param enrollmentId identifikator prijave za koju je sertifikat izdat
 * @param courseId identifikator kursa
 * @param courseTitle naslov kursa
 * @param studentId identifikator studenta kome je sertifikat izdat
 * @param studentFullName puno ime i prezime studenta
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record CertificateDto(
        Long certificateId,
        String code,
        LocalDateTime issuedAt,
        String certificateUrl,
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        Long studentId,
        String studentFullName
) {
}