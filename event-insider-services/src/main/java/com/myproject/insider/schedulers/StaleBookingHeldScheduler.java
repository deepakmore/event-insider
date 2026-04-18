package com.myproject.insider.schedulers;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.myproject.insider.enums.BookingStatus;
import com.myproject.insider.repository.BookingRepository;
import com.myproject.insider.service.BookingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StaleBookingHeldScheduler {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Scheduled(fixedDelayString = "${app.booking.stale-hold-scan-interval-ms:60000}")
    public void sweepExpiredHolds() {
        System.out.println("[StaleBookingHeldScheduler] : Inside cron");
        bookingRepository.findByStatusAndHoldExpiresAtBefore(BookingStatus.IN_PROGRESS, Instant.now())
                .forEach(b -> {
                    try {
                        bookingService.cancelExpiredHold(b.getId());
                    } catch (Exception ex) {
                        log.warn("Failed to cancel expired booking hold id={}", b.getId(), ex);
                    }
                });
    }
}