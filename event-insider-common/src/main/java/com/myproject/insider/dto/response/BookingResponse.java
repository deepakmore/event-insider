package com.myproject.insider.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.myproject.insider.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long userId;
    private Long showId;
    private BookingStatus status;
    private Instant createdAt;
    private Instant holdExpiresAt;
    private BigDecimal totalAmount;
    private List<BookingLineResponse> lines;
    private Instant cancelledAt;
    private String cancelReason;
    private Long cancelledByUserId;
}