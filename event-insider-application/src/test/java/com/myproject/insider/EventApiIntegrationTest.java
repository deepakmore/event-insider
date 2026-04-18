package com.myproject.insider;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

/**
 * End-to-end API tests against PostgreSQL database {@code event_insider_test} (see
 * {@code application-test.yaml} and Flyway migrations including seed data from
 * {@code V20260416152011__insert_sample_records.sql}).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EventApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getSampleUser_byId_returnsFlywaySeedData() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_200"))
                .andExpect(jsonPath("$.meta.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Deepak"))
                .andExpect(jsonPath("$.data.email").value("deepakmore@outlook.com"))
                .andExpect(jsonPath("$.data.mobileNumber").value("+919420390095"));
    }

    @Test
    void createUser_returns201AndSuccess201Code() throws Exception {
        String email = "integration.user." + UUID.randomUUID() + "@example.com";
        String mobile = "+9199" + String.format("%07d", Math.abs(UUID.randomUUID().getMostSignificantBits() % 10_000_000L));
        String body = """
				{"name":"Integration User","email":"%s","mobileNumber":"%s","password":"CorrectHorse1"}"""
                .formatted(email, mobile);
        mockMvc.perform(post("/api/v1/users").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_201"))
                .andExpect(jsonPath("$.meta.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Integration User"))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.mobileNumber").value(mobile));
    }

    @Test
    void getSampleEvent_byId_returnsFlywaySeedData() throws Exception {
        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_200"))
                .andExpect(jsonPath("$.meta.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Arijit Singh Live Concert"))
                .andExpect(jsonPath("$.data.description").value("Live concert by Arijit Singh"))
                .andExpect(jsonPath("$.data.eventType").value("CONCERT"))
                .andExpect(jsonPath("$.data.pricingType").value("CATEGORY"));
    }

    @Test
    void createEvent_returns201AndSuccess201Code() throws Exception {
        String body = """
				{"name":"Test Event","description":null,"eventType":"CONCERT","pricingType":"CATEGORY"}""";
        mockMvc.perform(post("/api/v1/events").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_201"))
                .andExpect(jsonPath("$.meta.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Test Event"));
    }

    @Test
    void getById_unknown_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/events/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_ERROR_404"))
                .andExpect(jsonPath("$.meta.status").value("error"))
                .andExpect(jsonPath("$.meta.message").exists())
                .andExpect(jsonPath("$.data.curl").exists());
    }

    @Test
    void getSampleEventShow_byId_returnsFlywaySeedData() throws Exception {
        mockMvc.perform(get("/api/v1/event-shows/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_200"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.eventId").value(1))
                .andExpect(jsonPath("$.data.venueId").value(1))
                .andExpect(jsonPath("$.data.startTime").exists())
                .andExpect(jsonPath("$.data.endTime").exists());
    }

    @Test
    void getEventShow_unknown_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/event-shows/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_ERROR_404"));
    }

    @Test
    void createEventShow_returns201() throws Exception {
        String body = """
				{"eventId":1,"venueId":1,"startTime":"2027-01-01T18:00:00Z","endTime":"2027-01-01T22:00:00Z"}""";
        mockMvc.perform(post("/api/v1/event-shows").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_201"))
                .andExpect(jsonPath("$.data.eventId").value(1))
                .andExpect(jsonPath("$.data.venueId").value(1));
    }

    @Test
    void getSampleEventSeatCategory_byId_returnsFlywaySeedData() throws Exception {
        mockMvc.perform(get("/api/v1/event-seat-categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_200"))
                .andExpect(jsonPath("$.data.eventId").value(1))
                .andExpect(jsonPath("$.data.name").value("BRONZE"));
    }

    @Test
    void getSampleShowSeatPricing_byId_returnsFlywaySeedData() throws Exception {
        mockMvc.perform(get("/api/v1/show-seat-pricings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_200"))
                .andExpect(jsonPath("$.data.showId").value(1))
                .andExpect(jsonPath("$.data.eventCategoryId").value(1))
                .andExpect(jsonPath("$.data.basePrice").value(500));
    }

    @Test
    void getSampleSeat_byId_returnsFlywaySeedData() throws Exception {
        mockMvc.perform(get("/api/v1/seats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_200"))
                .andExpect(jsonPath("$.data.showId").value(1))
                .andExpect(jsonPath("$.data.seatNumber").value("A1"));
    }

    @Test
    void getSampleSeatInventory_byId_returnsFlywaySeedData() throws Exception {
        mockMvc.perform(get("/api/v1/seat-inventories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_SUCCESS_200"))
                .andExpect(jsonPath("$.data.showId").value(1))
                .andExpect(jsonPath("$.data.seatId").value(1))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"));
    }

    @Test
    void nestedCatalog_eventShows_seatCategories_showPricing_seatInventories() throws Exception {
        mockMvc.perform(get("/api/v1/events/1/shows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].id", hasItem(1)));
        mockMvc.perform(get("/api/v1/events/1/seat-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("BRONZE"));
        mockMvc.perform(get("/api/v1/event-shows/1/seat-categories/1/seat-pricing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.basePrice").value(500));
        mockMvc.perform(get("/api/v1/seat-inventories/by-show/1").param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(9))
                .andExpect(jsonPath("$.data[*].id", hasItem(9)));
    }

    @Test
    void booking_holdThenWebhook_marksSeatsBooked_idempotentWebhook() throws Exception {
        String externalEventId = "evt-integration-" + UUID.randomUUID();
        String createBody = """
				{"userId":1,"showId":1,"eventSeatCategoryId":1,"seatInventoryIds":[7,8,9]}""";
        MvcResult created = mockMvc.perform(post("/api/v1/bookings").contentType(APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.lines.length()").value(3))
                .andReturn();
        long bookingId = ((Number) JsonPath.read(created.getResponse().getContentAsString(), "$.data.id")).longValue();

        mockMvc.perform(get("/api/v1/bookings/" + bookingId).param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAmount").value(1500));

        String webhookBody = """
				{"provider":"SANDBOX","externalEventId":"%s","bookingId":%d,"paymentStatus":"SUCCEEDED"}"""
                .formatted(externalEventId, bookingId);
        mockMvc.perform(post("/api/v1/payments/webhook")
                        .header("X-Payment-Webhook-Secret", "test-webhook-secret")
                        .contentType(APPLICATION_JSON)
                        .content(webhookBody))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .header("X-Payment-Webhook-Secret", "test-webhook-secret")
                        .contentType(APPLICATION_JSON)
                        .content(webhookBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/bookings/" + bookingId).param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETE"));

        mockMvc.perform(get("/api/v1/seat-inventories/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("BOOKED"));
    }
}