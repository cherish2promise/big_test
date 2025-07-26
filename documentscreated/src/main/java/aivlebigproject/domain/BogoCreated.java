package aivlebigproject.domain;

import aivlebigproject.domain.*;
import aivlebigproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class BogoCreated extends AbstractEvent {

    private Long docId;
    private Long id;
    private Long templateId;
    private String fileUrl;

    public BogoCreated(DocumentSave aggregate) {
        super(aggregate);
    }

    public BogoCreated() {
        super();
    }
}
//>>> DDD / Domain Event
