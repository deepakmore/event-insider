package com.myproject.insider.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.myproject.insider.advice.GlobalExceptionHandler;
import com.myproject.insider.service.EventSeatCategoryService;
import com.myproject.insider.service.EventService;
import com.myproject.insider.service.EventShowService;
import com.myproject.insider.advice.ApiResponseBodyAdvice;

@WebMvcTest(controllers = EventController.class)
@Import({ GlobalExceptionHandler.class, ApiResponseBodyAdvice.class })
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    EventService eventService;

    @MockitoBean
    EventShowService eventShowService;

    @MockitoBean
    EventSeatCategoryService eventSeatCategoryService;

    @Test
    void create_withEmptyBody_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/events").contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_ERROR_400"))
                .andExpect(jsonPath("$.meta.status").value("error"))
                .andExpect(jsonPath("$.data.errors.name").exists())
                .andExpect(jsonPath("$.data.errors.eventType").exists())
                .andExpect(jsonPath("$.data.errors.pricingType").exists())
                .andExpect(jsonPath("$.data.curl").exists());
    }
}