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
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CreateLessonRequest;
import rs.ac.bg.fon.eduhub.dto.LessonDto;
import rs.ac.bg.fon.eduhub.dto.UpdateLessonRequest;
import rs.ac.bg.fon.eduhub.service.LessonService;

/**
 * REST kontroler za upravljanje lekcijama u okviru kursa (SO12-SO15).
 * Dodavanje, izmena i brisanje lekcija zahtevaju ulogu INSTRUCTOR ili
 * ADMIN, i to samo za lekcije kursa čiji je korisnik autor.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestController
public class LessonController {

    private final LessonService lessonService;

    /**
     * Kreira kontroler sa injektovanim servisom za lekcije.
     *
     * @param lessonService servis koji implementira poslovnu logiku lekcija
     */
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    /**
     * Vraća listu svih lekcija u okviru zadatog kursa (SO15).
     *
     * @param courseId identifikator kursa
     * @return HTTP 200 sa listom lekcija kursa
     */
    @GetMapping("/api/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonDto>> getLessonsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }

    /**
     * Dodaje novu lekciju na zadati kurs (SO12).
     *
     * @param courseId identifikator kursa na koji se lekcija dodaje
     * @param request podaci o novoj lekciji
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 201 sa podacima novokreirane lekcije
     */
    @PostMapping("/api/courses/{courseId}/lessons")
    public ResponseEntity<LessonDto> addLesson(@PathVariable Long courseId,
                                               @Valid @RequestBody CreateLessonRequest request,
                                               Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.addLesson(courseId, request, authentication));
    }

    /**
     * Izmenjuje podatke postojeće lekcije (SO13).
     *
     * @param id identifikator lekcije koja se menja
     * @param request novi podaci o lekciji
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 200 sa podacima izmenjene lekcije
     */
    @PutMapping("/api/lessons/{id}")
    public ResponseEntity<LessonDto> updateLesson(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateLessonRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request, authentication));
    }

    /**
     * Trajno briše lekciju (SO14).
     *
     * @param id identifikator lekcije koja se briše
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 204 bez tela odgovora
     */
    @DeleteMapping("/api/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id, Authentication authentication) {
        lessonService.deleteLesson(id, authentication);
        return ResponseEntity.noContent().build();
    }
}