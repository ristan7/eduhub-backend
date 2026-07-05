package rs.ac.bg.fon.eduhub.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void testGettersAndSetters() {
        material.setMaterialId(1L);
        material.setMaterialName("Slajdovi predavanja");
        material.setMaterialOrderIndex(1);
        material.setContent("Sadrzaj materijala");
        material.setUrl("https://eduhub.com/materials/1.pdf");

        assertEquals(1L, material.getMaterialId());
        assertEquals("Slajdovi predavanja", material.getMaterialName());
        assertEquals(1, material.getMaterialOrderIndex());
        assertEquals("Sadrzaj materijala", material.getContent());
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
}