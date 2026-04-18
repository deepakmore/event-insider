package com.myproject.insider.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.insider.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByIdAndDisabledFalse(Long id);

    Page<Event> findAllByDisabledFalse(Pageable pageable);
}