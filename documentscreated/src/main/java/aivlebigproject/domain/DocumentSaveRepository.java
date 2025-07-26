package aivlebigproject.domain;

import aivlebigproject.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "documentSaves",
    path = "documentSaves"
)
public interface DocumentSaveRepository
    extends PagingAndSortingRepository<DocumentSave, Long> {}
