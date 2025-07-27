package aivlebigproject.infra;

import aivlebigproject.config.kafka.KafkaProcessor;
import aivlebigproject.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.messaging.support.MessageBuilder; 

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional // Spring Framework의 @Transactional 어노테이션 사용
public class PolicyHandler {

    @Autowired
    KafkaProcessor kafkaProcessor;
    @Autowired
    FuneralInfoRepository funeralInfoRepository;
    @Autowired
    DocumentSaveRepository documentSaveRepository;


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {
    }

    /**
     * CustomerRegistered 이벤트 발생 시, 사용자 정보 정책을 처리합니다.
     * FuneralInfo 도메인의 userInfoPolicy 정적 메서드를 호출하여 비즈니스 로직을 위임합니다.
     * @param customerRegistered 고객 등록 이벤트 페이로드
     */
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

    /**
     * FuneralRegiste 이벤트 발생 시, Bogu AI 서비스를 호출합니다.
     * 이 서비스는 Kafka 메시지를 통해 요청을 받는다고 가정하고,
     * 아웃바운드 Kafka 채널로 FuneralRegiste 이벤트를 재전송합니다.
     *
     * @param funeralRegiste 장례 등록 이벤트 페이로드
     */
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

        System.out.println("📨 Kafka로 funeral-request 메시지(Bogu AI용) 전송 완료");
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
        System.out.println("📨 Kafka로 samang-ai-request 메시지 전송 완료");
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
        System.out.println("📨 Kafka로 timetable-ai-request 메시지 전송 완료");
    }
}
//>>> Clean Arch / Inbound Adaptor