package emsi.codegenerator.controller;

import emsi.codegenerator.entity.ProjectRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

@RestController
public class ProjectGenerationController {

    private final RestTemplate restTemplate;

    private static final String SPRING_INITIALIZR_URL = "https://start.spring.io/starter.zip";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";  // OpenAI API endpoint
    private static final String OPENAI_API_KEY = "your-api-key";  // Replace with your OpenAI API key

    public ProjectGenerationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Operation
    @PostMapping("/generate-project")
    public ResponseEntity<byte[]> generateProject(
            @RequestBody ProjectRequest projectRequest) {

        try {
            // Step 1: Generate the Spring Boot project using Spring Initializr
            String url = String.format("%s?type=%s&language=%s&bootVersion=%s&baseDir=%s&groupId=%s&artifactId=%s&name=%s&packageName=%s&javaVersion=%s",
                    SPRING_INITIALIZR_URL,
                    projectRequest.getType(),
                    projectRequest.getLanguage(),
                    projectRequest.getBootVersion(),
                    projectRequest.getBaseDir(),
                    projectRequest.getGroupId(),
                    projectRequest.getArtifactId(),
                    projectRequest.getName(),
                    projectRequest.getPackageName(),
                    projectRequest.getJavaVersion()
            );

            RestTemplate restTemplate = new RestTemplate();
            byte[] projectZip = restTemplate.getForObject(url, byte[].class);

            // Step 2: Create a temporary directory to extract the Spring Boot project
            Path tempDir = Files.createTempDirectory("project-temp");
            Path zipFilePath = tempDir.resolve(projectRequest.getArtifactId() + ".zip");
            Files.write(zipFilePath, projectZip);

            // Step 3: Unzip the Spring Boot project
            unzipProject(tempDir, zipFilePath.toString());

            // Step 4: Generate entity code for each entity and place it in the project
            for (ProjectRequest.Entity entity : projectRequest.getEntities()) {
                String entityCode = generateEntityCode(entity, projectRequest.getPackageName());
                String entityFilePath = tempDir + "/" + projectRequest.getArtifactId() + "/src/main/java/" +
                        projectRequest.getPackageName().replace(".", "/") + "/entity/" + entity.getName() + ".java";

                Path entityFile = Paths.get(entityFilePath);
                Files.createDirectories(entityFile.getParent());
                Files.write(entityFile, entityCode.getBytes());
            }

            // Step 5: Zip the updated project folder
            byte[] updatedProjectZip = zipProject(tempDir.resolve(projectRequest.getArtifactId()));

            // Step 6: Clean up
            Files.deleteIfExists(zipFilePath);

            // Return the updated zip file
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + projectRequest.getArtifactId() + ".zip");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(updatedProjectZip, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Error generating project: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Generate entity code dynamically
    /*private String generateEntityCode(ProjectRequest.Entity entity, String basePackage) {
        StringBuilder entityCode = new StringBuilder();
        entityCode.append("package ").append(basePackage).append(".entity;\n\n");
        entityCode.append("import javax.persistence.*;\n");
        entityCode.append("import lombok.*;\n\n");
        entityCode.append("@Entity\n");
        entityCode.append("@Data\n");
        entityCode.append("@NoArgsConstructor\n");
        entityCode.append("@AllArgsConstructor\n");
        entityCode.append("public class ").append(entity.getName()).append(" {\n");
        entityCode.append("    @Id\n");
        entityCode.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
        entityCode.append("    private Long id;\n\n");
        for (ProjectRequest.Field field : entity.getFields()) {
            entityCode.append("    private ").append(field.getType()).append(" ").append(field.getName()).append(";\n");
        }
        entityCode.append("}\n");
        return entityCode.toString();
    }*/

    private String generateEntityCode(ProjectRequest.Entity entity, String basePackage) {
        // Build the user input for the prompt
        String prompt = buildEntityPrompt(entity, basePackage);

        // Call the prompt API
        String chatResponse = callChatAPI(prompt);

        // Use the response to generate the entity code
        return chatResponse;
    }

    private String buildEntityPrompt(ProjectRequest.Entity entity, String basePackage) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a JPA entity class with the following details:\n");
        prompt.append("Package: ").append(basePackage).append(".entity\n");
        prompt.append("Entity Name: ").append(entity.getName()).append("\n");
        prompt.append("Fields:\n");
        for (ProjectRequest.Field field : entity.getFields()) {
            prompt.append("- ").append(field.getType()).append(" ").append(field.getName()).append("\n");
        }
        return prompt.toString();
    }

    private String callChatAPI(String userInput) {
        try {
            String url = "http://localhost:8081/api/chat"; // Replace with your actual base URL if necessary

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            HttpEntity<String> request = new HttpEntity<>(userInput, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class
            );

            return response.getBody(); // The generated entity code
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }


    // Unzip the project zip file directly into the target directory
    private void unzipProject(Path destDir, String zipFilePath) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newFile = destDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(newFile);
                } else {
                    Files.createDirectories(newFile.getParent());
                    try (FileOutputStream fos = new FileOutputStream(newFile.toFile())) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    // Zip the project folder and return the resulting byte array
    private byte[] zipProject(Path folderToZip) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zipFolder(zos, folderToZip.toFile(), folderToZip.getFileName().toString());
        }
        return baos.toByteArray();
    }

    // Helper method to zip a folder
    private void zipFolder(ZipOutputStream zos, File folder, String parentFolder) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipFolder(zos, file, parentFolder + "/" + file.getName());
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                    zos.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }
    }
}
