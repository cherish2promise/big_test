package aivlebigproject.domain;

import aivlebigproject.DocumentscreatedApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;
import java.time.LocalDate;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;


@Entity
@Table(name="FuneralInfo_table")
@Data

//<<< DDD / Aggregate Root
public class FuneralInfo  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
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
    
    @ElementCollection
private List<String> chiefMourners;    
    
    
private String templateKeyword;


    public static FuneralInfoRepository repository(){
        FuneralInfoRepository funeralInfoRepository = DocumentscreatedApplication.applicationContext.getBean(FuneralInfoRepository.class);
        return funeralInfoRepository;
    }



//<<< Clean Arch / Port Method
    public void registerFuneralInfo(){
        
        //implement business logic here:
        

        aivlebigproject.external.FuneralInfoQuery funeralInfoQuery = new aivlebigproject.external.FuneralInfoQuery();
        // funeralInfoQuery.set??()        
          = FuneralInfoApplication.applicationContext
            .getBean(aivlebigproject.external.Service.class)
            .funeralInfo(funeralInfoQuery);

        FuneralRegiste funeralRegiste = new FuneralRegiste(this);
        funeralRegiste.publishAfterCommit();
    }
//>>> Clean Arch / Port Method

//<<< Clean Arch / Port Method
    public static void userInfoPolicy(CustomerRegistered customerRegistered){
        
        //implement business logic here:
        
        /** Example 1:  new item 
        FuneralInfo funeralInfo = new FuneralInfo();
        repository().save(funeralInfo);

        */

        /** Example 2:  finding and process
        

        repository().findById(customerRegistered.get???()).ifPresent(funeralInfo->{
            
            funeralInfo // do something
            repository().save(funeralInfo);


         });
        */

        
    }
//>>> Clean Arch / Port Method


}
//>>> DDD / Aggregate Root
