package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.entity.impl.Course;

/**
 * Konvertuje {@link Course} entitete u odgovarajuće {@link CourseDto} objekte.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Component
public class CourseMapper {

    /**
     * Mapira entitet kursa u DTO za slanje klijentu.
     *
     * @param course entitet kursa, može biti {@code null}
     * @return {@link CourseDto} sa podacima kursa, ili {@code null} ako je ulazni entitet {@code null}
     */
    public CourseDto toDto(Course course) {
        if (course == null) {
            return null;
        }
        return new CourseDto(
                course.getCourseId(),
                course.getCourseTitle(),
                course.getCourseDescription(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                course.getIsPublished(),
                course.getAuthor() != null ? course.getAuthor().getUserId() : null,
                course.getAuthor() != null ? course.getAuthor().getFirstName() + " " + course.getAuthor().getLastName() : null,
                course.getCourseCategory() != null ? course.getCourseCategory().getCourseCategoryId() : null,
                course.getCourseCategory() != null ? course.getCourseCategory().getCourseCategoryName() : null,
                course.getCourseLevel() != null ? course.getCourseLevel().getCourseLevelId() : null,
                course.getCourseLevel() != null ? course.getCourseLevel().getCourseLevelName() : null,
                course.getCourseStatus() != null ? course.getCourseStatus().getCourseStatusId() : null,
                course.getCourseStatus() != null ? course.getCourseStatus().getCourseStatusName() : null
        );
    }
}