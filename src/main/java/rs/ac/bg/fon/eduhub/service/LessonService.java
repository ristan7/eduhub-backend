package rs.ac.bg.fon.eduhub.service;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
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
 * Servis koji implementira poslovnu logiku upravljanja lekcijama u okviru
 * kursa (SO12-SO15). Dodavanje, izmena i brisanje lekcija dozvoljeno je
 * samo autoru kursa kojem lekcija pripada ili korisniku sa ulogom ADMIN.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonTypeRepository lessonTypeRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonMapper lessonMapper;

    /**
     * Kreira servis sa svim potrebnim zavisnostima.
     *
     * @param lessonRepository repozitorijum lekcija
     * @param lessonTypeRepository repozitorijum tipova lekcija
     * @param courseRepository repozitorijum kurseva
     * @param userRepository repozitorijum korisnika
     * @param lessonMapper mapper za konverziju entiteta lekcije u DTO
     */
    public LessonService(LessonRepository lessonRepository,
                         LessonTypeRepository lessonTypeRepository,
                         CourseRepository courseRepository,
                         UserRepository userRepository,
                         LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonTypeRepository = lessonTypeRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.lessonMapper = lessonMapper;
    }

    /**
     * Vraća listu svih lekcija u okviru zadatog kursa, sortiranu po
     * redosledu prikaza (SO15).
     *
     * @param courseId identifikator kursa
     * @return lista lekcija kursa
     * @throws IllegalArgumentException ako kurs sa datim identifikatorom ne postoji
     */
    public List<LessonDto> getLessonsByCourse(Long courseId) {
        findCourseOrThrow(courseId);
        return lessonRepository.findByCourse_CourseIdOrderByLessonOrderIndexAsc(courseId)
                .stream()
                .map(lessonMapper::toDto)
                .toList();
    }

    /**
     * Dodaje novu lekciju na zadati kurs (SO12). Dozvoljeno samo autoru
     * kursa ili korisniku sa ulogom ADMIN.
     *
     * @param courseId identifikator kursa na koji se lekcija dodaje
     * @param request podaci o novoj lekciji
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return DTO novokreirane lekcije
     * @throws IllegalArgumentException ako kurs ili tip lekcije sa datim identifikatorom ne postoje
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
    public LessonDto addLesson(Long courseId, CreateLessonRequest request, Authentication authentication) {
        Course course = findCourseOrThrow(courseId);
        requireCourseOwnerOrAdmin(course, authentication);

        LessonType lessonType = lessonTypeRepository.findById(request.lessonTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson type not found: " + request.lessonTypeId()));

        Lesson lesson = new Lesson();
        lesson.setLessonTitle(request.lessonTitle());
        lesson.setLessonOrderIndex(request.lessonOrderIndex());
        lesson.setLessonType(lessonType);
        lesson.setIsAvailable(true);
        lesson.setCourse(course);

        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    /**
     * Izmenjuje podatke postojeće lekcije (SO13). Dozvoljeno samo autoru
     * kursa kojem lekcija pripada ili korisniku sa ulogom ADMIN.
     *
     * @param lessonId identifikator lekcije koja se menja
     * @param request novi podaci o lekciji
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return DTO izmenjene lekcije
     * @throws IllegalArgumentException ako lekcija ili tip lekcije sa datim identifikatorom ne postoje
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
    public LessonDto updateLesson(Long lessonId, UpdateLessonRequest request, Authentication authentication) {
        Lesson lesson = findLessonOrThrow(lessonId);
        requireCourseOwnerOrAdmin(lesson.getCourse(), authentication);

        LessonType lessonType = lessonTypeRepository.findById(request.lessonTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson type not found: " + request.lessonTypeId()));

        lesson.setLessonTitle(request.lessonTitle());
        lesson.setLessonOrderIndex(request.lessonOrderIndex());
        lesson.setLessonType(lessonType);
        lesson.setIsAvailable(request.isAvailable());

        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    /**
     * Trajno briše lekciju (SO14). Dozvoljeno samo autoru kursa kojem
     * lekcija pripada ili korisniku sa ulogom ADMIN.
     *
     * @param lessonId identifikator lekcije koja se briše
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @throws IllegalArgumentException ako lekcija sa datim identifikatorom ne postoji
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
    public void deleteLesson(Long lessonId, Authentication authentication) {
        Lesson lesson = findLessonOrThrow(lessonId);
        requireCourseOwnerOrAdmin(lesson.getCourse(), authentication);
        lessonRepository.delete(lesson);
    }

    /**
     * Pronalazi kurs po identifikatoru ili baca izuzetak ako ne postoji.
     *
     * @param courseId identifikator kursa
     * @return pronađeni entitet kursa
     * @throws IllegalArgumentException ako kurs sa datim identifikatorom ne postoji
     */
    private Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
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
     * Proverava da li trenutno prijavljeni korisnik ima pravo da menja
     * lekcije zadatog kursa (mora biti autor kursa ili imati ulogu ADMIN).
     *
     * @param course kurs kojem lekcija pripada
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @throws IllegalStateException ako autentifikovani korisnik neočekivano ne postoji u bazi
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
    private void requireCourseOwnerOrAdmin(Course course, Authentication authentication) {
        User currentUser = userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = course.getAuthor() != null
                && course.getAuthor().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Only the course author or an admin can modify lessons of this course.");
        }
    }
}