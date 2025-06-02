package com.example.teamproject.kafka;

import com.example.kafka_schemas.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
@RequiredArgsConstructor
public class UserKafkaProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void sendSignup(UserEvent event) {
        // 토픽: user.signup, 키: username
        kafkaTemplate.send("user.event", event.getUsername(), event);
        log.info("Kafka 전송 성공 → topic=user.signup, key={}, payload={}",
                event.getUsername(), event);
    }
}