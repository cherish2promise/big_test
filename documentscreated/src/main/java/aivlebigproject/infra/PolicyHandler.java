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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    private static final Logger logger = LoggerFactory.getLogger(PolicyHandler.class);

    @Autowired
    KafkaProcessor kafkaProcessor;
    @Autowired
    FuneralInfoRepository funeralInfoRepository;
    @Autowired
    ObjectMapper objectMapper; // JSON 직렬화/역직렬화를 위해 필요합니다.

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
        // 모든 이벤트를 여기서 받아서 로깅합니다.
        // 특정 타입의 이벤트만 처리하려면 아래 @StreamListener 메서드처럼 condition을 사용합니다.
        logger.info("##### Received event: {}", eventString);
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='CustomerRegistered'")
    public void wheneverCustomerRegistered_UserInfoPolicy(
                @Payload CustomerRegistered customerRegistered) {
        logger.info("\n\n##### listener UserInfoPolicy : {}\n\n", customerRegistered);
        FuneralInfo.userInfoPolicy(customerRegistered);
    }

    // FuneralRegiste 이벤트 발생 시, 동일한 토픽(aivlebigproject)으로 AI 요청 메시지 전송
    // 'aiTarget' 헤더와 'type' 헤더를 추가하여 AI와 다른 컨슈머들이 구분할 수 있도록 합니다.
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='FuneralRegiste'")
    public void handleFuneralRegisteForAI(
                @Payload FuneralRegiste funeralRegiste) {
        logger.info("\n\n##### listener FuneralRegiste for AI processing (using shared topic): {}\n\n", funeralRegiste);

        // Bogu AI 요청 (동일 토픽으로 전송)
        kafkaProcessor.outboundTopic().send( // <-- outboundTopic() 사용
                MessageBuilder
                        .withPayload(funeralRegiste)
                        .setHeader("type", "BoguAiRequest") // 이 메시지는 Bogu AI 요청임을 나타내는 새로운 타입
                        .setHeader("aiTarget", "BoguAi") // 어떤 AI를 위한 요청인지 추가 헤더로 명시 (AI 서비스가 이 헤더를 보고 필터링)
                        .build());
        logger.info("Kafka로 Bogu AI 요청 메시지 전송 완료 (aivlebigproject 토픽)");

        // Samang AI 요청 (동일 토픽으로 전송)
        kafkaProcessor.outboundTopic().send( // <-- outboundTopic() 사용
                MessageBuilder
                        .withPayload(funeralRegiste)
                        .setHeader("type", "SamangAiRequest") // Samang AI 요청임을 나타내는 새로운 타입
                        .setHeader("aiTarget", "SamangAi")
                        .build());
        logger.info("Kafka로 Samang AI 요청 메시지 전송 완료 (aivlebigproject 토픽)");

        // Timetable AI 요청 (동일 토픽으로 전송)
        kafkaProcessor.outboundTopic().send( // <-- outboundTopic() 사용
                MessageBuilder
                        .withPayload(funeralRegiste)
                        .setHeader("type", "TimetableAiRequest") // Timetable AI 요청임을 나타내는 새로운 타입
                        .setHeader("aiTarget", "TimetableAi")
                        .build());
        logger.info("Kafka로 Timetable AI 요청 메시지 전송 완료 (aivlebigproject 토픽)");
    }


    // AI로부터의 모든 응답을 동일한 토픽(aivlebigproject)에서 수신
    // 각 AI 서비스는 응답 메시지에 고유한 'type' 헤더를 포함하여 보내야 합니다.
    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='BoguDocumentCreated'")
    public void handleFuneralRequestProcessed(@Payload String message) {
        processDocumentCompletion(message, "부고장");
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='SamangDocumentCreated'")
    public void handleSamangAiProcessed(@Payload String message) {
        processDocumentCompletion(message, "사망신고서");
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='TimetableDocumentCreated'")
    public void handleTimetableAiProcessed(@Payload String message) {
        processDocumentCompletion(message, "장례일정표");
    }

    private void processDocumentCompletion(String message, String documentType) {
        logger.info(String.format("\n\n##### Received %s completion message: %s\n\n", documentType, message));
        try {
            Map<String, Object> result = objectMapper.readValue(message, Map.class);
            if (!result.containsKey("id") || !result.containsKey("template_id")) {
                logger.error("Error: Missing 'id' or 'template_id' in processed message for {}. Cannot process.", documentType);
                return;
            }
            Long originalRequestId = ((Number) result.get("id")).longValue();
            Long templateId = ((Number) result.get("template_id")).longValue();
            String fileUrl = (String) result.get("file_url");

            if (fileUrl != null && !fileUrl.trim().isEmpty()) {
                DocumentSave.saveOrUpdate(originalRequestId, templateId, fileUrl);
                logger.info(String.format(
                        "%s generated successfully for Request ID '%d', Template ID '%d'. File URL saved.",
                        documentType, originalRequestId, templateId));
            } else {
                logger.warn(String.format(
                        "Request ID '%d', Template ID '%d' for %s: No valid file_url found. Assuming document generation failure.",
                        originalRequestId, templateId, documentType));
            }
        } catch (Exception e) {
            logger.error("Error processing {} completion message: {}", documentType, e.getMessage(), e);
        }
    }
}