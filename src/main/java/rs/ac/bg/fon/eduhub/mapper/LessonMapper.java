package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.LessonDto;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;

/**
 * Konvertuje {@link Lesson} entitete u odgovarajuće {@link LessonDto} objekte.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Component
public class LessonMapper {

    /**
     * Mapira entitet lekcije u DTO za slanje klijentu.
     *
     * @param lesson entitet lekcije, može biti {@code null}
     * @return {@link LessonDto} sa podacima lekcije, ili {@code null} ako je ulazni entitet {@code null}
     */
    public LessonDto toDto(Lesson lesson) {
        if (lesson == null) {
            return null;
        }
        return new LessonDto(
                lesson.getLessonId(),
                lesson.getLessonTitle(),
                lesson.getLessonOrderIndex(),
                lesson.getCreatedAt(),
                lesson.getIsAvailable(),
                lesson.getCourse() != null ? lesson.getCourse().getCourseId() : null,
                lesson.getLessonType() != null ? lesson.getLessonType().getLessonTypeId() : null,
                lesson.getLessonType() != null ? lesson.getLessonType().getLessonTypeName() : null
        );
    }
}