package emsi.codegenerator.repository;

import emsi.codegenerator.entity.ProjectRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRequestRepository extends MongoRepository<ProjectRequest,String> {
}
