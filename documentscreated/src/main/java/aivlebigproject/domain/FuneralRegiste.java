package aivlebigproject.domain;

import aivlebigproject.domain.*;
import aivlebigproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class FuneralRegiste extends AbstractEvent {

    private Long id;
    private String name;
    private String nameHanja;
    private String rrn;
    private String gender;
    private String religion;
    private String relationToHouseholdHead;
    private Date reportRegistrationDate;
    private String reportUserId;
    private String reporterName;
    private String reporterRrn;
    private String reporterRelationToDeceased;
    private String reporterAddress;
    private String reporterPhone;
    private String reporterEmail;
    private String submitterName;
    private String submitterRrn;
    private String funeralCompanyName;
    private String directorName;
    private String directorPhone;
    private String funeralHomeName;
    private String mortuaryInfo;
    private String funeralHomeAddress;
    private String funeralDuration;
    private Date processionDateTime;
    private String burialSiteInfo;
    private List<String> chiefMourners;
    private String templateKeyword;

    public FuneralRegiste(FuneralInfo aggregate) {
        super(aggregate);
    }

    public FuneralRegiste() {
        super();
    }

    public String getType() {
        return "FuneralRegiste"; // PolicyHandler의 condition과 정확히 일치해야 합니다.
    }
}
//>>> DDD / Domain Event
