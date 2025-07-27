package aivlebigproject.config.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KafkaProcessor {

    String INPUT = "inboundTopic";
    String OUTPUT = "outboundTopic";

    String OUTPUT_FUNERAL_REQUEST = "outboundFuneralRequest"; // Bogu AI용 (현재 있음)
    String OUTPUT_SAMANG_AI_REQUEST = "outboundSamangAiRequest"; // <-- Samang AI용 추가!
    String OUTPUT_TIMETABLE_AI_REQUEST = "outboundTimetableAiRequest"; // <-- Timetable AI용 추가!

    @Input(INPUT)
    MessageChannel inboundTopic();

    @Output(OUTPUT)
    MessageChannel outboundTopic();

    @Output(OUTPUT_FUNERAL_REQUEST)
    MessageChannel outboundFuneralRequest();

    @Output(OUTPUT_SAMANG_AI_REQUEST) // <-- Samang AI용 Output 채널 정의
    MessageChannel outboundSamangAiRequest();

    @Output(OUTPUT_TIMETABLE_AI_REQUEST) // <-- Timetable AI용 Output 채널 정의
    MessageChannel outboundTimetableAiRequest();
}