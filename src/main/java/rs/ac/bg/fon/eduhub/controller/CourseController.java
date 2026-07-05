package rs.ac.bg.fon.eduhub.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CourseDto;
import rs.ac.bg.fon.eduhub.dto.CreateCourseRequest;
import rs.ac.bg.fon.eduhub.dto.UpdateCourseRequest;
import rs.ac.bg.fon.eduhub.service.CourseService;

/**
 * REST kontroler za pregled, pretragu i upravljanje kursevima
 * (SO4-SO9). Kreiranje, izmena i deaktivacija kursa zahtevaju ulogu
 * INSTRUCTOR ili ADMIN.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    /**
     * Kreira kontroler sa injektovanim servisom za kurseve.
     *
     * @param courseService servis koji implementira poslovnu logiku kurseva
     */
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Vraća listu kurseva. Ako nijedan parametar za pretragu nije zadat,
     * vraća sve kurseve (SO4); u suprotnom primenjuje zadate filtere
     * (SO5).
     *
     * @param keyword ključna reč za pretragu naslova kursa, opciono
     * @param categoryId identifikator kategorije za filtriranje, opciono
     * @param levelId identifikator nivoa za filtriranje, opciono
     * @return HTTP 200 sa listom kurseva
     */
    @GetMapping
    public ResponseEntity<List<CourseDto>> getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long levelId) {

        if (keyword == null && categoryId == null && levelId == null) {
            return ResponseEntity.ok(courseService.getAllCourses());
        }
        return ResponseEntity.ok(courseService.searchCourses(keyword, categoryId, levelId));
    }

    /**
     * Vraća detalje jednog kursa (SO6).
     *
     * @param id identifikator kursa
     * @return HTTP 200 sa detaljima kursa
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    /**
     * Kreira novi kurs, sa trenutno prijavljenim korisnikom kao autorom
     * (SO7).
     *
     * @param request podaci o novom kursu
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 201 sa podacima novokreiranog kursa
     */
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CreateCourseRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request, authentication));
    }

    /**
     * Izmenjuje podatke postojećeg kursa (SO8).
     *
     * @param id identifikator kursa koji se menja
     * @param request novi podaci o kursu
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 200 sa podacima izmenjenog kursa
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateCourseRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, authentication));
    }

    /**
     * Deaktivira (arhivira) kurs (SO9).
     *
     * @param id identifikator kursa koji se deaktivira
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 204 bez tela odgovora
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateCourse(@PathVariable Long id, Authentication authentication) {
        courseService.deactivateCourse(id, authentication);
        return ResponseEntity.noContent().build();
    }
}