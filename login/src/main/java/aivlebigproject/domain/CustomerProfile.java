package aivlebigproject.domain;

import aivlebigproject.LoginApplication;
import aivlebigproject.domain.CustomerRegistered;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CustomerProfile_table")
@Data
//<<< DDD / Aggregate Root
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @ElementCollection
    private List<String> diseaseList;

    private Date birthDate;

    @PostPersist
    public void onPostPersist() {
        CustomerRegistered customerRegistered = new CustomerRegistered(this);
        customerRegistered.publishAfterCommit();
    }

    public static CustomerProfileRepository repository() {
        CustomerProfileRepository customerProfileRepository = LoginApplication.applicationContext.getBean(
            CustomerProfileRepository.class
        );
        return customerProfileRepository;
    }
}
//>>> DDD / Aggregate Root
