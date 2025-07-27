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
import java.util.Optional; // Optional 임포트 유지


@Entity
@Table(name="FuneralInfo_table")
@Data // Lombok을 사용하여 getter, setter, toString, equals, hashCode 자동 생성

//<<< DDD / Aggregate Root
public class FuneralInfo {

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

    // 고객과의 연관성을 위해 customerId 필드 추가 (CustomerRegistered 이벤트에서 사용)
    private Long customerId;


    public static FuneralInfoRepository repository(){
        FuneralInfoRepository funeralInfoRepository = DocumentscreatedApplication.applicationContext.getBean(FuneralInfoRepository.class);
        return funeralInfoRepository;
    }

    //<<< Clean Arch / Port Method
    public void registerFuneralInfo(){

        // 1. 비즈니스 로직 구현:
        System.out.println("Registering funeral information for: " + this.getName());

        // 2. 외부 서비스 호출 (관련 로직 제거됨)

        // 3. 현재 FuneralInfo 객체 저장
        repository().save(this);


        // 4. 이벤트 발행:
        FuneralRegiste funeralRegiste = new FuneralRegiste(this);
        funeralRegiste.publishAfterCommit();

        System.out.println("Funeral information registered and event published for: " + this.getName());
    }
    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void userInfoPolicy(CustomerRegistered customerRegistered){

        // 비즈니스 로직 구현:
        System.out.println("Processing userInfoPolicy for CustomerRegistered event, Customer ID: " + customerRegistered.getId());

        Long customerId = customerRegistered.getId();

        if (customerId == null) {
            System.err.println("Error: Customer ID is null in CustomerRegistered event. Cannot process userInfoPolicy.");
            return;
        }

        // 2. 해당 customerId를 가진 FuneralInfo가 이미 존재하는지 확인
        Optional<FuneralInfo> existingFuneralInfo = repository().findByCustomerId(customerId);

        if (existingFuneralInfo.isPresent()) {
            // Case 2: 기존 FuneralInfo가 존재하는 경우 - 업데이트
            FuneralInfo funeralInfo = existingFuneralInfo.get();
            System.out.println("Existing FuneralInfo found for customer ID " + customerId + ". Updating...");

            // CustomerRegistered 이벤트의 정보를 기반으로 FuneralInfo 업데이트
            funeralInfo.setName(customerRegistered.getName());
            funeralInfo.setRrn(customerRegistered.getRnn());
            // funeralInfo.setReporterName(customerRegistered.getRegistrantName()); // 이 라인은 제거됨
            // ... (CustomerRegistered 이벤트가 제공하는 데이터에 따라 필드 업데이트)

            repository().save(funeralInfo); // 업데이트된 FuneralInfo 저장
            System.out.println("Updated FuneralInfo for customer ID: " + customerId);

        } else {
            // Case 1: 기존 FuneralInfo가 존재하지 않는 경우 - 새로 생성
            System.out.println("No existing FuneralInfo found for customer ID " + customerId + ". Creating new one...");

            FuneralInfo newFuneralInfo = new FuneralInfo();
            newFuneralInfo.setCustomerId(customerId); // 새로 생성된 FuneralInfo에 customerId 설정

            // CustomerRegistered 이벤트의 데이터를 바탕으로 FuneralInfo의 초기 필드를 설정합니다.
            newFuneralInfo.setName(customerRegistered.getName());
            newFuneralInfo.setRrn(customerRegistered.getRnn());
            // newFuneralInfo.setReporterName(customerRegistered.getName()); // 이 라인도 필요 없다면 제거
            // newFuneralInfo.setReportRegistrationDate(new Date());

            // 기타 필요한 기본값 설정 (예: templateKeyword의 기본값)
            newFuneralInfo.setTemplateKeyword("default-template");
            newFuneralInfo.setChiefMourners(Collections.emptyList()); // 초기에는 비어있는 리스트

            repository().save(newFuneralInfo); // 새로운 FuneralInfo 저장
            System.out.println("Created new FuneralInfo with ID: " + newFuneralInfo.getId() + " for customer ID: " + customerId);
        }
    }
    //>>> Clean Arch / Port Method
}
//>>> DDD / Aggregate Root