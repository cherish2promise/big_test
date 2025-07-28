package aivlebigproject.infra;

import aivlebigproject.config.kafka.KafkaProcessor;
import aivlebigproject.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.messaging.support.MessageBuilder; 
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    KafkaProcessor kafkaProcessor;
    @Autowired
    FuneralInfoRepository funeralInfoRepository;


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='CustomerRegistered'"
    )
    public void wheneverCustomerRegistered_UserInfoPolicy(
        @Payload CustomerRegistered customerRegistered
    ) {
        // 수신된 이벤트를 로그로 출력
        System.out.println(
            "\n\n##### listener UserInfoPolicy : " + customerRegistered + "\n\n"
        );
        FuneralInfo.userInfoPolicy(customerRegistered);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='FuneralRegiste'"
    )
    public void wheneverFuneralRegiste_BoguAi(
        @Payload FuneralRegiste funeralRegiste
    ) {
        // 수신된 이벤트를 로그로 출력
        System.out.println(
            "\n\n##### listener BoguAi : " + funeralRegiste + "\n\n"
        );
        kafkaProcessor.outboundFuneralRequest().send(
            MessageBuilder
                .withPayload(funeralRegiste)
                .setHeader("type", "FuneralRegiste")
                .build()
        );

        System.out.println("Kafka로 funeral-request 메시지(Bogu AI용) 전송 완료");
    }

    /**
     * FuneralRegiste 이벤트 발생 시, Samang AI 서비스를 호출합니다.
     * 이 서비스는 Kafka 메시지를 통해 요청을 받는다고 가정합니다.
     *
     * @param funeralRegiste 장례 등록 이벤트 페이로드
     */
    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='FuneralRegiste'"
    )
    public void wheneverFuneralRegiste_SamangAi(
        @Payload FuneralRegiste funeralRegiste
    ) {
        // 수신된 이벤트를 로그로 출력
        System.out.println(
            "\n\n##### listener SamangAi (Kafka) : " + funeralRegiste + "\n\n"
        );

        // Samang AI 서비스가 구독할 Kafka 토픽으로 FuneralRegiste 이벤트를 전송
        kafkaProcessor.outboundSamangAiRequest().send( // <-- 새로 정의한 채널 사용
            MessageBuilder
                .withPayload(funeralRegiste)
                .setHeader("type", "FuneralRegiste") // AI 서비스에서 이벤트 타입 식별 가능하도록 헤더 추가
                .build()
        );
        System.out.println("Kafka로 samang-ai-request 메시지 전송 완료");
    }
    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='FuneralRegiste'"
    )
    public void wheneverFuneralRegiste_TimetableAi(
        @Payload FuneralRegiste funeralRegiste
    ) {
        // 수신된 이벤트를 로그로 출력
        System.out.println(
            "\n\n##### listener TimetableAi (Kafka) : " + funeralRegiste + "\n\n"
        );
        kafkaProcessor.outboundTimetableAiRequest().send( 
            MessageBuilder
                .withPayload(funeralRegiste)
                .setHeader("type", "FuneralRegiste") 
                .build()
        );
        System.out.println("Kafka로 timetable-ai-request 메시지 전송 완료");
    }
    @StreamListener(
        value = {
            KafkaProcessor.INPUT_FUNERAL_REQUEST_PROCESSED, // 부고장 완료 토픽
            KafkaProcessor.INPUT_SAMANG_AI_PROCESSED,       // 사망 신고서 완료 토픽
            KafkaProcessor.INPUT_TIMETABLE_AI_PROCESSED     // 장례 일정표 완료 토픽
        }
    )public void handleAllProcessedDocuments(@Payload String message) {
        System.out.println("\n\n##### Received a document completion message: " + message + "\n\n");
        try {
            Map<String, Object> result = objectMapper.readValue(message, Map.class);
            if (!result.containsKey("id") || !result.containsKey("template_id")) {
                System.err.println("Error: Missing 'id' or 'template_id' in processed message. Cannot process.");
                return;
            }
            Long originalRequestId = ((Number) result.get("id")).longValue();
            Long templateId = ((Number) result.get("template_id")).longValue();
            String fileUrl = (String) result.get("file_url");
                // fileUrl의 유효성 (null이 아니고 빈 문자열이 아닌지)으로 문서 생성 성공 여부 판단
            if (fileUrl != null && !fileUrl.trim().isEmpty()) {
                // DocumentSave 도메인 엔티티의 정적 메소드를 호출하여 DB에 기록
                DocumentSave.saveOrUpdate(originalRequestId, templateId, fileUrl); 
                System.out.println(String.format("Document generated successfully for Request ID '%d', Template ID '%d'. File URL saved.",
                        originalRequestId, templateId));
            } else {
                System.out.println(String.format("Request ID '%d', Template ID '%d': No valid file_url found in message. Assuming document generation failure.",
                        originalRequestId, templateId));
                // 필요하다면, 문서 생성 실패에 대한 별도의 로그 또는 DB 기록 로직 추가 가능
            }
        } catch (Exception e) {
            System.err.println("Error processing document completion message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // `checkAndNotifyIfAllDocumentsCompleted` 메소드는 사용자 요청에 따라 제거됨.
}

//>>> Clean Arch / Inbound Adaptor