package rs.ac.bg.fon.eduhub.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.dto.CreateEnrollmentRequest;
import rs.ac.bg.fon.eduhub.dto.EnrollmentDto;
import rs.ac.bg.fon.eduhub.dto.UpdateProgressRequest;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.EnrollmentStatus;
import rs.ac.bg.fon.eduhub.mapper.EnrollmentMapper;
import rs.ac.bg.fon.eduhub.repository.CourseRepository;
import rs.ac.bg.fon.eduhub.repository.EnrollmentRepository;
import rs.ac.bg.fon.eduhub.repository.EnrollmentStatusRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

/**
 * Servis koji implementira poslovnu logiku prijave studenata na kurseve,
 * pregleda prijava i praćenja napretka (SO10, SO11, SO18).
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@Service
public class EnrollmentService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentStatusRepository enrollmentStatusRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;

    /**
     * Kreira servis sa svim potrebnim zavisnostima.
     *
     * @param enrollmentRepository repozitorijum prijava
     * @param enrollmentStatusRepository repozitorijum statusa prijava
     * @param courseRepository repozitorijum kurseva
     * @param userRepository repozitorijum korisnika
     * @param enrollmentMapper mapper za konverziju entiteta prijave u DTO
     */
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             EnrollmentStatusRepository enrollmentStatusRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository,
                             EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentStatusRepository = enrollmentStatusRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    /**
     * Prijavljuje trenutno prijavljenog korisnika (studenta) na zadati
     * kurs, u statusu ACTIVE (SO10).
     *
     * @param request podaci o kursu na koji se student prijavljuje
     * @param authentication autentifikacija trenutno prijavljenog korisnika (student)
     * @return DTO novokreirane prijave
     * @throws IllegalArgumentException ako kurs sa datim identifikatorom ne postoji, ili je student već prijavljen na taj kurs
     * @throws IllegalStateException ako podrazumevani status ACTIVE ne postoji u bazi
     */
    public EnrollmentDto enroll(CreateEnrollmentRequest request, Authentication authentication) {
        User student = currentUser(authentication);

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + request.courseId()));

        enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(student.getUserId(), course.getCourseId())
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Student is already enrolled in this course.");
                });

        EnrollmentStatus activeStatus = enrollmentStatusRepository.findByEnrollmentStatusName(ACTIVE_STATUS)
                .orElseThrow(() -> new IllegalStateException("Default enrollment status ACTIVE not found."));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentStatus(activeStatus);
        enrollment.setProgressPercentage(0);

        enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(enrollment);
    }

    /**
     * Vraća listu svih prijava trenutno prijavljenog korisnika (SO11).
     *
     * @param authentication autentifikacija trenutno prijavljenog korisnika (student)
     * @return lista prijava korisnika na kurseve
     */
    public List<EnrollmentDto> getMyEnrollments(Authentication authentication) {
        User student = currentUser(authentication);
        return enrollmentRepository.findByStudent_UserId(student.getUserId())
                .stream()
                .map(enrollmentMapper::toDto)
                .toList();
    }

    /**
     * Ažurira procenat napretka studenta kroz kurs (SO18). Dozvoljeno
     * samo studentu kome prijava pripada. Kada napredak dostigne 100%,
     * status prijave se automatski menja u COMPLETED i beleži se datum
     * završetka.
     *
     * @param enrollmentId identifikator prijave čiji se napredak ažurira
     * @param request novi procenat napretka
     * @param authentication autentifikacija trenutno prijavljenog korisnika (student)
     * @return DTO ažurirane prijave
     * @throws IllegalArgumentException ako prijava sa datim identifikatorom ne postoji
     * @throws AccessDeniedException ako prijava ne pripada trenutno prijavljenom korisniku
     * @throws IllegalStateException ako status COMPLETED ne postoji u bazi (samo kad napredak dostigne 100%)
     */
    public EnrollmentDto updateProgress(Long enrollmentId, UpdateProgressRequest request, Authentication authentication) {
        User student = currentUser(authentication);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + enrollmentId));

        if (enrollment.getStudent() == null || !enrollment.getStudent().getUserId().equals(student.getUserId())) {
            throw new AccessDeniedException("You can only update progress on your own enrollments.");
        }

        enrollment.setProgressPercentage(request.progressPercentage());

        if (request.progressPercentage() == 100) {
            EnrollmentStatus completedStatus = enrollmentStatusRepository.findByEnrollmentStatusName("COMPLETED")
                    .orElseThrow(() -> new IllegalStateException("Enrollment status COMPLETED not found."));
            enrollment.setEnrollmentStatus(completedStatus);
            enrollment.setCompletedAt(LocalDateTime.now());
        }

        enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDto(enrollment);
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
}