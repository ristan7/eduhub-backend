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

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // SO4 - Pregled svih kurseva / SO5 - Pretraga i filtriranje kurseva
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

    // SO6 - Pregled detalja kursa
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // SO7 - Kreiranje novog kursa
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CreateCourseRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request, authentication));
    }

    // SO8 - Izmena podataka o kursu
    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateCourseRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, authentication));
    }

    // SO9 - Brisanje ili deaktivacija kursa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateCourse(@PathVariable Long id, Authentication authentication) {
        courseService.deactivateCourse(id, authentication);
        return ResponseEntity.noContent().build();
    }
}