package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.bg.fon.eduhub.entity.impl.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE "
            + "(:keyword IS NULL OR LOWER(c.courseTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
            + "(:categoryId IS NULL OR c.courseCategory.courseCategoryId = :categoryId) AND "
            + "(:levelId IS NULL OR c.courseLevel.courseLevelId = :levelId)")
    List<Course> search(@Param("keyword") String keyword,
                        @Param("categoryId") Long categoryId,
                        @Param("levelId") Long levelId);

    long countByCourseStatus_CourseStatusName(String courseStatusName);
}