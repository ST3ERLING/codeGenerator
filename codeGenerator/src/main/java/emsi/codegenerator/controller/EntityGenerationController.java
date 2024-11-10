package emsi.codegenerator.controller;

import emsi.codegenerator.entity.EntityData;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RestController
public class EntityGenerationController {

    private static final String AI_API_URL = "https://api.openai.com/v1/completions"; // OpenAI endpoint

    @PostMapping("/create-entity")
    public ResponseEntity<String> createEntity(@RequestBody EntityData entityData) {
        String entityName = entityData.getEntityName();
        String entityFolderPath = "entite/";

        try {
            // Step 1: Create the 'entite' folder if it doesn't exist
            File folder = new File(entityFolderPath);
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Step 2: Use AI to generate entity code
            String entityCode = generateEntityCode(entityName);

            // Step 3: Create the file for the entity with generated code
            File entityFile = new File(entityFolderPath + entityName + ".java");
            try (FileWriter writer = new FileWriter(entityFile)) {
                writer.write(entityCode);
            }

            return new ResponseEntity<>("Entity created successfully.", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error creating entity: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method to generate entity code using AI
    private String generateEntityCode(String entityName) {
        try {
            // Example: Sending a request to OpenAI's GPT-3 for generating entity code
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer YOUR_OPENAI_API_KEY");

            String body = "{"
                    + "\"model\": \"gpt-3.5-turbo\","
                    + "\"messages\": [{\"role\": \"system\", \"content\": \"Generate a Java entity class\"},"
                    + "{\"role\": \"user\", \"content\": \"Generate a Java class for an entity called " + entityName + "\"}]"
                    + "}";

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> aiResponse = restTemplate.exchange(AI_API_URL, HttpMethod.POST, request, String.class);

            // Extract the generated code from the AI response
            return aiResponse.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "public class " + entityName + " {\n    // Generated entity code\n}";
        }
    }
}

