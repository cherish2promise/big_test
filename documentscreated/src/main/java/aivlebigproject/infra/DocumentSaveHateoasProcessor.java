package aivlebigproject.infra;

import aivlebigproject.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class DocumentSaveHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<DocumentSave>> {

    @Override
    public EntityModel<DocumentSave> process(EntityModel<DocumentSave> model) {
        return model;
    }
}
