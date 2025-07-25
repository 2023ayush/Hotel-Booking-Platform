package com.properyservice.service;

import com.properyservice.dto.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

    @Autowired
    private KafkaTemplate<String, EmailRequest> kafkaTemplate;

    private static final String TOPIC = "send_email";

    public void sendEmail(EmailRequest request) {
        kafkaTemplate.send(TOPIC, request);
    }
}