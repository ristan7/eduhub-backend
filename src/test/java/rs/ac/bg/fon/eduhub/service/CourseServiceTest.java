package rs.ac.bg.fon.eduhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

/**
 * JUnit testovi za servis {@link CourseService}, uz mokovanje svih
 * zavisnosti (SO4-SO9).
 *
 * @author Mihajlo Ristanovic
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseCategoryRepository courseCategoryRepository;

    @Mock
    private CourseLevelRepository courseLevelRepository;

    @Mock
    private CourseStatusRepository courseStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseMapper courseMapper;

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(courseRepository, courseCategoryRepository,
                courseLevelRepository, courseStatusRepository, userRepository, courseMapper);
    }

    @Test
    void testGetAllCourses() {
        Course course = new Course();
        CourseDto dto = new CourseDto(1L, "Java", "Opis", null, null, false, null, null, null, null, null, null, null, null);

        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(courseMapper.toDto(course)).thenReturn(dto);

        List<CourseDto> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testSearchCourses() {
        when(courseRepository.search("java", 1L, 1L)).thenReturn(List.of());

        List<CourseDto> result = courseService.searchCourses("java", 1L, 1L);

        assertEquals(0, result.size());
    }

    @Test
    void testGetCourseByIdSuccess() {
        Course course = new Course();
        CourseDto dto = new CourseDto(1L, "Java", "Opis", null, null, false, null, null, null, null, null, null, null, null);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseMapper.toDto(course)).thenReturn(dto);

        CourseDto result = courseService.getCourseById(1L);

        assertEquals(dto, result);
    }

    @Test
    void testGetCourseByIdNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> courseService.getCourseById(99L));
    }

    @Test
    void testCreateCourseSuccess() {
        CreateCourseRequest request = new CreateCourseRequest("Java Osnove", "Opis", 1L, 1L);
        Authentication authentication = mock(Authentication.class);
        User author = new User();
        author.setUserId(1L);
        CourseCategory category = new CourseCategory(1L, "PROGRAMMING");
        CourseLevel level = new CourseLevel(1L, "BEGINNER");
        CourseStatus draftStatus = new CourseStatus(1L, "DRAFT");
        CourseDto expectedDto = new CourseDto(1L, "Java Osnove", "Opis", null, null, false, 1L, "A A", 1L, "PROGRAMMING", 1L, "BEGINNER", 1L, "DRAFT");

        when(authentication.getName()).thenReturn("author@eduhub.com");
        when(userRepository.findByUserEmail("author@eduhub.com")).thenReturn(Optional.of(author));
        when(courseCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(courseLevelRepository.findById(1L)).thenReturn(Optional.of(level));
        when(courseStatusRepository.findByCourseStatusName("DRAFT")).thenReturn(Optional.of(draftStatus));
        when(courseMapper.toDto(any(Course.class))).thenReturn(expectedDto);

        CourseDto result = courseService.createCourse(request, authentication);

        assertEquals(expectedDto, result);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void testCreateCourseCategoryNotFound() {
        CreateCourseRequest request = new CreateCourseRequest("Java Osnove", "Opis", 99L, 1L);
        Authentication authentication = mock(Authentication.class);
        User author = new User();
        author.setUserId(1L);

        when(authentication.getName()).thenReturn("author@eduhub.com");
        when(userRepository.findByUserEmail("author@eduhub.com")).thenReturn(Optional.of(author));
        when(courseCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(request, authentication));
    }

    @Test
    void testUpdateCourseAsOwnerSuccess() {
        Long courseId = 1L;
        UpdateCourseRequest request = new UpdateCourseRequest("Novi naslov", "Novi opis", 1L, 1L);
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);

        Course course = new Course();
        course.setCourseId(courseId);
        course.setAuthor(owner);

        CourseCategory category = new CourseCategory(1L, "PROGRAMMING");
        CourseLevel level = new CourseLevel(1L, "BEGINNER");
        CourseDto expectedDto = new CourseDto(1L, "Novi naslov", "Novi opis", null, null, false, 1L, "A A", 1L, "PROGRAMMING", 1L, "BEGINNER", null, null);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(authentication.getName()).thenReturn("owner@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();
        when(userRepository.findByUserEmail("owner@eduhub.com")).thenReturn(Optional.of(owner));
        when(courseCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(courseLevelRepository.findById(1L)).thenReturn(Optional.of(level));
        when(courseMapper.toDto(course)).thenReturn(expectedDto);

        CourseDto result = courseService.updateCourse(courseId, request, authentication);

        assertEquals(expectedDto, result);
    }

    @Test
    void testUpdateCourseForbiddenForNonOwnerNonAdmin() {
        Long courseId = 1L;
        UpdateCourseRequest request = new UpdateCourseRequest("Novi naslov", "Novi opis", 1L, 1L);
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);
        User intruder = new User();
        intruder.setUserId(2L);

        Course course = new Course();
        course.setCourseId(courseId);
        course.setAuthor(owner);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(authentication.getName()).thenReturn("intruder@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();
        when(userRepository.findByUserEmail("intruder@eduhub.com")).thenReturn(Optional.of(intruder));

        assertThrows(AccessDeniedException.class, () -> courseService.updateCourse(courseId, request, authentication));
    }

    @Test
    void testUpdateCourseAllowedForAdmin() {
        Long courseId = 1L;
        UpdateCourseRequest request = new UpdateCourseRequest("Novi naslov", "Novi opis", 1L, 1L);
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);
        User admin = new User();
        admin.setUserId(2L);

        Course course = new Course();
        course.setCourseId(courseId);
        course.setAuthor(owner);

        CourseCategory category = new CourseCategory(1L, "PROGRAMMING");
        CourseLevel level = new CourseLevel(1L, "BEGINNER");
        CourseDto expectedDto = new CourseDto(1L, "Novi naslov", "Novi opis", null, null, false, 1L, "A A", 1L, "PROGRAMMING", 1L, "BEGINNER", null, null);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(authentication.getName()).thenReturn("admin@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(authentication).getAuthorities();
        when(userRepository.findByUserEmail("admin@eduhub.com")).thenReturn(Optional.of(admin));
        when(courseCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(courseLevelRepository.findById(1L)).thenReturn(Optional.of(level));
        when(courseMapper.toDto(course)).thenReturn(expectedDto);

        CourseDto result = courseService.updateCourse(courseId, request, authentication);

        assertEquals(expectedDto, result);
    }

    @Test
    void testDeactivateCourseSuccess() {
        Long courseId = 1L;
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);

        Course course = new Course();
        course.setCourseId(courseId);
        course.setAuthor(owner);

        CourseStatus archivedStatus = new CourseStatus(3L, "ARCHIVED");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(authentication.getName()).thenReturn("owner@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();
        when(userRepository.findByUserEmail("owner@eduhub.com")).thenReturn(Optional.of(owner));
        when(courseStatusRepository.findByCourseStatusName("ARCHIVED")).thenReturn(Optional.of(archivedStatus));

        courseService.deactivateCourse(courseId, authentication);

        assertEquals(archivedStatus, course.getCourseStatus());
        assertEquals(false, course.getIsPublished());
        verify(courseRepository).save(course);
    }
}