package rs.ac.bg.fon.eduhub.entity.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit testovi za entitet {@link MaterialType}.
 *
 * @author Mihajlo Ristanovic
 */
class MaterialTypeTest {

    private MaterialType materialType;

    @BeforeEach
    void setUp() {
        materialType = new MaterialType();
    }

    @Test
    void testGettersAndSetters() {
        materialType.setMaterialTypeId(1L);
        materialType.setMaterialTypeName("PDF");

        assertEquals(1L, materialType.getMaterialTypeId());
        assertEquals("PDF", materialType.getMaterialTypeName());
    }

    @Test
    void testEqualsSameId() {
        MaterialType first = new MaterialType(1L, "PDF");
        MaterialType second = new MaterialType(1L, "VIDEO");

        assertEquals(first, second);
    }

    @Test
    void testEqualsDifferentId() {
        MaterialType first = new MaterialType(1L, "PDF");
        MaterialType second = new MaterialType(2L, "PDF");

        assertNotEquals(first, second);
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        MaterialType first = new MaterialType(1L, "PDF");
        MaterialType second = new MaterialType(1L, "PDF");

        assertEquals(first.hashCode(), second.hashCode());
    }
}