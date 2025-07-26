package aivlebigproject.domain;

import aivlebigproject.domain.*;
import aivlebigproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class CustomerRegistered extends AbstractEvent {

    private Long id;
    private Long loginId;
    private String name;
    private String rnn;
    private Integer age;
    private String gender;
    private String address;
    private String email;
    private String phone;
    private String job;
    private Boolean hasChildren;
    private Boolean isMarried;
    private List<String> diseaseList;
    private Date birthDate;

    public CustomerRegistered(CustomerProfile aggregate) {
        super(aggregate);
    }

    public CustomerRegistered() {
        super();
    }
}
//>>> DDD / Domain Event
