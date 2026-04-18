package com.myproject.insider.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SendKafkaEvent {
    String topic();
    String keyExpression() default "#result != null ? #result.id.toString() : 'unknown'";
    String payloadExpression() default "";
}
