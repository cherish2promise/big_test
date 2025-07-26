package aivlebigproject.domain;

import aivlebigproject.LoginApplication;
import aivlebigproject.domain.StaffRegiste;
import aivlebigproject.domain.UserLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "User_table")
@Data
//<<< DDD / Aggregate Root
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String loginId;

    private String loginPassword;

    private String name;

    @Embedded
    private Email email;

    private String role;

    @PostPersist
    public void onPostPersist() {
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        aivlebigproject.external.CustomerProfile customerProfile = new aivlebigproject.external.CustomerProfile();
        // mappings goes here
        LoginApplication.applicationContext
            .getBean(aivlebigproject.external.CustomerProfileService.class)
            .createcustomer(customerProfile);

        UserLogin userLogin = new UserLogin(this);
        userLogin.publishAfterCommit();

        StaffRegiste staffRegiste = new StaffRegiste(this);
        staffRegiste.publishAfterCommit();
    }

    public static UserRepository repository() {
        UserRepository userRepository = LoginApplication.applicationContext.getBean(
            UserRepository.class
        );
        return userRepository;
    }
}
//>>> DDD / Aggregate Root
