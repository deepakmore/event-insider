package com.myproject.insider.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.insider.entity.EventSeatCategory;

public interface EventSeatCategoryRepository extends JpaRepository<EventSeatCategory, Long> {

    List<EventSeatCategory> findByEvent_Id(Long eventId);

    Optional<EventSeatCategory> findByIdAndDisabledFalse(Long id);
    Page<EventSeatCategory> findAllByDisabledFalse(Pageable pageable);
}