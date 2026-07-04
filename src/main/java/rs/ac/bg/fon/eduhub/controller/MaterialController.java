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

@RestController
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    // SO17 - Pregled nastavnog materijala
    @GetMapping("/api/lessons/{lessonId}/materials")
    public ResponseEntity<List<MaterialDto>> getMaterialsByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(materialService.getMaterialsByLesson(lessonId));
    }

    // SO16 - Dodavanje nastavnog materijala
    @PostMapping("/api/lessons/{lessonId}/materials")
    public ResponseEntity<MaterialDto> addMaterial(@PathVariable Long lessonId,
                                                   @Valid @RequestBody CreateMaterialRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materialService.addMaterial(lessonId, request, authentication));
    }
}