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

/**
 * Servis koji implementira poslovnu logiku administratorskih operacija:
 * upravljanje korisnicima, dodelu uloga, odobravanje/blokiranje kurseva
 * i pregled statistike platforme (SO21-SO24). Pristup svim operacijama
 * ovog servisa ograničen je na korisnike sa ulogom ADMIN
 * ({@link rs.ac.bg.fon.eduhub.config.SecurityConfig}).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
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

    /**
     * Kreira servis sa svim potrebnim zavisnostima.
     *
     * @param userRepository repozitorijum korisnika
     * @param roleRepository repozitorijum uloga
     * @param courseRepository repozitorijum kurseva
     * @param courseStatusRepository repozitorijum statusa kurseva
     * @param enrollmentRepository repozitorijum prijava
     * @param certificateRepository repozitorijum sertifikata
     * @param userMapper mapper za konverziju entiteta korisnika u DTO
     * @param courseMapper mapper za konverziju entiteta kursa u DTO
     */
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

    /**
     * Vraća listu svih registrovanih korisnika (SO21).
     *
     * @return lista svih korisnika u sistemu
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    /**
     * Vraća detalje jednog korisnika po identifikatoru (SO21).
     *
     * @param userId identifikator korisnika
     * @return detalji traženog korisnika
     * @throws IllegalArgumentException ako korisnik sa datim identifikatorom ne postoji
     */
    public UserDto getUserById(Long userId) {
        return userMapper.toDto(findUserOrThrow(userId));
    }

    /**
     * Menja status aktivnosti naloga korisnika, čime se korisnik može
     * blokirati ili ponovo aktivirati (SO21).
     *
     * @param userId identifikator korisnika
     * @param isActive nova vrednost statusa aktivnosti naloga
     * @return DTO ažuriranog korisnika
     * @throws IllegalArgumentException ako korisnik sa datim identifikatorom ne postoji
     */
    public UserDto setUserActiveStatus(Long userId, boolean isActive) {
        User user = findUserOrThrow(userId);
        user.setIsActive(isActive);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * Dodeljuje novu ulogu korisniku (SO22).
     *
     * @param userId identifikator korisnika
     * @param roleId identifikator uloge koja se dodeljuje
     * @return DTO ažuriranog korisnika
     * @throws IllegalArgumentException ako korisnik ili uloga sa datim identifikatorom ne postoje
     */
    public UserDto updateUserRole(Long userId, Long roleId) {
        User user = findUserOrThrow(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
        user.setRole(role);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * Odobrava kurs postavljanjem statusa na PUBLISHED (SO23).
     *
     * @param courseId identifikator kursa koji se odobrava
     * @return DTO ažuriranog kursa
     * @throws IllegalArgumentException ako kurs sa datim identifikatorom ne postoji
     * @throws IllegalStateException ako status PUBLISHED ne postoji u bazi
     */
    public CourseDto approveCourse(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        CourseStatus published = courseStatusRepository.findByCourseStatusName(PUBLISHED_STATUS)
                .orElseThrow(() -> new IllegalStateException("Course status PUBLISHED not found."));
        course.setCourseStatus(published);
        course.setIsPublished(true);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    /**
     * Blokira kurs postavljanjem statusa na ARCHIVED (SO23).
     *
     * @param courseId identifikator kursa koji se blokira
     * @return DTO ažuriranog kursa
     * @throws IllegalArgumentException ako kurs sa datim identifikatorom ne postoji
     * @throws IllegalStateException ako status ARCHIVED ne postoji u bazi
     */
    public CourseDto blockCourse(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        CourseStatus archived = courseStatusRepository.findByCourseStatusName(ARCHIVED_STATUS)
                .orElseThrow(() -> new IllegalStateException("Course status ARCHIVED not found."));
        course.setCourseStatus(archived);
        course.setIsPublished(false);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    /**
     * Prikuplja zbirnu statistiku o korisnicima, kursevima, prijavama i
     * izdatim sertifikatima na platformi (SO24).
     *
     * @return objekat sa svim brojčanim pokazateljima statistike platforme
     */
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

    /**
     * Pronalazi korisnika po identifikatoru ili baca izuzetak ako ne postoji.
     *
     * @param userId identifikator korisnika
     * @return pronađeni entitet korisnika
     * @throws IllegalArgumentException ako korisnik sa datim identifikatorom ne postoji
     */
    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
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
}