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
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CreateLessonRequest;
import rs.ac.bg.fon.eduhub.dto.LessonDto;
import rs.ac.bg.fon.eduhub.dto.UpdateLessonRequest;
import rs.ac.bg.fon.eduhub.service.LessonService;

@RestController
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    // SO15 - Pregled lekcija u okviru kursa
    @GetMapping("/api/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonDto>> getLessonsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }

    // SO12 - Dodavanje lekcije na kurs
    @PostMapping("/api/courses/{courseId}/lessons")
    public ResponseEntity<LessonDto> addLesson(@PathVariable Long courseId,
                                               @Valid @RequestBody CreateLessonRequest request,
                                               Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.addLesson(courseId, request, authentication));
    }

    // SO13 - Izmena lekcije
    @PutMapping("/api/lessons/{id}")
    public ResponseEntity<LessonDto> updateLesson(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateLessonRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request, authentication));
    }

    // SO14 - Brisanje lekcije
    @DeleteMapping("/api/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id, Authentication authentication) {
        lessonService.deleteLesson(id, authentication);
        return ResponseEntity.noContent().build();
    }
}