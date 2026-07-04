package rs.ac.bg.fon.eduhub.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.bg.fon.eduhub.entity.impl.Course;

/**
 * Repozitorijum za pristup podacima entiteta {@link Course}.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Pretražuje kurseve po ključnoj reči u naslovu, kategoriji i nivou.
     * Svaki od parametara je opcion — ako je {@code null}, odgovarajući
     * filter se ne primenjuje.
     *
     * @param keyword ključna reč za pretragu naslova kursa (delimično poklapanje, bez razlike u velikim/malim slovima), ili {@code null}
     * @param categoryId identifikator kategorije za filtriranje, ili {@code null}
     * @param levelId identifikator nivoa za filtriranje, ili {@code null}
     * @return lista kurseva koji zadovoljavaju zadate kriterijume
     */
    @Query("SELECT c FROM Course c WHERE "
            + "(:keyword IS NULL OR LOWER(c.courseTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
            + "(:categoryId IS NULL OR c.courseCategory.courseCategoryId = :categoryId) AND "
            + "(:levelId IS NULL OR c.courseLevel.courseLevelId = :levelId)")
    List<Course> search(@Param("keyword") String keyword,
                        @Param("categoryId") Long categoryId,
                        @Param("levelId") Long levelId);

    /**
     * Broji kurseve koji imaju dati status.
     *
     * @param courseStatusName naziv statusa (npr. "DRAFT", "PUBLISHED", "ARCHIVED")
     * @return broj kurseva sa datim statusom
     */
    long countByCourseStatus_CourseStatusName(String courseStatusName);
}