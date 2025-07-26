package aivlebigproject.domain;

import aivlebigproject.domain.*;
import aivlebigproject.infra.AbstractEvent;
import java.util.*;
import lombok.*;

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
    private Object diseaseList;
    private Date birthDate;
}
