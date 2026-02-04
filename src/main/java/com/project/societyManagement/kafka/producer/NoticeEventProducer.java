package com.project.societyManagement.kafka.producer;

import com.project.societyManagement.kafka.dto.NoticeCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoticeEventProducer {

    private final KafkaTemplate<String, NoticeCreatedEvent> kafkaTemplate;

    public NoticeEventProducer(KafkaTemplate<String, NoticeCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishNoticeCreated(NoticeCreatedEvent event) {
        kafkaTemplate.send("notice-created-topic", event);
    }
}
