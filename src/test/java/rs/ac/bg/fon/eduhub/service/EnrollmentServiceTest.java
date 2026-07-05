package rs.ac.bg.fon.eduhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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

/**
 * JUnit testovi za servis {@link EnrollmentService}, uz mokovanje svih
 * zavisnosti (SO10, SO11, SO18).
 *
 * @author Mihajlo Ristanovic
 */
@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private EnrollmentStatusRepository enrollmentStatusRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService(enrollmentRepository, enrollmentStatusRepository,
                courseRepository, userRepository, enrollmentMapper);
    }

    @Test
    void testEnrollSuccess() {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(1L);
        Authentication authentication = mock(Authentication.class);

        User student = new User();
        student.setUserId(1L);

        Course course = new Course();
        course.setCourseId(1L);

        EnrollmentStatus activeStatus = new EnrollmentStatus(1L, "ACTIVE");
        EnrollmentDto expectedDto = new EnrollmentDto(1L, null, 0, null, 1L, "Petar Nikolic", 1L, "Java", 1L, "ACTIVE");

        when(authentication.getName()).thenReturn("student@eduhub.com");
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(1L, 1L)).thenReturn(Optional.empty());
        when(enrollmentStatusRepository.findByEnrollmentStatusName("ACTIVE")).thenReturn(Optional.of(activeStatus));
        when(enrollmentMapper.toDto(any(Enrollment.class))).thenReturn(expectedDto);

        EnrollmentDto result = enrollmentService.enroll(request, authentication);

        assertEquals(expectedDto, result);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void testEnrollAlreadyEnrolled() {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(1L);
        Authentication authentication = mock(Authentication.class);

        User student = new User();
        student.setUserId(1L);

        Course course = new Course();
        course.setCourseId(1L);

        when(authentication.getName()).thenReturn("student@eduhub.com");
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(1L, 1L)).thenReturn(Optional.of(new Enrollment()));

        assertThrows(IllegalArgumentException.class, () -> enrollmentService.enroll(request, authentication));
    }

    @Test
    void testEnrollCourseNotFound() {
        CreateEnrollmentRequest request = new CreateEnrollmentRequest(99L);
        Authentication authentication = mock(Authentication.class);

        User student = new User();
        student.setUserId(1L);

        when(authentication.getName()).thenReturn("student@eduhub.com");
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> enrollmentService.enroll(request, authentication));
    }

    @Test
    void testGetMyEnrollments() {
        Authentication authentication = mock(Authentication.class);
        User student = new User();
        student.setUserId(1L);

        Enrollment enrollment = new Enrollment();
        EnrollmentDto dto = new EnrollmentDto(1L, null, 0, null, 1L, "Petar Nikolic", 1L, "Java", 1L, "ACTIVE");

        when(authentication.getName()).thenReturn("student@eduhub.com");
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(student));
        when(enrollmentRepository.findByStudent_UserId(1L)).thenReturn(List.of(enrollment));
        when(enrollmentMapper.toDto(enrollment)).thenReturn(dto);

        List<EnrollmentDto> result = enrollmentService.getMyEnrollments(authentication);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testUpdateProgressSuccess() {
        Long enrollmentId = 1L;
        UpdateProgressRequest request = new UpdateProgressRequest(45);
        Authentication authentication = mock(Authentication.class);

        User student = new User();
        student.setUserId(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setStudent(student);

        EnrollmentDto expectedDto = new EnrollmentDto(1L, null, 45, null, 1L, "Petar Nikolic", 1L, "Java", 1L, "ACTIVE");

        when(authentication.getName()).thenReturn("student@eduhub.com");
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(student));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(enrollmentMapper.toDto(enrollment)).thenReturn(expectedDto);

        EnrollmentDto result = enrollmentService.updateProgress(enrollmentId, request, authentication);

        assertEquals(45, enrollment.getProgressPercentage());
        assertEquals(expectedDto, result);
    }

    @Test
    void testUpdateProgressTo100SetsCompletedStatus() {
        Long enrollmentId = 1L;
        UpdateProgressRequest request = new UpdateProgressRequest(100);
        Authentication authentication = mock(Authentication.class);

        User student = new User();
        student.setUserId(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setStudent(student);

        EnrollmentStatus completedStatus = new EnrollmentStatus(2L, "COMPLETED");
        EnrollmentDto expectedDto = new EnrollmentDto(1L, null, 100, null, 1L, "Petar Nikolic", 1L, "Java", 2L, "COMPLETED");

        when(authentication.getName()).thenReturn("student@eduhub.com");
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(student));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(enrollmentStatusRepository.findByEnrollmentStatusName("COMPLETED")).thenReturn(Optional.of(completedStatus));
        when(enrollmentMapper.toDto(enrollment)).thenReturn(expectedDto);

        enrollmentService.updateProgress(enrollmentId, request, authentication);

        assertEquals(completedStatus, enrollment.getEnrollmentStatus());
        assertEquals(100, enrollment.getProgressPercentage());
    }

    @Test
    void testUpdateProgressForbiddenForNonOwner() {
        Long enrollmentId = 1L;
        UpdateProgressRequest request = new UpdateProgressRequest(50);
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);
        User intruder = new User();
        intruder.setUserId(2L);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setStudent(owner);

        when(authentication.getName()).thenReturn("intruder@eduhub.com");
        when(userRepository.findByUserEmail("intruder@eduhub.com")).thenReturn(Optional.of(intruder));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        assertThrows(AccessDeniedException.class, () -> enrollmentService.updateProgress(enrollmentId, request, authentication));
    }
}