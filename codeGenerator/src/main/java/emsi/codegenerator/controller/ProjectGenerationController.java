package emsi.codegenerator.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

@RestController
public class ProjectGenerationController {

    private static final String SPRING_INITIALIZR_URL = "https://start.spring.io/starter.zip";

    @GetMapping("/generate-project")
    public ResponseEntity<byte[]> generateProject(
            @RequestParam String type,
            @RequestParam String language,
            @RequestParam String bootVersion,
            @RequestParam String baseDir,
            @RequestParam String groupId,
            @RequestParam String artifactId,
            @RequestParam String name,
            @RequestParam String packageName,
            @RequestParam String javaVersion,
            @RequestParam String entityName) {

        try {
            // Step 1: Generate the Spring Boot project using Spring Initializr
            String url = String.format("%s?type=%s&language=%s&bootVersion=%s&baseDir=%s&groupId=%s&artifactId=%s&name=%s&packageName=%s&javaVersion=%s",
                    SPRING_INITIALIZR_URL, type, language, bootVersion, baseDir, groupId, artifactId, name, packageName, javaVersion);

            RestTemplate restTemplate = new RestTemplate();
            byte[] projectZip = restTemplate.getForObject(url, byte[].class);

            // Step 2: Create a temporary directory to extract the Spring Boot project
            Path tempDir = Files.createTempDirectory("project-temp");
            Path zipFilePath = tempDir.resolve(artifactId + ".zip");
            Files.write(zipFilePath, projectZip);

            // Step 3: Unzip the Spring Boot project directly into tempDir (without extra folders)
            unzipProject(tempDir, zipFilePath.toString());

            // Step 4: Generate the entity code and place it in the existing src directory of the unzipped project
            String entityCode = generateEntityCode(entityName);
            String entityFilePath = tempDir + "/" + artifactId + "/src/main/java/"+packageName+"/entity/" +  entityName + ".java";
            Path entityFile = Paths.get(entityFilePath);
            Files.createDirectories(entityFile.getParent()); // Ensure the directory exists
            Files.write(entityFile, entityCode.getBytes());  // Write the entity code to the file

            // Step 5: Zip the updated project folder with the entity file included
            byte[] updatedProjectZip = zipProject(tempDir.resolve(artifactId));

            // Step 6: Clean up the temporary directory and the initial Spring Initializr zip file
            Files.deleteIfExists(zipFilePath);

            // Return the zip file with the entity added
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + artifactId + ".zip");
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(updatedProjectZip, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Error generating project: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // Generate the entity code using AI or a predefined template
    private String generateEntityCode(String entityName) {
        // Example entity code (can be dynamically generated via AI or predefined template)
        return "package com.example.myproject;\n\n" +
                "public class " + entityName + " {\n" +
                "    private Long id;\n" +
                "    private String name;\n\n" +
                "    // Getters and setters\n" +
                "}\n";
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
