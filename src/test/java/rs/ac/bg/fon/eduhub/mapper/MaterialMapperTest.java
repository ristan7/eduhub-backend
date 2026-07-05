package rs.ac.bg.fon.eduhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.dto.MaterialDto;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;
import rs.ac.bg.fon.eduhub.entity.impl.Material;
import rs.ac.bg.fon.eduhub.entity.lookup.MaterialType;

/**
 * JUnit testovi za {@link MaterialMapper}.
 *
 * @author Mihajlo Ristanovic
 */
class MaterialMapperTest {

    private MaterialMapper materialMapper;

    @BeforeEach
    void setUp() {
        materialMapper = new MaterialMapper();
    }

    @Test
    void testToDtoMapsAllFields() {
        Lesson lesson = new Lesson();
        lesson.setLessonId(1L);

        Material material = new Material();
        material.setMaterialId(1L);
        material.setMaterialName("Slajdovi");
        material.setMaterialOrderIndex(1);
        material.setContent("Sadrzaj");
        material.setUrl("http://x.com/a.pdf");
        material.setLesson(lesson);
        material.setMaterialType(new MaterialType(1L, "PDF"));

        MaterialDto dto = materialMapper.toDto(material);

        assertEquals(1L, dto.materialId());
        assertEquals("Slajdovi", dto.materialName());
        assertEquals(1, dto.materialOrderIndex());
        assertEquals("Sadrzaj", dto.content());
        assertEquals("http://x.com/a.pdf", dto.url());
        assertEquals(1L, dto.lessonId());
        assertEquals("PDF", dto.materialTypeName());
    }

    @Test
    void testToDtoWithNullAssociationsReturnsNullFields() {
        Material material = new Material();
        material.setMaterialId(1L);

        MaterialDto dto = materialMapper.toDto(material);

        assertNull(dto.lessonId());
        assertNull(dto.materialTypeId());
    }

    @Test
    void testToDtoWithNullMaterialReturnsNull() {
        assertNull(materialMapper.toDto(null));
    }
}