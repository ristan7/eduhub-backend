package rs.ac.bg.fon.eduhub.service;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.dto.CreateCourseRequest;
import rs.ac.bg.fon.eduhub.dto.UpdateCourseRequest;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseCategory;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseLevel;
import rs.ac.bg.fon.eduhub.entity.lookup.CourseStatus;
import rs.ac.bg.fon.eduhub.mapper.CourseMapper;
import rs.ac.bg.fon.eduhub.repository.CourseCategoryRepository;
import rs.ac.bg.fon.eduhub.repository.CourseLevelRepository;
import rs.ac.bg.fon.eduhub.repository.CourseRepository;
import rs.ac.bg.fon.eduhub.repository.CourseStatusRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

@Service
public class CourseService {

    private static final String DEFAULT_STATUS = "DRAFT";
    private static final String ARCHIVED_STATUS = "ARCHIVED";

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseLevelRepository courseLevelRepository;
    private final CourseStatusRepository courseStatusRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository,
                         CourseCategoryRepository courseCategoryRepository,
                         CourseLevelRepository courseLevelRepository,
                         CourseStatusRepository courseStatusRepository,
                         UserRepository userRepository,
                         CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.courseLevelRepository = courseLevelRepository;
        this.courseStatusRepository = courseStatusRepository;
        this.userRepository = userRepository;
        this.courseMapper = courseMapper;
    }

    // SO4 - Pregled svih kurseva
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(courseMapper::toDto).toList();
    }

    // SO5 - Pretraga i filtriranje kurseva
    public List<CourseDto> searchCourses(String keyword, Long categoryId, Long levelId) {
        return courseRepository.search(keyword, categoryId, levelId).stream()
                .map(courseMapper::toDto)
                .toList();
    }

    // SO6 - Pregled detalja kursa
    public CourseDto getCourseById(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        return courseMapper.toDto(course);
    }

    // SO7 - Kreiranje novog kursa
    public CourseDto createCourse(CreateCourseRequest request, Authentication authentication) {
        User author = currentUser(authentication);

        CourseCategory category = courseCategoryRepository.findById(request.courseCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Course category not found: " + request.courseCategoryId()));
        CourseLevel level = courseLevelRepository.findById(request.courseLevelId())
                .orElseThrow(() -> new IllegalArgumentException("Course level not found: " + request.courseLevelId()));
        CourseStatus draftStatus = courseStatusRepository.findByCourseStatusName(DEFAULT_STATUS)
                .orElseThrow(() -> new IllegalStateException("Default course status DRAFT not found."));

        Course course = new Course();
        course.setCourseTitle(request.courseTitle());
        course.setCourseDescription(request.courseDescription());
        course.setCourseCategory(category);
        course.setCourseLevel(level);
        course.setCourseStatus(draftStatus);
        course.setIsPublished(false);
        course.setAuthor(author);

        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    // SO8 - Izmena podataka o kursu
    public CourseDto updateCourse(Long courseId, UpdateCourseRequest request, Authentication authentication) {
        Course course = findCourseOrThrow(courseId);
        requireOwnerOrAdmin(course, authentication);

        CourseCategory category = courseCategoryRepository.findById(request.courseCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Course category not found: " + request.courseCategoryId()));
        CourseLevel level = courseLevelRepository.findById(request.courseLevelId())
                .orElseThrow(() -> new IllegalArgumentException("Course level not found: " + request.courseLevelId()));

        course.setCourseTitle(request.courseTitle());
        course.setCourseDescription(request.courseDescription());
        course.setCourseCategory(category);
        course.setCourseLevel(level);

        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    // SO9 - Brisanje ili deaktivacija kursa (soft-delete: status -> ARCHIVED)
    public void deactivateCourse(Long courseId, Authentication authentication) {
        Course course = findCourseOrThrow(courseId);
        requireOwnerOrAdmin(course, authentication);

        CourseStatus archived = courseStatusRepository.findByCourseStatusName(ARCHIVED_STATUS)
                .orElseThrow(() -> new IllegalStateException("Course status ARCHIVED not found."));

        course.setCourseStatus(archived);
        course.setIsPublished(false);
        courseRepository.save(course);
    }

    private Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }

    private void requireOwnerOrAdmin(Course course, Authentication authentication) {
        User currentUser = currentUser(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = course.getAuthor() != null
                && course.getAuthor().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Only the course author or an admin can modify this course.");
        }
    }
}