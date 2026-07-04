package rs.ac.bg.fon.eduhub.dto;

import java.time.LocalDateTime;

/**
 * Prenosni objekat (DTO) koji predstavlja podatke o kursu vraćene
 * klijentu.
 *
 * @param courseId jedinstveni identifikator kursa
 * @param courseTitle naslov kursa
 * @param courseDescription opis kursa
 * @param createdAt datum i vreme kreiranja kursa
 * @param updatedAt datum i vreme poslednje izmene kursa
 * @param isPublished da li je kurs javno objavljen
 * @param authorId identifikator autora (instruktora) kursa
 * @param authorFullName puno ime i prezime autora kursa
 * @param courseCategoryId identifikator kategorije kursa
 * @param courseCategoryName naziv kategorije kursa
 * @param courseLevelId identifikator nivoa kursa
 * @param courseLevelName naziv nivoa kursa
 * @param courseStatusId identifikator statusa kursa
 * @param courseStatusName naziv statusa kursa
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record CourseDto(
        Long courseId,
        String courseTitle,
        String courseDescription,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isPublished,
        Long authorId,
        String authorFullName,
        Long courseCategoryId,
        String courseCategoryName,
        Long courseLevelId,
        String courseLevelName,
        Long courseStatusId,
        String courseStatusName
) {
}