package aivlebigproject.domain;

import aivlebigproject.domain.*;
import aivlebigproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class TimetableCreate extends AbstractEvent {

    private Long docId;
    private Long id;
    private Long templateId;
    private String fileUrl;

    public TimetableCreate(DocumentSave aggregate) {
        super(aggregate);
    }

    public TimetableCreate() {
        super();
    }
}
//>>> DDD / Domain Event
