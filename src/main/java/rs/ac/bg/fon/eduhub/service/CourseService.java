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

/**
 * Servis koji implementira poslovnu logiku pregleda, pretrage i upravljanja
 * kursevima (SO4-SO9). Izmena i deaktivacija kursa dozvoljena je samo
 * autoru kursa ili korisniku sa ulogom ADMIN.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
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

    /**
     * Kreira servis sa svim potrebnim zavisnostima.
     *
     * @param courseRepository repozitorijum kurseva
     * @param courseCategoryRepository repozitorijum kategorija kurseva
     * @param courseLevelRepository repozitorijum nivoa kurseva
     * @param courseStatusRepository repozitorijum statusa kurseva
     * @param userRepository repozitorijum korisnika
     * @param courseMapper mapper za konverziju entiteta kursa u DTO
     */
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

    /**
     * Vraća listu svih kurseva u sistemu (SO4).
     *
     * @return lista svih kurseva
     */
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(courseMapper::toDto).toList();
    }

    /**
     * Pretražuje kurseve po ključnoj reči, kategoriji i nivou (SO5).
     *
     * @param keyword ključna reč za pretragu naslova, ili {@code null}
     * @param categoryId identifikator kategorije za filtriranje, ili {@code null}
     * @param levelId identifikator nivoa za filtriranje, ili {@code null}
     * @return lista kurseva koji zadovoljavaju zadate kriterijume
     */
    public List<CourseDto> searchCourses(String keyword, Long categoryId, Long levelId) {
        return courseRepository.search(keyword, categoryId, levelId).stream()
                .map(courseMapper::toDto)
                .toList();
    }

    /**
     * Vraća detalje jednog kursa po identifikatoru (SO6).
     *
     * @param courseId identifikator kursa
     * @return detalji traženog kursa
     * @throws IllegalArgumentException ako kurs sa datim identifikatorom ne postoji
     */
    public CourseDto getCourseById(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        return courseMapper.toDto(course);
    }

    /**
     * Kreira novi kurs u statusu DRAFT, sa trenutno prijavljenim
     * korisnikom kao autorom (SO7).
     *
     * @param request podaci o novom kursu
     * @param authentication autentifikacija trenutno prijavljenog korisnika (postaje autor kursa)
     * @return DTO novokreiranog kursa
     * @throws IllegalArgumentException ako kategorija ili nivo sa datim identifikatorom ne postoje
     * @throws IllegalStateException ako podrazumevani status DRAFT ne postoji u bazi
     */
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

    /**
     * Izmenjuje podatke postojećeg kursa (SO8). Dozvoljeno samo autoru
     * kursa ili korisniku sa ulogom ADMIN.
     *
     * @param courseId identifikator kursa koji se menja
     * @param request novi podaci o kursu
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return DTO izmenjenog kursa
     * @throws IllegalArgumentException ako kurs, kategorija ili nivo sa datim identifikatorom ne postoje
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
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

    /**
     * Deaktivira (arhivira) kurs, umesto trajnog brisanja (SO9).
     * Dozvoljeno samo autoru kursa ili korisniku sa ulogom ADMIN.
     *
     * @param courseId identifikator kursa koji se deaktivira
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @throws IllegalArgumentException ako kurs sa datim identifikatorom ne postoji
     * @throws IllegalStateException ako status ARCHIVED ne postoji u bazi
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
    public void deactivateCourse(Long courseId, Authentication authentication) {
        Course course = findCourseOrThrow(courseId);
        requireOwnerOrAdmin(course, authentication);

        CourseStatus archived = courseStatusRepository.findByCourseStatusName(ARCHIVED_STATUS)
                .orElseThrow(() -> new IllegalStateException("Course status ARCHIVED not found."));

        course.setCourseStatus(archived);
        course.setIsPublished(false);
        courseRepository.save(course);
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
     * Pronalazi entitet korisnika na osnovu email adrese iz autentifikacije.
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return pronađeni entitet korisnika
     * @throws IllegalStateException ako autentifikovani korisnik neočekivano ne postoji u bazi
     */
    private User currentUser(Authentication authentication) {
        return userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
    }

    /**
     * Proverava da li trenutno prijavljeni korisnik ima pravo da menja
     * dati kurs (mora biti autor kursa ili imati ulogu ADMIN).
     *
     * @param course kurs koji se menja
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @throws AccessDeniedException ako korisnik nije autor kursa niti ima ulogu ADMIN
     */
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