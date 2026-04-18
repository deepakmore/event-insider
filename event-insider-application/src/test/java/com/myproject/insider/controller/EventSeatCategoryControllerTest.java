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

import com.myproject.insider.advice.ApiResponseBodyAdvice;
import com.myproject.insider.advice.GlobalExceptionHandler;
import com.myproject.insider.service.EventSeatCategoryService;

@WebMvcTest(controllers = EventSeatCategoryController.class)
@Import({ GlobalExceptionHandler.class, ApiResponseBodyAdvice.class })
class EventSeatCategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    EventSeatCategoryService eventSeatCategoryService;

    @Test
    void create_withEmptyBody_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/event-seat-categories").contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.code").value("EVENT_INSIDER_SERVICE_ERROR_400"))
                .andExpect(jsonPath("$.data.errors.eventId").exists())
                .andExpect(jsonPath("$.data.errors.name").exists())
                .andExpect(jsonPath("$.data.curl").exists());
    }
}