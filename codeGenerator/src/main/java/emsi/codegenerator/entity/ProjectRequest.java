package emsi.codegenerator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "project_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {

    @Id
    private String id;
    private String type;
    private String language;
    private String bootVersion;
    private String baseDir;
    private String groupId;
    private String artifactId;
    private String name;
    private String packageName;
    private String javaVersion;
    private List<Entity> entities; // Updated to allow multiple entities

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Entity {
        private String name;
        private String packageName;
        private List<Field> fields;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Field {
        private String name;
        private String type;
    }
}
