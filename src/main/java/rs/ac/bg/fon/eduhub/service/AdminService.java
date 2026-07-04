package rs.ac.bg.fon.eduhub.service;

import java.util.List;
import org.springframework.stereotype.Service;
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

@Service
public class AdminService {

    private static final String PUBLISHED_STATUS = "PUBLISHED";
    private static final String ARCHIVED_STATUS = "ARCHIVED";
    private static final String DRAFT_STATUS = "DRAFT";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CourseRepository courseRepository;
    private final CourseStatusRepository courseStatusRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;

    public AdminService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        CourseRepository courseRepository,
                        CourseStatusRepository courseStatusRepository,
                        EnrollmentRepository enrollmentRepository,
                        CertificateRepository certificateRepository,
                        UserMapper userMapper,
                        CourseMapper courseMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.courseRepository = courseRepository;
        this.courseStatusRepository = courseStatusRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.certificateRepository = certificateRepository;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
    }

    // SO21 - Upravljanje korisnicima (pregled svih)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    // SO21 - Upravljanje korisnicima (pregled jednog + blokiranje/aktivacija naloga)
    public UserDto getUserById(Long userId) {
        return userMapper.toDto(findUserOrThrow(userId));
    }

    public UserDto setUserActiveStatus(Long userId, boolean isActive) {
        User user = findUserOrThrow(userId);
        user.setIsActive(isActive);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    // SO22 - Dodela uloga korisnicima
    public UserDto updateUserRole(Long userId, Long roleId) {
        User user = findUserOrThrow(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
        user.setRole(role);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    // SO23 - Odobravanje kursa
    public CourseDto approveCourse(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        CourseStatus published = courseStatusRepository.findByCourseStatusName(PUBLISHED_STATUS)
                .orElseThrow(() -> new IllegalStateException("Course status PUBLISHED not found."));
        course.setCourseStatus(published);
        course.setIsPublished(true);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    // SO23 - Blokiranje kursa
    public CourseDto blockCourse(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        CourseStatus archived = courseStatusRepository.findByCourseStatusName(ARCHIVED_STATUS)
                .orElseThrow(() -> new IllegalStateException("Course status ARCHIVED not found."));
        course.setCourseStatus(archived);
        course.setIsPublished(false);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    // SO24 - Pregled statistike platforme
    public PlatformStatisticsDto getPlatformStatistics() {
        return new PlatformStatisticsDto(
                userRepository.count(),
                userRepository.countByRole_RoleName("STUDENT"),
                userRepository.countByRole_RoleName("INSTRUCTOR"),
                userRepository.countByRole_RoleName("ADMIN"),
                courseRepository.count(),
                courseRepository.countByCourseStatus_CourseStatusName(PUBLISHED_STATUS),
                courseRepository.countByCourseStatus_CourseStatusName(DRAFT_STATUS),
                courseRepository.countByCourseStatus_CourseStatusName(ARCHIVED_STATUS),
                enrollmentRepository.count(),
                certificateRepository.count()
        );
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
    }
}