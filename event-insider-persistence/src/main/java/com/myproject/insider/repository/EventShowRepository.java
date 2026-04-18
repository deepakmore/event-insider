package com.myproject.insider.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.insider.entity.EventShow;

public interface EventShowRepository extends JpaRepository<EventShow, Long> {

    List<EventShow> findByEvent_Id(Long eventId);

    List<EventShow> findByVenue_Id(Long venueId);

    Optional<EventShow> findByIdAndDisabledFalse(Long id);

    Page<EventShow> findAllByDisabledFalse(Pageable pageable);
}