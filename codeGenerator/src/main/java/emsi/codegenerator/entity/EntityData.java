package emsi.codegenerator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityData {
    private String entityName;
}
