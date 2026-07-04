package rs.ac.bg.fon.eduhub.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.eduhub.dto.CreateMaterialRequest;
import rs.ac.bg.fon.eduhub.dto.MaterialDto;
import rs.ac.bg.fon.eduhub.service.MaterialService;

/**
 * REST kontroler za upravljanje nastavnim materijalima u okviru lekcije
 * (SO16-SO17). Dodavanje materijala zahteva ulogu INSTRUCTOR ili ADMIN,
 * i to samo za lekcije kursa čiji je korisnik autor.
 *
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
@RestController
public class MaterialController {

    private final MaterialService materialService;

    /**
     * Kreira kontroler sa injektovanim servisom za materijale.
     *
     * @param materialService servis koji implementira poslovnu logiku materijala
     */
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    /**
     * Vraća listu svih nastavnih materijala u okviru zadate lekcije
     * (SO17).
     *
     * @param lessonId identifikator lekcije
     * @return HTTP 200 sa listom materijala lekcije
     */
    @GetMapping("/api/lessons/{lessonId}/materials")
    public ResponseEntity<List<MaterialDto>> getMaterialsByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(materialService.getMaterialsByLesson(lessonId));
    }

    /**
     * Dodaje novi nastavni materijal na zadatu lekciju (SO16).
     *
     * @param lessonId identifikator lekcije na koju se materijal dodaje
     * @param request podaci o novom materijalu
     * @param authentication autentifikacija trenutno prijavljenog korisnika
     * @return HTTP 201 sa podacima novokreiranog materijala
     */
    @PostMapping("/api/lessons/{lessonId}/materials")
    public ResponseEntity<MaterialDto> addMaterial(@PathVariable Long lessonId,
                                                   @Valid @RequestBody CreateMaterialRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materialService.addMaterial(lessonId, request, authentication));
    }
}