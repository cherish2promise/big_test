package aivlebigproject.domain;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional; // findByIdAndTemplateId의 반환 타입으로 사용

@RepositoryRestResource(collectionResourceRel = "documentSaves", path = "documentSaves")
public interface DocumentSaveRepository extends PagingAndSortingRepository<DocumentSave, Long> {
    Optional<DocumentSave> findByIdAndTemplateId(Long id, Long templateId);
}