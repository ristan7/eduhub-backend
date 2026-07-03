package rs.ac.bg.fon.eduhub.mapper;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.entity.impl.Course;

@Component
public class CourseMapper {

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