package aivlebigproject.domain;

import aivlebigproject.DocumentscreatedApplication;
import aivlebigproject.domain.BogoCreated;
import aivlebigproject.domain.SamangCreate;
import aivlebigproject.domain.TimetableCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "DocumentSave_table")
@Data
//<<< DDD / Aggregate Root
public class DocumentSave {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long docId;

    private Long id;

    private Long templateId;

    private String fileUrl;

    public static DocumentSaveRepository repository() {
        DocumentSaveRepository documentSaveRepository = DocumentscreatedApplication.applicationContext.getBean(
            DocumentSaveRepository.class
        );
        return documentSaveRepository;
    }
}
//>>> DDD / Aggregate Root
