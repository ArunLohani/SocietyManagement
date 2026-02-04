package com.project.societyManagement.kafka.consumer;

import com.project.societyManagement.kafka.dto.NoticeCreatedEvent;
import com.project.societyManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NoticeEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "notice-created-topic",
            groupId = "society-group"
    )
    public void handleNoticeCreated(NoticeCreatedEvent event) {
        notificationService.notifySociety(
                event.getTenantId(),
                "New Notice: " + event.getTitle(),
                "A new notice has been published: " + event.getTitle(), "/notices/" + event.getNoticeId()
        );
        System.out.println("Notice created: " + event);
    }
}
