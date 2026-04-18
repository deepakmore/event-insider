package com.myproject.insider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;
import com.myproject.insider.enums.SeatInventoryStatus;
import com.myproject.insider.repository.SeatInventoryRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BassiMumbaiConcurrentBookingIntegrationTest {

    private record UserBooking(long userId, long bookingId) {
    }

    private static final long BASSI_MUMBAI_SHOW_ID = 3L;
    private static final long BASSI_BRONZE_CATEGORY_ID = 7L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SeatInventoryRepository seatInventoryRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void tenUsersRaceForNineSeats_onBassiMumbaiShow_nineBookingsOneConflict() throws Exception {
        List<Long> userIds = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            String email = "bassi.concurrent." + i + "." + UUID.randomUUID() + "@example.com";
            String mobile = "+9198" + String.format("%08d", Math.abs(UUID.randomUUID().getMostSignificantBits() % 100_000_000L));
            String body = """
					{"name":"Bassi Concurrent User %d","email":"%s","mobileNumber":"%s","password":"CorrectHorse1"}"""
                    .formatted(i, email, mobile);
            MvcResult reg = mockMvc.perform(post("/api/v1/users").contentType(APPLICATION_JSON).content(body))
                    .andReturn();
            assertEquals(201, reg.getResponse().getStatus(), reg.getResponse().getContentAsString());
            long id = ((Number) JsonPath.read(reg.getResponse().getContentAsString(), "$.data.id")).longValue();
            userIds.add(id);
        }

        CopyOnWriteArrayList<UserBooking> heldBookings = new CopyOnWriteArrayList<>();
        try {
            jdbcTemplate.update(
                    "update seat_inventory set status = 'AVAILABLE', booking_id = null where show_id = ?",
                    BASSI_MUMBAI_SHOW_ID);

            List<Long> availableSeatIds = seatInventoryRepository
                    .findByShowAndStatusWithSeat(BASSI_MUMBAI_SHOW_ID, SeatInventoryStatus.AVAILABLE)
                    .stream()
                    .map(si -> si.getId())
                    .sorted()
                    .limit(9)
                    .toList();
            assertEquals(9, availableSeatIds.size(), "test requires nine available seats in Mumbai bronze inventory");

            CountDownLatch allArmed = new CountDownLatch(10);
            CountDownLatch fire = new CountDownLatch(1);
            AtomicInteger created = new AtomicInteger();
            AtomicInteger conflict = new AtomicInteger();
            AtomicInteger other = new AtomicInteger();

            ExecutorService pool = Executors.newFixedThreadPool(10);
            try {
                for (int i = 0; i < 10; i++) {
                    final int index = i;
                    final long userId = userIds.get(i);
                    final long seatInventoryId = index < 9 ? availableSeatIds.get(index) : availableSeatIds.get(0);
                    pool.submit(() -> {
                        try {
                            allArmed.countDown();
                            assertTrue(fire.await(1, TimeUnit.MINUTES));
                            String bookingBody = """
									{"userId":%d,"showId":%d,"eventSeatCategoryId":%d,"seatInventoryIds":[%d]}"""
                                    .formatted(userId, BASSI_MUMBAI_SHOW_ID, BASSI_BRONZE_CATEGORY_ID, seatInventoryId);
                            MvcResult r = mockMvc.perform(post("/api/v1/bookings").contentType(APPLICATION_JSON).content(bookingBody))
                                    .andReturn();
                            int httpStatus = r.getResponse().getStatus();
                            if (httpStatus == 201) {
                                long bookingId = ((Number) JsonPath.read(r.getResponse().getContentAsString(), "$.data.id")).longValue();
                                heldBookings.add(new UserBooking(userId, bookingId));
                                created.incrementAndGet();
                            } else if (httpStatus == 409) {
                                conflict.incrementAndGet();
                            } else {
                                other.incrementAndGet();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            other.incrementAndGet();
                        } catch (Exception e) {
                            other.incrementAndGet();
                        }
                    });
                }

                assertTrue(allArmed.await(1, TimeUnit.MINUTES), "all booking threads should reach the gate");
                fire.countDown();
                pool.shutdown();
                assertTrue(pool.awaitTermination(2, TimeUnit.MINUTES), "all booking attempts should finish");
            } finally {
                pool.shutdownNow();
            }

            assertEquals(0, other.get(), "unexpected HTTP statuses or failures: count=" + other.get());
            assertEquals(9, created.get(), "nine distinct seats should be held successfully");
            assertEquals(1, conflict.get(), "one contender for the contested seat should receive conflict");
        } finally {
            for (UserBooking ub : heldBookings) {
                try {
                    mockMvc.perform(post("/api/v1/bookings/{id}/cancel", ub.bookingId()).param("userId", String.valueOf(ub.userId())))
                            .andExpect(status().isNoContent());
                } catch (Exception ignored) {}
            }
        }
    }

}