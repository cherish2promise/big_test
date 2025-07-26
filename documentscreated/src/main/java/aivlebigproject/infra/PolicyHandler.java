package aivlebigproject.infra;

import aivlebigproject.config.kafka.KafkaProcessor;
import aivlebigproject.domain.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    FuneralInfoRepository funeralInfoRepository;

    @Autowired
    DocumentSaveRepository documentSaveRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='CustomerRegistered'"
    )
    public void wheneverCustomerRegistered_UserInfoPolicy(
        @Payload CustomerRegistered customerRegistered
    ) {
        CustomerRegistered event = customerRegistered;
        System.out.println(
            "\n\n##### listener UserInfoPolicy : " + customerRegistered + "\n\n"
        );

        // Sample Logic //
        FuneralInfo.userInfoPolicy(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='FuneralRegiste'"
    )
    public void wheneverFuneralRegiste_BoguAi(
        @Payload FuneralRegiste funeralRegiste
    ) {
        FuneralRegiste event = funeralRegiste;
        System.out.println(
            "\n\n##### listener BoguAi : " + funeralRegiste + "\n\n"
        );
        // Comments //
        //정책상에서 AI 호출한다 fastAPI  반환값

        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='FuneralRegiste'"
    )
    public void wheneverFuneralRegiste_SamangAi(
        @Payload FuneralRegiste funeralRegiste
    ) {
        FuneralRegiste event = funeralRegiste;
        System.out.println(
            "\n\n##### listener SamangAi : " + funeralRegiste + "\n\n"
        );
        // Comments //
        //정책상에서 AI 호출한다 fastAPI

        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='FuneralRegiste'"
    )
    public void wheneverFuneralRegiste_TimetableAi(
        @Payload FuneralRegiste funeralRegiste
    ) {
        FuneralRegiste event = funeralRegiste;
        System.out.println(
            "\n\n##### listener TimetableAi : " + funeralRegiste + "\n\n"
        );
        // Sample Logic //

    }
}
//>>> Clean Arch / Inbound Adaptor
