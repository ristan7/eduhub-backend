package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.bg.fon.eduhub.entity.impl.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByEnrollment_EnrollmentId(Long enrollmentId);

    @Query("SELECT r FROM Review r WHERE r.enrollment.course.courseId = :courseId")
    List<Review> findByCourseId(@Param("courseId") Long courseId);
}