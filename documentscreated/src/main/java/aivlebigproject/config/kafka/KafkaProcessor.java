package aivlebigproject.config.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KafkaProcessor {

    // 내부 토픽은 요거로
    String INPUT = "inboundTopic";
    String OUTPUT = "outboundTopic";

    @Input(INPUT)
    MessageChannel inboundTopic();

    @Output(OUTPUT)
    MessageChannel outboundTopic();
}

