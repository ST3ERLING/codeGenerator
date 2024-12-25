package emsi.codegenerator.service;

import emsi.codegenerator.entity.ProjectRequest;
import emsi.codegenerator.repository.ProjectRequestRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ProjectRequestRepository projectRequestRepository;

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

        // Step 4: Generate entity, service, controller, and repository code for each entity and place it in the project
        for (ProjectRequest.Entity entity : projectRequest.getEntities()) {
            // Generate and save entity code
            String entityCode = generateEntityCode(entity, projectRequest.getPackageName());
            String entityFilePath = tempDir + "/" + projectRequest.getArtifactId() + "/src/main/java/" +
                    projectRequest.getPackageName().replace(".", "/") + "/entity/" + entity.getName() + ".java";

            Path entityFile = Paths.get(entityFilePath);
            Files.createDirectories(entityFile.getParent());
            Files.write(entityFile, entityCode.getBytes());

            // Generate and save repository code
            String repositoryCode = generateRepositoryCode(entity, projectRequest.getPackageName());
            String repositoryFilePath = tempDir + "/" + projectRequest.getArtifactId() + "/src/main/java/" +
                    projectRequest.getPackageName().replace(".", "/") + "/repository/" + entity.getName() + "Repository.java";

            Path repositoryFile = Paths.get(repositoryFilePath);
            Files.createDirectories(repositoryFile.getParent());
            Files.write(repositoryFile, repositoryCode.getBytes());

            // Generate and save service code
            String serviceCode = generateServiceCode(entity, projectRequest.getPackageName());
            String serviceFilePath = tempDir + "/" + projectRequest.getArtifactId() + "/src/main/java/" +
                    projectRequest.getPackageName().replace(".", "/") + "/service/" + entity.getName() + "Service.java";

            Path serviceFile = Paths.get(serviceFilePath);
            Files.createDirectories(serviceFile.getParent());
            Files.write(serviceFile, serviceCode.getBytes());

            // Generate and save controller code
            String controllerCode = generateControllerCode(entity, projectRequest.getPackageName());
            String controllerFilePath = tempDir + "/" + projectRequest.getArtifactId() + "/src/main/java/" +
                    projectRequest.getPackageName().replace(".", "/") + "/controller/" + entity.getName() + "Controller.java";

            Path controllerFile = Paths.get(controllerFilePath);
            Files.createDirectories(controllerFile.getParent());
            Files.write(controllerFile, controllerCode.getBytes());
        }

        // Step 5: Generate and write the pom.xml file
        createPomXml(tempDir.resolve(projectRequest.getArtifactId()), projectRequest);

        // Step 6: Zip the updated project folder
        byte[] updatedProjectZip = zipProject(tempDir.resolve(projectRequest.getArtifactId()));

        // Step 7: Clean up
        Files.deleteIfExists(zipFilePath);

        projectRequestRepository.save(projectRequest);
        return updatedProjectZip;
    }

    private void createPomXml(Path projectPath, ProjectRequest projectRequest) throws IOException {
        // Generate dependencies dynamically using the chat API
        String dependencies = generatePomXmlDependencies(projectRequest);

        // Template for pom.xml with placeholders
        String pomTemplate = """
            <project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">
                <modelVersion>4.0.0</modelVersion>
                <parent>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-parent</artifactId>
                    <version>%s</version>
                    <relativePath/>
                </parent>
                <groupId>%s</groupId>
                <artifactId>%s</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                <name>%s</name>
                <description>Generated Spring Boot Project</description>
                <properties>
                    <java.version>%s</java.version>
                </properties>
                <dependencies>
                    %s
                </dependencies>
                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-maven-plugin</artifactId>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """;

        // Fill the template with project-specific details
        String pomXml = String.format(
                pomTemplate,
                projectRequest.getBootVersion(),
                projectRequest.getGroupId(),
                projectRequest.getArtifactId(),
                projectRequest.getName(),
                projectRequest.getJavaVersion(),
                dependencies
        );

        // Write the pom.xml to the project directory
        Path pomFile = projectPath.resolve("pom.xml");
        Files.writeString(pomFile, pomXml);
    }

    private String generatePomXmlDependencies(ProjectRequest projectRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a Maven pom.xml dependency block with the following details:\n");
        prompt.append("Dependencies:\n");
        for (String dependency : projectRequest.getDependencies()) {
            prompt.append("- ").append(dependency).append("\n");
        }
        prompt.append("Use the latest version for each dependency.");

        return callChatAPI(prompt.toString());
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
        prompt.append("Relationships:\n");
        for (ProjectRequest.Relationship relationship : entity.getRelationships()) {
            prompt.append("- ").append(relationship.getRelationType())
                    .append(" to ").append(relationship.getTargetEntity()).append("\n");
        }
        return prompt.toString();
    }

    private String generateRepositoryCode(ProjectRequest.Entity entity, String basePackage) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a Spring Boot Repository interface for the following entity:\n");
        prompt.append("Package: ").append(basePackage).append(".repository\n");
        prompt.append("Entity Name: ").append(entity.getName()).append("\n");
        return callChatAPI(prompt.toString());
    }

    private String generateServiceCode(ProjectRequest.Entity entity, String basePackage) {
        String prompt = buildServicePrompt(entity, basePackage);
        return callChatAPI(prompt);
    }

    private String buildServicePrompt(ProjectRequest.Entity entity, String basePackage) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a Spring Boot Service class for the following entity:\n");
        prompt.append("Package: ").append(basePackage).append(".service\n");
        prompt.append("Entity Name: ").append(entity.getName()).append("\n");
        prompt.append("Repository: ").append(basePackage).append(".repository.").append(entity.getName()).append("Repository\n");
        prompt.append("Operations:\n");
        prompt.append("- Find all ").append(entity.getName().toLowerCase()).append("s\n");
        prompt.append("- Find a ").append(entity.getName().toLowerCase()).append(" by ID\n");
        prompt.append("- Save a ").append(entity.getName().toLowerCase()).append("\n");
        prompt.append("- Delete a ").append(entity.getName().toLowerCase()).append(" by ID\n");
        return prompt.toString();
    }

    private String generateControllerCode(ProjectRequest.Entity entity, String basePackage) {
        String prompt = buildControllerPrompt(entity, basePackage);
        return callChatAPI(prompt);
    }

    private String buildControllerPrompt(ProjectRequest.Entity entity, String basePackage) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a Spring Boot Controller class for the following service:\n");
        prompt.append("Package: ").append(basePackage).append(".controller\n");
        prompt.append("Service Name: ").append(entity.getName()).append("Service\n");
        prompt.append("Entity Name: ").append(entity.getName()).append("\n");
        prompt.append("Endpoints:\n");
        prompt.append("- GET /api/").append(entity.getName().toLowerCase()).append("s: Fetch all ").append(entity.getName().toLowerCase()).append("s\n");
        prompt.append("- GET /api/").append(entity.getName().toLowerCase()).append("s/{id}: Fetch ").append(entity.getName().toLowerCase()).append(" by ID\n");
        prompt.append("- POST /api/").append(entity.getName().toLowerCase()).append("s: Save a new ").append(entity.getName().toLowerCase()).append("\n");
        prompt.append("- DELETE /api/").append(entity.getName().toLowerCase()).append("s/{id}: Delete a ").append(entity.getName().toLowerCase()).append(" by ID\n");
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

            return response.getBody(); // The generated code
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