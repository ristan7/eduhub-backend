package rs.ac.bg.fon.eduhub.service;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.dto.CreateEnrollmentRequest;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
import rs.ac.bg.fon.eduhub.dto.UpdateProgressRequest;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;
import rs.ac.bg.fon.eduhub.mapper.EnrollmentMapper;
import rs.ac.bg.fon.eduhub.repository.CourseRepository;
import rs.ac.bg.fon.eduhub.repository.EnrollmentRepository;
import rs.ac.bg.fon.eduhub.repository.EnrollmentStatusRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

@Service
public class EnrollmentService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentStatusRepository enrollmentStatusRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             EnrollmentStatusRepository enrollmentStatusRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository,
                             EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentStatusRepository = enrollmentStatusRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    // SO10 - Prijava studenta na kurs
    public EnrollmentDto enroll(CreateEnrollmentRequest request, Authentication authentication) {
        User student = currentUser(authentication);

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + request.courseId()));

        enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(student.getUserId(), course.getCourseId())
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Student is already enrolled in this course.");
                });

        EnrollmentStatus activeStatus = enrollmentStatusRepository.findByEnrollmentStatusName(ACTIVE_STATUS)
                .orElseThrow(() -> new IllegalStateException("Default enrollment status ACTIVE not found."));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentStatus(activeStatus);
        enrollment.setProgressPercentage(0);

        enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(enrollment);
    }

    // SO11 - Pregled prijavljenih kurseva studenta
    public List<EnrollmentDto> getMyEnrollments(Authentication authentication) {
        User student = currentUser(authentication);
        return enrollmentRepository.findByStudent_UserId(student.getUserId())
                .stream()
                .map(enrollmentMapper::toDto)
                .toList();
    }

    // SO18 - Praćenje napretka studenta
    public EnrollmentDto updateProgress(Long enrollmentId, UpdateProgressRequest request, Authentication authentication) {
        User student = currentUser(authentication);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + enrollmentId));

        if (enrollment.getStudent() == null || !enrollment.getStudent().getUserId().equals(student.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only update progress on your own enrollments.");
        }

        enrollment.setProgressPercentage(request.progressPercentage());

        if (request.progressPercentage() == 100) {
            EnrollmentStatus completedStatus = enrollmentStatusRepository.findByEnrollmentStatusName("COMPLETED")
                    .orElseThrow(() -> new IllegalStateException("Enrollment status COMPLETED not found."));
            enrollment.setEnrollmentStatus(completedStatus);
            enrollment.setCompletedAt(java.time.LocalDateTime.now());
        }

        enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(enrollment);
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }
}