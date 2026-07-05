package rs.ac.bg.fon.eduhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.dto.PlatformStatisticsDto;
import rs.ac.bg.fon.eduhub.dto.UserDto;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;
import rs.ac.bg.fon.eduhub.mapper.CourseMapper;
import rs.ac.bg.fon.eduhub.mapper.UserMapper;
import rs.ac.bg.fon.eduhub.repository.CertificateRepository;
import rs.ac.bg.fon.eduhub.repository.CourseRepository;
import rs.ac.bg.fon.eduhub.repository.CourseStatusRepository;
import rs.ac.bg.fon.eduhub.repository.EnrollmentRepository;
import rs.ac.bg.fon.eduhub.repository.RoleRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

/**
 * JUnit testovi za servis {@link AdminService}, uz mokovanje svih
 * zavisnosti (SO21-SO24).
 *
 * @author Mihajlo Ristanovic
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseStatusRepository courseStatusRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CourseMapper courseMapper;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(userRepository, roleRepository, courseRepository,
                courseStatusRepository, enrollmentRepository, certificateRepository, userMapper, courseMapper);
    }

    @Test
    void testGetAllUsers() {
        User user = new User();
        UserDto dto = new UserDto(1L, "user@eduhub.com", "Petar", "Nikolic", "STUDENT", true, null);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        List<UserDto> result = adminService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetUserByIdSuccess() {
        User user = new User();
        UserDto dto = new UserDto(1L, "user@eduhub.com", "Petar", "Nikolic", "STUDENT", true, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = adminService.getUserById(1L);

        assertEquals(dto, result);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> adminService.getUserById(99L));
    }

    @Test
    void testSetUserActiveStatus() {
        User user = new User();
        user.setIsActive(true);
        UserDto expectedDto = new UserDto(1L, "user@eduhub.com", "Petar", "Nikolic", "STUDENT", false, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDto result = adminService.setUserActiveStatus(1L, false);

        assertEquals(false, user.getIsActive());
        assertEquals(expectedDto, result);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserRoleSuccess() {
        User user = new User();
        Role instructorRole = new Role(2L, "INSTRUCTOR");
        UserDto expectedDto = new UserDto(1L, "user@eduhub.com", "Petar", "Nikolic", "INSTRUCTOR", true, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(instructorRole));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDto result = adminService.updateUserRole(1L, 2L);

        assertEquals(instructorRole, user.getRole());
        assertEquals(expectedDto, result);
    }

    @Test
    void testUpdateUserRoleNotFound() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> adminService.updateUserRole(1L, 99L));
    }

    @Test
    void testApproveCourseSuccess() {
        Course course = new Course();
        CourseStatus publishedStatus = new CourseStatus(2L, "PUBLISHED");
        CourseDto expectedDto = new CourseDto(1L, "Java", "Opis", null, null, true, null, null, null, null, null, null, 2L, "PUBLISHED");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseStatusRepository.findByCourseStatusName("PUBLISHED")).thenReturn(Optional.of(publishedStatus));
        when(courseMapper.toDto(course)).thenReturn(expectedDto);

        CourseDto result = adminService.approveCourse(1L);

        assertEquals(publishedStatus, course.getCourseStatus());
        assertEquals(true, course.getIsPublished());
        assertEquals(expectedDto, result);
    }

    @Test
    void testBlockCourseSuccess() {
        Course course = new Course();
        CourseStatus archivedStatus = new CourseStatus(3L, "ARCHIVED");
        CourseDto expectedDto = new CourseDto(1L, "Java", "Opis", null, null, false, null, null, null, null, null, null, 3L, "ARCHIVED");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseStatusRepository.findByCourseStatusName("ARCHIVED")).thenReturn(Optional.of(archivedStatus));
        when(courseMapper.toDto(course)).thenReturn(expectedDto);

        CourseDto result = adminService.blockCourse(1L);

        assertEquals(archivedStatus, course.getCourseStatus());
        assertEquals(false, course.getIsPublished());
        assertEquals(expectedDto, result);
    }

    @Test
    void testGetPlatformStatistics() {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByRole_RoleName("STUDENT")).thenReturn(7L);
        when(userRepository.countByRole_RoleName("INSTRUCTOR")).thenReturn(2L);
        when(userRepository.countByRole_RoleName("ADMIN")).thenReturn(1L);
        when(courseRepository.count()).thenReturn(5L);
        when(courseRepository.countByCourseStatus_CourseStatusName("PUBLISHED")).thenReturn(3L);
        when(courseRepository.countByCourseStatus_CourseStatusName("DRAFT")).thenReturn(1L);
        when(courseRepository.countByCourseStatus_CourseStatusName("ARCHIVED")).thenReturn(1L);
        when(enrollmentRepository.count()).thenReturn(20L);
        when(certificateRepository.count()).thenReturn(4L);

        PlatformStatisticsDto result = adminService.getPlatformStatistics();

        assertEquals(10L, result.totalUsers());
        assertEquals(7L, result.totalStudents());
        assertEquals(2L, result.totalInstructors());
        assertEquals(1L, result.totalAdmins());
        assertEquals(5L, result.totalCourses());
        assertEquals(3L, result.totalPublishedCourses());
        assertEquals(1L, result.totalDraftCourses());
        assertEquals(1L, result.totalArchivedCourses());
        assertEquals(20L, result.totalEnrollments());
        assertEquals(4L, result.totalCertificatesIssued());
    }
}