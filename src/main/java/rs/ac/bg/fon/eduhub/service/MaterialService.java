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

/**
 * Servis koji implementira poslovnu logiku upravljanja nastavnim
 * materijalima u okviru lekcije (SO16-SO17). Dodavanje materijala
 * dozvoljeno je samo autoru kursa kojem lekcija pripada ili korisniku
 * sa ulogom ADMIN.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialTypeRepository materialTypeRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final MaterialMapper materialMapper;

    /**
     * Kreira servis sa svim potrebnim zavisnostima.
     *
     * @param materialRepository repozitorijum materijala
     * @param materialTypeRepository repozitorijum tipova materijala
     * @param lessonRepository repozitorijum lekcija
     * @param userRepository repozitorijum korisnika
     * @param materialMapper mapper za konverziju entiteta materijala u DTO
     */
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

    /**
     * Vraća listu svih nastavnih materijala u okviru zadate lekcije,
     * sortiranu po redosledu prikaza (SO17).
     *
     * @param lessonId identifikator lekcije
     * @return lista materijala lekcije
     * @throws IllegalArgumentException ako lekcija sa datim identifikatorom ne postoji
     */
    public List<MaterialDto> getMaterialsByLesson(Long lessonId) {
        findLessonOrThrow(lessonId);
        return materialRepository.findByLesson_LessonIdOrderByMaterialOrderIndexAsc(lessonId)
                .stream()
                .map(materialMapper::toDto)
                .toList();
    }

    /**
     * Dodaje novi nastavni materijal na zadatu lekciju (SO16). Dozvoljeno
     * samo autoru kursa kojem lekcija pripada ili korisniku sa ulogom ADMIN.
     *
     * @param lessonId identifikator lekcije na koju se materijal dodaje
     * @param request podaci o novom materijalu
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return DTO novokreiranog materijala
     * @throws IllegalArgumentException ako lekcija ili tip materijala sa datim identifikatorom ne postoje
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
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

    /**
     * Pronalazi lekciju po identifikatoru ili baca izuzetak ako ne postoji.
     *
     * @param lessonId identifikator lekcije
     * @return pronađeni entitet lekcije
     * @throws IllegalArgumentException ako lekcija sa datim identifikatorom ne postoji
     */
    private Lesson findLessonOrThrow(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));
    }

    /**
     * Proverava da li trenutno prijavljeni korisnik ima pravo da dodaje
     * materijale u datu lekciju (mora biti autor kursa kojem lekcija
     * pripada ili imati ulogu ADMIN).
     *
     * @param lesson lekcija na koju se materijal dodaje
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @throws IllegalStateException ako autentifikovani korisnik neočekivano ne postoji u bazi
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
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