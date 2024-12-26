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
    private List<Entity> entities;
    private List<String> dependencies;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Entity {
        private String name;
        private String packageName;
        private List<Field> fields;
        private List<Relationship> relationships;// Example: "OneToOne", "OneToMany", "ManyToOne", "ManyToMany"

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Field {
        private String name;
        private String type;
        private boolean isRelationship; // Indicate if this field is a relationship
        private String relationType;
        private String targetEntity;    // Target entity name

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Relationship {
        private String relationType; // Example: "OneToOne", "OneToMany", etc.
        private String targetEntity; // Name of the target entity
    }
}
