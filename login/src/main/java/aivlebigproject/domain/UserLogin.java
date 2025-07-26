package aivlebigproject.domain;

import aivlebigproject.domain.*;
import aivlebigproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class UserLogin extends AbstractEvent {

    private Long id;
    private String loginId;
    private String loginPassword;
    private String name;
    private Email email;
    private String role;

    public UserLogin(User aggregate) {
        super(aggregate);
    }

    public UserLogin() {
        super();
    }
}
//>>> DDD / Domain Event
