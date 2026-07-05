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
import rs.ac.bg.fon.eduhub.dto.CertificateDto;
import rs.ac.bg.fon.eduhub.entity.impl.Certificate;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.mapper.CertificateMapper;
import rs.ac.bg.fon.eduhub.repository.CertificateRepository;
import rs.ac.bg.fon.eduhub.repository.EnrollmentRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

/**
 * JUnit testovi za servis {@link CertificateService}, uz mokovanje svih
 * zavisnosti (SO28, SO29).
 *
 * @author Mihajlo Ristanovic
 */
@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CertificateMapper certificateMapper;

    private CertificateService certificateService;

    @BeforeEach
    void setUp() {
        certificateService = new CertificateService(certificateRepository, enrollmentRepository,
                userRepository, certificateMapper);
    }

    @Test
    void testIssueCertificateSuccess() {
        Long enrollmentId = 1L;
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);

        Course course = new Course();
        course.setAuthor(owner);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setCourse(course);

        CertificateDto expectedDto = new CertificateDto(1L, "CERT-ABCD1234-2026", null, null, enrollmentId, null, null, null, null);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(authentication.getName()).thenReturn("owner@eduhub.com");
        when(userRepository.findByUserEmail("owner@eduhub.com")).thenReturn(Optional.of(owner));
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();
        when(certificateRepository.findByEnrollment_EnrollmentId(enrollmentId)).thenReturn(Optional.empty());
        when(certificateMapper.toDto(any(Certificate.class))).thenReturn(expectedDto);

        CertificateDto result = certificateService.issueCertificate(enrollmentId, authentication);

        assertEquals(expectedDto, result);
        verify(certificateRepository).save(any(Certificate.class));
    }

    @Test
    void testIssueCertificateForbiddenForNonOwner() {
        Long enrollmentId = 1L;
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);
        User intruder = new User();
        intruder.setUserId(2L);

        Course course = new Course();
        course.setAuthor(owner);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setCourse(course);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(authentication.getName()).thenReturn("intruder@eduhub.com");
        when(userRepository.findByUserEmail("intruder@eduhub.com")).thenReturn(Optional.of(intruder));
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();

        assertThrows(AccessDeniedException.class, () -> certificateService.issueCertificate(enrollmentId, authentication));
    }

    @Test
    void testIssueCertificateAlreadyExists() {
        Long enrollmentId = 1L;
        Authentication authentication = mock(Authentication.class);

        User owner = new User();
        owner.setUserId(1L);

        Course course = new Course();
        course.setAuthor(owner);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setCourse(course);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(authentication.getName()).thenReturn("owner@eduhub.com");
        when(userRepository.findByUserEmail("owner@eduhub.com")).thenReturn(Optional.of(owner));
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))).when(authentication).getAuthorities();
        when(certificateRepository.findByEnrollment_EnrollmentId(enrollmentId)).thenReturn(Optional.of(new Certificate()));

        assertThrows(IllegalArgumentException.class, () -> certificateService.issueCertificate(enrollmentId, authentication));
    }

    @Test
    void testGetMyCertificates() {
        Authentication authentication = mock(Authentication.class);
        User student = new User();
        student.setUserId(1L);

        Certificate certificate = new Certificate();
        CertificateDto dto = new CertificateDto(1L, "CERT-ABCD1234-2026", null, null, 1L, 1L, "Java", 1L, "Petar Nikolic");

        when(authentication.getName()).thenReturn("student@eduhub.com");
        when(userRepository.findByUserEmail("student@eduhub.com")).thenReturn(Optional.of(student));
        when(certificateRepository.findByEnrollment_Student_UserId(1L)).thenReturn(List.of(certificate));
        when(certificateMapper.toDto(certificate)).thenReturn(dto);

        List<CertificateDto> result = certificateService.getMyCertificates(authentication);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }
}