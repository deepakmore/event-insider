package com.myproject.insider.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.insider.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByEventShow_Id(Long eventShowId);
}