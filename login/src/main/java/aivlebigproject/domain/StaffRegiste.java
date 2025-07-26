package aivlebigproject.domain;

import aivlebigproject.domain.*;
import aivlebigproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class StaffRegiste extends AbstractEvent {

    private Long id;

    public StaffRegiste(User aggregate) {
        super(aggregate);
    }

    public StaffRegiste() {
        super();
    }
}
//>>> DDD / Domain Event
