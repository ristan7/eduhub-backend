package rs.ac.bg.fon.eduhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.dto.CertificateDto;
import rs.ac.bg.fon.eduhub.entity.impl.Certificate;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Enrollment;
import rs.ac.bg.fon.eduhub.entity.impl.User;

/**
 * JUnit testovi za {@link CertificateMapper}.
 *
 * @author Mihajlo Ristanovic
 */
class CertificateMapperTest {

    private CertificateMapper certificateMapper;

    @BeforeEach
    void setUp() {
        certificateMapper = new CertificateMapper();
    }

    @Test
    void testToDtoMapsAllFields() {
        User student = new User();
        student.setUserId(1L);
        student.setFirstName("Petar");
        student.setLastName("Nikolic");

        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseTitle("Java Osnove");

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(1L);
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Certificate certificate = new Certificate();
        certificate.setCertificateId(1L);
        certificate.setCode("CERT-ABCD1234-2026");
        certificate.setCertificateUrl("http://x.com/cert.pdf");
        certificate.setEnrollment(enrollment);

        CertificateDto dto = certificateMapper.toDto(certificate);

        assertEquals(1L, dto.certificateId());
        assertEquals("CERT-ABCD1234-2026", dto.code());
        assertEquals("http://x.com/cert.pdf", dto.certificateUrl());
        assertEquals(1L, dto.enrollmentId());
        assertEquals(1L, dto.courseId());
        assertEquals("Java Osnove", dto.courseTitle());
        assertEquals(1L, dto.studentId());
        assertEquals("Petar Nikolic", dto.studentFullName());
    }

    @Test
    void testToDtoWithNullEnrollmentReturnsNullFields() {
        Certificate certificate = new Certificate();
        certificate.setCertificateId(1L);
        certificate.setCode("CERT-XYZ");

        CertificateDto dto = certificateMapper.toDto(certificate);

        assertNull(dto.enrollmentId());
        assertNull(dto.courseId());
        assertNull(dto.studentId());
    }

    @Test
    void testToDtoWithNullCertificateReturnsNull() {
        assertNull(certificateMapper.toDto(null));
    }
}