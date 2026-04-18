package com.myproject.insider.kafka;

import com.myproject.insider.annotations.SendKafkaEvent;
import com.myproject.insider.entity.Booking;
import org.springframework.stereotype.Service;

@Service
public class BookingCompletionKafkaTrigger {

    @SendKafkaEvent(topic = "${app.kafka.topics.booking:booking.events}")
    public Booking publishComplete(Booking booking) {
        return booking;
    };
}
