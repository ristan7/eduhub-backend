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
 * JUnit testovi za entitet {@link Material}.
 *
 * @author Mihajlo Ristanovic
 */
class MaterialTest {

    private static Validator validator;

    private Material material;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        material = validMaterial();
    }

    private Material validMaterial() {
        Material m = new Material();
        m.setMaterialName("Slajdovi predavanja");
        m.setMaterialOrderIndex(1);
        m.setUrl("https://eduhub.com/materials/1.pdf");
        return m;
    }

    private boolean hasViolationFor(Set<ConstraintViolation<Material>> violations, String propertyName) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(propertyName));
    }

    @Test
    void testSetMaterialId() {
        material.setMaterialId(1L);

        assertEquals(1L, material.getMaterialId());
    }

    @Test
    void testSetMaterialName() {
        material.setMaterialName("Slajdovi predavanja");

        assertEquals("Slajdovi predavanja", material.getMaterialName());
    }

    @Test
    void testSetMaterialOrderIndex() {
        material.setMaterialOrderIndex(1);

        assertEquals(1, material.getMaterialOrderIndex());
    }

    @Test
    void testSetContent() {
        material.setContent("Sadrzaj materijala");

        assertEquals("Sadrzaj materijala", material.getContent());
    }

    @Test
    void testSetUrl() {
        material.setUrl("https://eduhub.com/materials/1.pdf");

        assertEquals("https://eduhub.com/materials/1.pdf", material.getUrl());
    }

    @Test
    void testEqualsSameId() {
        Material first = new Material();
        first.setMaterialId(1L);

        Material second = new Material();
        second.setMaterialId(1L);

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        Material first = new Material();
        first.setMaterialId(1L);

        Material second = new Material();
        second.setMaterialId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void testOnCreateSetsUploadedAt() {
        assertEquals(null, material.getUploadedAt());
        material.onCreate();
        assertNotNull(material.getUploadedAt());
    }

    @Test
    void testMaterialNameValidPassesValidation() {
        material.setMaterialName("Slajdovi predavanja");

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertFalse(hasViolationFor(violations, "materialName"));
    }

    @Test
    void testMaterialNameNullFailsValidation() {
        material.setMaterialName(null);

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertTrue(hasViolationFor(violations, "materialName"));
    }

    @Test
    void testMaterialNameEmptyFailsValidation() {
        material.setMaterialName("");

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertTrue(hasViolationFor(violations, "materialName"));
    }

    @Test
    void testMaterialNameBlankFailsValidation() {
        material.setMaterialName("   ");

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertTrue(hasViolationFor(violations, "materialName"));
    }

    @Test
    void testMaterialNameAtMaxLengthPassesValidation() {
        material.setMaterialName("a".repeat(150));

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertFalse(hasViolationFor(violations, "materialName"));
    }

    @Test
    void testMaterialNameOverMaxLengthFailsValidation() {
        material.setMaterialName("a".repeat(151));

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertTrue(hasViolationFor(violations, "materialName"));
    }

    @Test
    void testMaterialOrderIndexValidPassesValidation() {
        material.setMaterialOrderIndex(1);

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertFalse(hasViolationFor(violations, "materialOrderIndex"));
    }

    @Test
    void testMaterialOrderIndexNullFailsValidation() {
        material.setMaterialOrderIndex(null);

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertTrue(hasViolationFor(violations, "materialOrderIndex"));
    }

    @Test
    void testUrlNullPassesValidation() {
        material.setUrl(null);

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertFalse(hasViolationFor(violations, "url"));
    }

    @Test
    void testUrlEmptyPassesValidation() {
        material.setUrl("");

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertFalse(hasViolationFor(violations, "url"));
    }

    @Test
    void testUrlAtMaxLengthPassesValidation() {
        material.setUrl("a".repeat(500));

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertFalse(hasViolationFor(violations, "url"));
    }

    @Test
    void testUrlOverMaxLengthFailsValidation() {
        material.setUrl("a".repeat(501));

        Set<ConstraintViolation<Material>> violations = validator.validate(material);

        assertTrue(hasViolationFor(violations, "url"));
    }
}