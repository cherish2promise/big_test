package aivlebigproject.config.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KafkaProcessor {

    // 내부 토픽은 요거로
    String INPUT = "inboundTopic";
    String OUTPUT = "outboundTopic";

    // --- Spring Boot가 AI 서비스로 요청을 보내는 채널 ---
    String OUTPUT_FUNERAL_REQUEST = "outboundFuneralRequest"; // Bogu AI 등 특정 장례 요청 AI로 보내는 토픽
    String OUTPUT_SAMANG_AI_REQUEST = "outboundSamangAiRequest"; // Samang AI로 요청 보내는 토픽
    String OUTPUT_TIMETABLE_AI_REQUEST = "outboundTimetableAiRequest"; // Timetable AI로 요청 보내는 토픽

    // --- Spring Boot가 AI 서비스로부터 처리 결과를 받는 채널 ---
    String INPUT_FUNERAL_REQUEST_PROCESSED = "inboundFuneralRequestProcessed"; // FastAPI에서 처리 후 보내는 응답을 받는 토픽 (장례 요청 처리 완료)
    String INPUT_SAMANG_AI_PROCESSED = "inboundSamangAiProcessed"; // Samang AI가 처리 후 보내는 응답을 받는 토픽
    String INPUT_TIMETABLE_AI_PROCESSED = "inboundTimetableAiProcessed"; // Timetable AI가 처리 후 보내는 응답을 받는 토픽

    @Input(INPUT)
    MessageChannel inboundTopic();

    @Output(OUTPUT)
    MessageChannel outboundTopic();

    // --- @Output 채널 정의 ---
    @Output(OUTPUT_FUNERAL_REQUEST)
    MessageChannel outboundFuneralRequest();

    @Output(OUTPUT_SAMANG_AI_REQUEST)
    MessageChannel outboundSamangAiRequest();

    @Output(OUTPUT_TIMETABLE_AI_REQUEST)
    MessageChannel outboundTimetableAiRequest();

    // --- @Input 채널 정의 ---
    @Input(INPUT_FUNERAL_REQUEST_PROCESSED)
    MessageChannel inboundFuneralRequestProcessed();

    @Input(INPUT_SAMANG_AI_PROCESSED)
    MessageChannel inboundSamangAiProcessed();

    @Input(INPUT_TIMETABLE_AI_PROCESSED)
    MessageChannel inboundTimetableAiProcessed();
}