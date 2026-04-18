package com.myproject.insider.entity;

import java.io.Serializable;
import java.util.Objects;

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
public class BookingSeatId implements Serializable {

    private Long bookingId;
    private Long seatInventoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookingSeatId that = (BookingSeatId) o;
        return Objects.equals(bookingId, that.bookingId) && Objects.equals(seatInventoryId, that.seatInventoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, seatInventoryId);
    }
}