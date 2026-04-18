package com.myproject.insider.kafka;

import java.util.LinkedHashMap;
import java.util.Map;

import com.myproject.insider.annotations.SendKafkaEvent;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.insider.entity.Booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@ConditionalOnBean(KafkaTemplate.class)
@RequiredArgsConstructor
@Slf4j
public class SendKafkaEventAspect {

    private static final ExpressionParser SPEL = new SpelExpressionParser();

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    @AfterReturning(pointcut = "@annotation(sendKafkaEvent) && within(com.myproject.insider.kafka..*)", returning = "result")
    public void publishAfterSuccess(SendKafkaEvent sendKafkaEvent, Object result) {
        if (result == null) {
            return;
        }
        String topic = environment.resolveRequiredPlaceholders(sendKafkaEvent.topic());
        String key = evaluateString(sendKafkaEvent.keyExpression(), result, "key");
        Object payload = resolvePayload(sendKafkaEvent, result);
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize Kafka payload for topic {}", topic, ex);
            return;
        }
        String finalTopic = topic;
        String finalKey = key;
        String finalJson = json;
        Runnable send = () -> {
            try {
                kafkaTemplate.send(finalTopic, finalKey, finalJson);
            } catch (Exception ex) {
                log.error("Failed to send Kafka message topic={} key={}", finalTopic, finalKey, ex);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send.run();
                }
            });
        } else {
            send.run();
        }
    }

    private Object resolvePayload(SendKafkaEvent sendKafkaEvent, Object result) {
        if (StringUtils.hasText(sendKafkaEvent.payloadExpression())) {
            return evaluateObject(sendKafkaEvent.payloadExpression(), result, "payload");
        }
        if (result instanceof Booking b) {
            return bookingPayload(b);
        }
        return result;
    }

    private static Map<String, Object> bookingPayload(Booking b) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", b.getId());
        map.put("userId", b.getUser().getId());
        map.put("showId", b.getEventShow().getId());
        map.put("status", b.getStatus() != null ? b.getStatus().name() : null);
        map.put("createdAt", b.getCreatedAt() != null ? b.getCreatedAt().toString() : null);
        map.put("holdExpiresAt", b.getHoldExpiresAt() != null ? b.getHoldExpiresAt().toString() : null);
        return map;
    }

    private static String evaluateString(String expression, Object result, String role) {
        Object value = evaluateObject(expression, result, role);
        return value != null ? value.toString() : "";
    }

    private static Object evaluateObject(String expression, Object result, String role) {
        try {
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            ctx.setVariable("result", result);
            return SPEL.parseExpression(expression).getValue(ctx);
        } catch (Exception ex) {
            throw new IllegalStateException("Invalid SpEL for " + role + ": " + expression, ex);
        }
    }
}