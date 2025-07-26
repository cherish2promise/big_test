package aivlebigproject.config.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface KafkaProcessor {
    String INPUT = "event-in";
    String OUTPUT = "event-out";
    String OUTPUT_FUNERAL_REQUEST = "event-out-funeral-request"; // 새 토픽
    String INPUT_DOCUMENT_RESPONSE = "event-in-document-response"; // 응답용 토픽

    @Input(INPUT)
    SubscribableChannel inboundTopic();

    @Output(OUTPUT)
    MessageChannel outboundTopic();

    @Output(OUTPUT_FUNERAL_REQUEST)
    MessageChannel outboundFuneralRequest();

    @Input(INPUT_DOCUMENT_RESPONSE)
    SubscribableChannel inboundDocumentResponse();
}