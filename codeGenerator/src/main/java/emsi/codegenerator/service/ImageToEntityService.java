package emsi.codegenerator.service;

import emsi.codegenerator.entity.ProjectRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ImageToEntityService {

    public List<ProjectRequest.Entity> generateEntitiesFromImage(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        // Debug logs to confirm file processing
        System.out.println("Received file: " + file.getOriginalFilename());

        // Validate file type
        String contentType = file.getContentType();
        if (!contentType.equals("image/png") && !contentType.equals("image/jpeg")) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }

        // Mocked logic for generating entities
        return List.of(
                new ProjectRequest.Entity("User", "com.example.entity",
                        List.of(new ProjectRequest.Field("id", "Long", false, null, null),
                                new ProjectRequest.Field("name", "String", false, null, null)),
                        null
                ),
                new ProjectRequest.Entity("Order", "com.example.entity",
                        List.of(new ProjectRequest.Field("id", "Long", false, null, null),
                                new ProjectRequest.Field("total", "Double", false, null, null)),
                        null
                )
        );
    }
}