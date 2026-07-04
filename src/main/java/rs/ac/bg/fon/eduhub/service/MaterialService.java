package rs.ac.bg.fon.eduhub.service;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.dto.CreateMaterialRequest;
import rs.ac.bg.fon.eduhub.dto.MaterialDto;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;
import rs.ac.bg.fon.eduhub.entity.impl.Material;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.MaterialType;
import rs.ac.bg.fon.eduhub.mapper.MaterialMapper;
import rs.ac.bg.fon.eduhub.repository.LessonRepository;
import rs.ac.bg.fon.eduhub.repository.MaterialRepository;
import rs.ac.bg.fon.eduhub.repository.MaterialTypeRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialTypeRepository materialTypeRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final MaterialMapper materialMapper;

    public MaterialService(MaterialRepository materialRepository,
                           MaterialTypeRepository materialTypeRepository,
                           LessonRepository lessonRepository,
                           UserRepository userRepository,
                           MaterialMapper materialMapper) {
        this.materialRepository = materialRepository;
        this.materialTypeRepository = materialTypeRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.materialMapper = materialMapper;
    }

    // SO17 - Pregled nastavnog materijala
    public List<MaterialDto> getMaterialsByLesson(Long lessonId) {
        findLessonOrThrow(lessonId);
        return materialRepository.findByLesson_LessonIdOrderByMaterialOrderIndexAsc(lessonId)
                .stream()
                .map(materialMapper::toDto)
                .toList();
    }

    // SO16 - Dodavanje nastavnog materijala
    public MaterialDto addMaterial(Long lessonId, CreateMaterialRequest request, Authentication authentication) {
        Lesson lesson = findLessonOrThrow(lessonId);
        requireCourseOwnerOrAdmin(lesson, authentication);

        MaterialType materialType = materialTypeRepository.findById(request.materialTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Material type not found: " + request.materialTypeId()));

        Material material = new Material();
        material.setMaterialName(request.materialName());
        material.setMaterialOrderIndex(request.materialOrderIndex());
        material.setContent(request.content());
        material.setUrl(request.url());
        material.setMaterialType(materialType);
        material.setLesson(lesson);

        materialRepository.save(material);
        return materialMapper.toDto(material);
    }

    private Lesson findLessonOrThrow(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));
    }

    private void requireCourseOwnerOrAdmin(Lesson lesson, Authentication authentication) {
        User currentUser = userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = lesson.getCourse() != null
                && lesson.getCourse().getAuthor() != null
                && lesson.getCourse().getAuthor().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Only the course author or an admin can add materials to this lesson.");
        }
    }
}