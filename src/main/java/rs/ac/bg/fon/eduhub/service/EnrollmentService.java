package rs.ac.bg.fon.eduhub.service;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.dto.CreateEnrollmentRequest;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
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

    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }
}