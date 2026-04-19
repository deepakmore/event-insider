package com.myproject.insider.kafka;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.myproject.insider.annotations.SendKafkaEvent;

@Service
public class SlackAlertKafkaTrigger {

    @Value("${app.alert.contact-team:Alpha}")
    private String contactTeam;

    @Value("${app.alert.escalate:Deepak More}")
    private String escalate;

    @SendKafkaEvent(topic = "${app.kafka.topics.slack:slack.alerts}")
    public SlackAlertEvent publish(String slackId, String category, String errorMessage) {
        return new SlackAlertEvent(
                UUID.randomUUID().toString(),
                slackId,
                category,
                errorMessage,
                contactTeam,
                escalate,
                Instant.now().toString());
    }

    public record SlackAlertEvent(
            String id,
            String slackId,
            String category,
            String errorMessage,
            String contactTeam,
            String escalate,
            String createdAt) {
    }
}
