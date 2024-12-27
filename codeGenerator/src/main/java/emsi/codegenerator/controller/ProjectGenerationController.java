package emsi.codegenerator.controller;

import emsi.codegenerator.entity.ProjectRequest;
import emsi.codegenerator.service.ImageToEntityService;
import emsi.codegenerator.service.ProjectGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController

public class ProjectGenerationController {

    private final ProjectGenerationService projectGenerationService;
    private final ImageToEntityService imageToEntityService;

    public ProjectGenerationController(ProjectGenerationService projectGenerationService, ImageToEntityService imageToEntityService) {
        this.projectGenerationService = projectGenerationService;
        this.imageToEntityService = imageToEntityService;
    }

    @Operation(summary = "Generate a Spring Boot project")
    @PostMapping("/generate-project")
    public ResponseEntity<byte[]> generateProject(@RequestBody ProjectRequest projectRequest) {
        try {
            byte[] projectZip = projectGenerationService.generateSpringProject(projectRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + projectRequest.getArtifactId() + ".zip");
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);

            return new ResponseEntity<>(projectZip, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Error generating project: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/generate-entities-from-image")
    public ResponseEntity<?> generateEntitiesFromImage(@RequestParam("file") MultipartFile file) {
        try {
            List<ProjectRequest.Entity> entities = imageToEntityService.generateEntitiesFromImage(file);
            return ResponseEntity.ok(entities);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process the image");
        }
    }

}
