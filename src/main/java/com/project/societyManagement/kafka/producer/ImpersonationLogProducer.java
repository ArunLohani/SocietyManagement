package com.project.societyManagement.kafka.producer;

import com.project.societyManagement.kafka.dto.ImpersonationLogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImpersonationLogProducer {

    private final KafkaTemplate<String, ImpersonationLogEvent> kafkaTemplate;

    public void publishImpersonationLog(ImpersonationLogEvent impersonationLogEvent){

        this.kafkaTemplate.send("impersonation-log",impersonationLogEvent);
    }

}
