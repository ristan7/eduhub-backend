package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Certificate}.
 *
 * @author Mihajlo Ristanovic
 */
class CertificateTest {

    private Certificate certificate;

    @BeforeEach
    void setUp() {
        certificate = new Certificate();
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
    void testCodeNullIsInvalid() {
        certificate.setCode(null);

        assertNull(certificate.getCode());
    }

    @Test
    void testCodeBlankIsInvalid() {
        certificate.setCode("   ");

        assertTrue(certificate.getCode().isBlank());
    }

    @Test
    void testCertificateUrlTooLongIsInvalid() {
        String tooLongUrl = "a".repeat(501);
        certificate.setCertificateUrl(tooLongUrl);

        assertTrue(certificate.getCertificateUrl().length() > 500);
    }
}