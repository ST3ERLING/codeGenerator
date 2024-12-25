package emsi.codegenerator.service;

import emsi.codegenerator.entity.ProjectRequest;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

@Service
public class ProjectGenerationService {

    private static final String SPRING_INITIALIZR_URL = "https://start.spring.io/starter.zip";
    private final RestTemplate restTemplate;

    public ProjectGenerationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] generateSpringProject(ProjectRequest projectRequest) throws Exception {
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

        return updatedProjectZip;
    }

    private String generateEntityCode(ProjectRequest.Entity entity, String basePackage) {
        String prompt = buildEntityPrompt(entity, basePackage);
        return callChatAPI(prompt);
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

    private byte[] zipProject(Path folderToZip) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zipFolder(zos, folderToZip.toFile(), folderToZip.getFileName().toString());
        }
        return baos.toByteArray();
    }

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

