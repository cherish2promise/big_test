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
    @Autowired 
    ObjectMapper objectMapper; 

    //@StreamListener(KafkaProcessor.INPUT)
    //public void whatever(@Payload String eventString) {
    //}

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='CustomerRegistered'")
    public void wheneverCustomerRegistered_UserInfoPolicy(
            @Payload CustomerRegistered customerRegistered) {
        // 수신된 이벤트를 로그로 출력
        System.out.println(
                "\n\n##### listener UserInfoPolicy : " + customerRegistered + "\n\n");
        FuneralInfo.userInfoPolicy(customerRegistered);
    }

    @StreamListener(value = KafkaProcessor.INPUT,condition = "headers['type']=='FuneralRegiste'" )
    public void wheneverFuneralRegiste_BoguAi(
            @Payload FuneralRegiste funeralRegiste) {
        // 수신된 이벤트를 로그로 출력
        System.out.println(
                "\n\n##### listener BoguAi : " + funeralRegiste + "\n\n");
        kafkaProcessor.outboundFuneralRequest().send(
                MessageBuilder
                        .withPayload(funeralRegiste)
                        .setHeader("type", "FuneralRegiste")
                        .build());

        System.out.println("Kafka로 funeral-request 메시지(Bogu AI용) 전송 완료");
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='FuneralRegiste'")
    public void wheneverFuneralRegiste_SamangAi(
            @Payload FuneralRegiste funeralRegiste) {
        // 수신된 이벤트를 로그로 출력
        System.out.println(
                "\n\n##### listener SamangAi (Kafka) : " + funeralRegiste + "\n\n");

        // Samang AI 서비스가 구독할 Kafka 토픽으로 FuneralRegiste 이벤트를 전송
        kafkaProcessor.outboundSamangAiRequest().send( // <-- 새로 정의한 채널 사용
                MessageBuilder
                        .withPayload(funeralRegiste)
                        .setHeader("type", "FuneralRegiste") // AI 서비스에서 이벤트 타입 식별 가능하도록 헤더 추가
                        .build());
        System.out.println("Kafka로 samang-ai-request 메시지 전송 완료");
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='FuneralRegiste'")
    public void wheneverFuneralRegiste_TimetableAi(
            @Payload FuneralRegiste funeralRegiste) {
        // 수신된 이벤트를 로그로 출력
        System.out.println(
                "\n\n##### listener TimetableAi (Kafka) : " + funeralRegiste + "\n\n");
        kafkaProcessor.outboundTimetableAiRequest().send(
                MessageBuilder
                        .withPayload(funeralRegiste)
                        .setHeader("type", "FuneralRegiste")
                        .build());
        System.out.println("Kafka로 timetable-ai-request 메시지 전송 완료");
    }

    // 부고장 완료 토픽 처리
    @StreamListener(value = KafkaProcessor.INPUT_FUNERAL_REQUEST_PROCESSED) 
    public void handleFuneralRequestProcessed(@Payload String message) {
        processDocumentCompletion(message, "부고장");
    }

    // 사망 신고서 완료 토픽 처리
    @StreamListener(value = KafkaProcessor.INPUT_SAMANG_AI_PROCESSED) 
    public void handleSamangAiProcessed(@Payload String message) {
        processDocumentCompletion(message, "사망신고서");
    }

    // 장례 일정표 완료 토픽 처리
    @StreamListener(value = KafkaProcessor.INPUT_TIMETABLE_AI_PROCESSED) 
    public void handleTimetableAiProcessed(@Payload String message) {
        processDocumentCompletion(message, "장례일정표");
    }

    private void processDocumentCompletion(String message, String documentType) {
        System.out.println(String.format("\n\n##### Received %s completion message: %s\n\n", documentType, message));
        try {
            Map<String, Object> result = objectMapper.readValue(message, Map.class);
            if (!result.containsKey("id") || !result.containsKey("template_id")) {
                System.err.println("Error: Missing 'id' or 'template_id' in processed message for " + documentType
                        + ". Cannot process.");
                return;
            }
            Long originalRequestId = ((Number) result.get("id")).longValue();
            Long templateId = ((Number) result.get("template_id")).longValue();
            String fileUrl = (String) result.get("file_url");

            if (fileUrl != null && !fileUrl.trim().isEmpty()) {
                DocumentSave.saveOrUpdate(originalRequestId, templateId, fileUrl);
                System.out.println(String.format(
                        "%s generated successfully for Request ID '%d', Template ID '%d'. File URL saved.",
                        documentType, originalRequestId, templateId));
            } else {
                System.out.println(String.format(
                        "Request ID '%d', Template ID '%d' for %s: No valid file_url found. Assuming document generation failure.",
                        originalRequestId, templateId, documentType));
            }
        } catch (Exception e) {
            System.err.println("Error processing " + documentType + " completion message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// >>> Clean Arch / Inbound Adaptor