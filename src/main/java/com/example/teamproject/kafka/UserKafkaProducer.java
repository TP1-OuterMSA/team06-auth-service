package com.example.teamproject.kafka;

import com.example.kafka_schemas.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserKafkaProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;


    public void sendSignup(UserEvent event) {
        String topicName = "user.event";

        CompletableFuture<SendResult<String, UserEvent>> future =
                kafkaTemplate.send(topicName, event.getUsername(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                        "[Kafka Producer] 전송 성공 → topic={}, partition={}, offset={}, key={}, payload={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.getUsername(),
                        event
                );
            } else {
                log.error(
                        "[Kafka Producer] 전송 실패 → topic={}, key={}, payload={}",
                        topicName,
                        event.getUsername(),
                        event,
                        ex
                );
            }
        });
    }
}
