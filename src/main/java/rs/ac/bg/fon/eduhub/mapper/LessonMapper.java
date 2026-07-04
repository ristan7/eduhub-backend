package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.LessonDto;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;

@Component
public class LessonMapper {

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