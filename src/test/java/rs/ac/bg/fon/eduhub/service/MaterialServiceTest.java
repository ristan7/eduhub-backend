package rs.ac.bg.fon.eduhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import rs.ac.bg.fon.eduhub.dto.CreateMaterialRequest;
import rs.ac.bg.fon.eduhub.dto.MaterialDto;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;
import rs.ac.bg.fon.eduhub.entity.impl.Material;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.MaterialType;
import rs.ac.bg.fon.eduhub.mapper.MaterialMapper;
import rs.ac.bg.fon.eduhub.repository.LessonRepository;
import rs.ac.bg.fon.eduhub.repository.MaterialRepository;
import rs.ac.bg.fon.eduhub.repository.MaterialTypeRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

/**
 * JUnit testovi za servis {@link MaterialService}, uz mokovanje svih
 * zavisnosti (SO16-SO17).
 *
 * @author Mihajlo Ristanovic
 */
@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private MaterialTypeRepository materialTypeRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MaterialMapper materialMapper;

    private MaterialService materialService;

    @BeforeEach
    void setUp() {
        materialService = new MaterialService(materialRepository, materialTypeRepository,
                lessonRepository, userRepository, materialMapper);
    }

    @Test
    void testGetMaterialsByLessonSuccess() {
        Long lessonId = 1L;
        Lesson lesson = new Lesson();
        Material material = new Material();
        MaterialDto dto = new MaterialDto(1L, "Slajdovi", 1, null, "http://x.com", null, lessonId, 1L, "PDF");

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(materialRepository.findByLesson_LessonIdOrderByMaterialOrderIndexAsc(lessonId)).thenReturn(List.of(material));
        when(materialMapper.toDto(material)).thenReturn(dto);

        List<MaterialDto> result = materialService.getMaterialsByLesson(lessonId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetMaterialsByLessonNotFound() {
        when(lessonRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> materialService.getMaterialsByLesson(99L));
    }

    @Test
    void testAddMaterialSuccess() {
        Long lessonId = 1L;
        CreateMaterialRequest request = new CreateMaterialRequest("Slajdovi", 1, null, "http://x.com", 1L);
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);

        Course course = new Course();
        course.setAuthor(owner);

        Lesson lesson = new Lesson();
        lesson.setLessonId(lessonId);
        lesson.setCourse(course);

        MaterialType materialType = new MaterialType(1L, "PDF");
        MaterialDto expectedDto = new MaterialDto(1L, "Slajdovi", 1, null, "http://x.com", null, lessonId, 1L, "PDF");

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findByUserEmail("owner@eduhub.com")).thenReturn(Optional.of(owner));
        when(authentication.getName()).thenReturn("owner@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();
        when(materialTypeRepository.findById(1L)).thenReturn(Optional.of(materialType));
        when(materialMapper.toDto(any(Material.class))).thenReturn(expectedDto);

        MaterialDto result = materialService.addMaterial(lessonId, request, authentication);

        assertEquals(expectedDto, result);
        verify(materialRepository).save(any(Material.class));
    }

    @Test
    void testAddMaterialForbiddenForNonOwner() {
        Long lessonId = 1L;
        CreateMaterialRequest request = new CreateMaterialRequest("Slajdovi", 1, null, "http://x.com", 1L);
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);
        User intruder = new User();
        intruder.setUserId(2L);

        Course course = new Course();
        course.setAuthor(owner);

        Lesson lesson = new Lesson();
        lesson.setLessonId(lessonId);
        lesson.setCourse(course);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(userRepository.findByUserEmail("intruder@eduhub.com")).thenReturn(Optional.of(intruder));
        when(authentication.getName()).thenReturn("intruder@eduhub.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();

        assertThrows(AccessDeniedException.class, () -> materialService.addMaterial(lessonId, request, authentication));
    }

    @Test
    void testAddMaterialTypeNotFound() {
        Long lessonId = 1L;
        CreateMaterialRequest request = new CreateMaterialRequest("Slajdovi", 1, null, "http://x.com", 99L);
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
        when(materialTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> materialService.addMaterial(lessonId, request, authentication));
    }
}