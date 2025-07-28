package aivlebigproject.domain; // 첫 줄의 package 선언은 그대로 둡니다.
import aivlebigproject.DocumentscreatedApplication;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor; 
import java.util.Optional; 
import lombok.AllArgsConstructor;


@Entity
@Table(name = "DocumentSave_table")
@Data
@NoArgsConstructor 
@AllArgsConstructor 
//<<< DDD / Aggregate Root
public class DocumentSave {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long docId;
    private Long id;
    private Long templateId;
    private String fileUrl;
    public static DocumentSaveRepository repository() {
        return DocumentscreatedApplication.applicationContext.getBean(
            DocumentSaveRepository.class
        );
    }


}

public static DocumentSave saveOrUpdate(Long originalRequestId, Long templateId, String fileUrl) {
        DocumentSaveRepository documentSaveRepository = DocumentSave.repository();
        Optional<DocumentSave> existingDoc = documentSaveRepository.findByIdAndTemplateId(originalRequestId, templateId);
        DocumentSave documentSave;
        if (existingDoc.isPresent()) {
            documentSave = existingDoc.get();
            documentSave.setFileUrl(fileUrl); // 기존 문서의 파일 URL 업데이트
            System.out.println(String.format("Updating existing DocumentSave entry in DB for Request ID '%d', Template ID '%d'.",
                    originalRequestId, templateId));
        } else {
            documentSave = new DocumentSave();
            documentSave.setId(originalRequestId); // DocumentSave 엔티티의 'id' 필드에 원본 요청 ID 저장
            documentSave.setTemplateId(templateId);
        documentSave.setFileUrl(fileUrl);
        System.out.println(String.format("Creating new DocumentSave entry in DB for Request ID '%d', Template ID '%d'.",
                originalRequestId, templateId));
    }
    return documentSaveRepository.save(documentSave);
}

