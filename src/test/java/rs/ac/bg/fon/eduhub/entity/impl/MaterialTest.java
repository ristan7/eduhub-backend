package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link Material}.
 *
 * @author Mihajlo Ristanovic
 */
class MaterialTest {

    private Material material;

    @BeforeEach
    void setUp() {
        material = new Material();
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
    void testMaterialNameNullIsInvalid() {
        material.setMaterialName(null);

        assertNull(material.getMaterialName());
    }

    @Test
    void testMaterialNameBlankIsInvalid() {
        material.setMaterialName("   ");

        assertTrue(material.getMaterialName().isBlank());
    }

    @Test
    void testMaterialNameTooLongIsInvalid() {
        String tooLongName = "a".repeat(151);
        material.setMaterialName(tooLongName);

        assertTrue(material.getMaterialName().length() > 150);
    }

    @Test
    void testMaterialOrderIndexNullIsInvalid() {
        material.setMaterialOrderIndex(null);

        assertNull(material.getMaterialOrderIndex());
    }

    @Test
    void testUrlTooLongIsInvalid() {
        String tooLongUrl = "a".repeat(501);
        material.setUrl(tooLongUrl);

        assertTrue(material.getUrl().length() > 500);
    }
}