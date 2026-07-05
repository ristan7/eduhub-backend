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
import rs.ac.bg.fon.eduhub.dto.CreateLessonRequest;
import rs.ac.bg.fon.eduhub.dto.LessonDto;
import rs.ac.bg.fon.eduhub.dto.UpdateLessonRequest;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.LessonType;
import rs.ac.bg.fon.eduhub.mapper.LessonMapper;
import rs.ac.bg.fon.eduhub.repository.CourseRepository;
import rs.ac.bg.fon.eduhub.repository.LessonRepository;
import rs.ac.bg.fon.eduhub.repository.LessonTypeRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

/**
 * JUnit testovi za servis {@link LessonService}, uz mokovanje svih
 * zavisnosti (SO12-SO15).
 *
 * @author Mihajlo Ristanovic
 */
@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonTypeRepository lessonTypeRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LessonMapper lessonMapper;

    private LessonService lessonService;

    @BeforeEach
    void setUp() {
        lessonService = new LessonService(lessonRepository, lessonTypeRepository,
                courseRepository, userRepository, lessonMapper);
    }

    @Test
    void testGetLessonsByCourseSuccess() {
        Long courseId = 1L;
        Course course = new Course();
        Lesson lesson = new Lesson();
        LessonDto dto = new LessonDto(1L, "Uvod", 1, null, true, courseId, 1L, "VIDEO");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.findByCourse_CourseIdOrderByLessonOrderIndexAsc(courseId)).thenReturn(List.of(lesson));
        when(lessonMapper.toDto(lesson)).thenReturn(dto);

        List<LessonDto> result = lessonService.getLessonsByCourse(courseId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetLessonsByCourseNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lessonService.getLessonsByCourse(99L));
    }

    @Test
    void testAddLessonSuccess() {
        Long courseId = 1L;
        CreateLessonRequest request = new CreateLessonRequest("Uvod", 1, 1L);
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);

        Course course = new Course();
        course.setCourseId(courseId);
        course.setAuthor(owner);

        LessonType lessonType = new LessonType(1L, "VIDEO");
        LessonDto expectedDto = new LessonDto(1L, "Uvod", 1, null, true, courseId, 1L, "VIDEO");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findByUserEmail("owner@eduhub.com")).thenReturn(Optional.of(owner));
        when(authentication.getName()).thenReturn("owner@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();
        when(lessonTypeRepository.findById(1L)).thenReturn(Optional.of(lessonType));
        when(lessonMapper.toDto(any(Lesson.class))).thenReturn(expectedDto);

        LessonDto result = lessonService.addLesson(courseId, request, authentication);

        assertEquals(expectedDto, result);
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    void testAddLessonForbiddenForNonOwner() {
        Long courseId = 1L;
        CreateLessonRequest request = new CreateLessonRequest("Uvod", 1, 1L);
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);
        User intruder = new User();
        intruder.setUserId(2L);

        Course course = new Course();
        course.setCourseId(courseId);
        course.setAuthor(owner);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userRepository.findByUserEmail("intruder@eduhub.com")).thenReturn(Optional.of(intruder));
        when(authentication.getName()).thenReturn("intruder@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();

        assertThrows(AccessDeniedException.class, () -> lessonService.addLesson(courseId, request, authentication));
    }

    @Test
    void testUpdateLessonSuccess() {
        Long lessonId = 1L;
        UpdateLessonRequest request = new UpdateLessonRequest("Novi naslov", 2, 1L, false);
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);

        Course course = new Course();
        course.setAuthor(owner);

        Lesson lesson = new Lesson();
        lesson.setLessonId(lessonId);
        lesson.setCourse(course);

        LessonType lessonType = new LessonType(1L, "VIDEO");
        LessonDto expectedDto = new LessonDto(1L, "Novi naslov", 2, null, false, null, 1L, "VIDEO");

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findByUserEmail("owner@eduhub.com")).thenReturn(Optional.of(owner));
        when(authentication.getName()).thenReturn("owner@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();
        when(lessonTypeRepository.findById(1L)).thenReturn(Optional.of(lessonType));
        when(lessonMapper.toDto(lesson)).thenReturn(expectedDto);

        LessonDto result = lessonService.updateLesson(lessonId, request, authentication);

        assertEquals(expectedDto, result);
    }

    @Test
    void testDeleteLessonSuccess() {
        Long lessonId = 1L;
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);

        Course course = new Course();
        course.setAuthor(owner);

        Lesson lesson = new Lesson();
        lesson.setLessonId(lessonId);
        lesson.setCourse(course);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findByUserEmail("owner@eduhub.com")).thenReturn(Optional.of(owner));
        when(authentication.getName()).thenReturn("owner@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();

        lessonService.deleteLesson(lessonId, authentication);

        verify(lessonRepository).delete(lesson);
    }

    @Test
    void testDeleteLessonNotFound() {
        Authentication authentication = mock(Authentication.class);
        lenient().when(lessonRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lessonService.deleteLesson(99L, authentication));
    }
}