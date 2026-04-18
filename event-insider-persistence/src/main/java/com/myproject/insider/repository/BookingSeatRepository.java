package com.myproject.insider.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.insider.entity.BookingSeat;
import com.myproject.insider.entity.BookingSeatId;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, BookingSeatId> {

    List<BookingSeat> findByBookingId(Long bookingId);
    List<BookingSeat> findBySeatInventoryId(Long seatInventoryId);
}