package com.myproject.insider.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.insider.entity.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}