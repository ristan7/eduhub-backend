package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Certificate}.
 *
 * @author Mihajlo Ristanovic
 */
class CertificateTest {

    private static Validator validator;

    private Certificate certificate;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        certificate = validCertificate();
    }

    /**
     * Pravi Certificate instancu kod koje su sva polja sa Bean Validation
     * anotacijama postavljena na validne vrednosti. Koristi se kao polazna
     * tačka za testove koji menjaju tačno jedno polje.
     */
    private Certificate validCertificate() {
        Certificate c = new Certificate();
        c.setCode("CERT-ABCD1234-2026");
        c.setCertificateUrl("https://eduhub.com/certificates/1.pdf");
        return c;
    }

    private boolean hasViolationFor(Set<ConstraintViolation<Certificate>> violations, String propertyName) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(propertyName));
    }

    @Test
    void testSetCertificateId() {
        certificate.setCertificateId(1L);

        assertEquals(1L, certificate.getCertificateId());
    }

    @Test
    void testSetCode() {
        certificate.setCode("CERT-ABCD1234-2026");

        assertEquals("CERT-ABCD1234-2026", certificate.getCode());
    }

    @Test
    void testSetCertificateUrl() {
        certificate.setCertificateUrl("https://eduhub.com/certificates/1.pdf");

        assertEquals("https://eduhub.com/certificates/1.pdf", certificate.getCertificateUrl());
    }

    @Test
    void testEqualsSameId() {
        Certificate first = new Certificate();
        first.setCertificateId(1L);

        Certificate second = new Certificate();
        second.setCertificateId(1L);

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        Certificate first = new Certificate();
        first.setCertificateId(1L);

        Certificate second = new Certificate();
        second.setCertificateId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void testOnCreateSetsIssuedAt() {
        assertEquals(null, certificate.getIssuedAt());
        certificate.onCreate();
        assertNotNull(certificate.getIssuedAt());
    }



    @Test
    void testCodeValidPassesValidation() {
        certificate.setCode("CERT-ABCD1234-2026");

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertFalse(hasViolationFor(violations, "code"));
    }

    @Test
    void testCodeNullFailsValidation() {
        certificate.setCode(null);

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertTrue(hasViolationFor(violations, "code"));
    }

    @Test
    void testCodeEmptyFailsValidation() {
        certificate.setCode("");

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertTrue(hasViolationFor(violations, "code"));
    }

    @Test
    void testCodeBlankFailsValidation() {
        certificate.setCode("   ");

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertTrue(hasViolationFor(violations, "code"));
    }

    @Test
    void testCodeAtMaxLengthPassesValidation() {
        certificate.setCode("a".repeat(50));

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertFalse(hasViolationFor(violations, "code"));
    }

    @Test
    void testCodeOverMaxLengthFailsValidation() {
        certificate.setCode("a".repeat(51));

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertTrue(hasViolationFor(violations, "code"));
    }

    @Test
    void testCertificateUrlNullPassesValidation() {
        certificate.setCertificateUrl(null);

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertFalse(hasViolationFor(violations, "certificateUrl"));
    }

    @Test
    void testCertificateUrlEmptyPassesValidation() {
        certificate.setCertificateUrl("");

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertFalse(hasViolationFor(violations, "certificateUrl"));
    }

    @Test
    void testCertificateUrlAtMaxLengthPassesValidation() {
        certificate.setCertificateUrl("a".repeat(500));

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertFalse(hasViolationFor(violations, "certificateUrl"));
    }

    @Test
    void testCertificateUrlOverMaxLengthFailsValidation() {
        certificate.setCertificateUrl("a".repeat(501));

        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);

        assertTrue(hasViolationFor(violations, "certificateUrl"));
    }
}