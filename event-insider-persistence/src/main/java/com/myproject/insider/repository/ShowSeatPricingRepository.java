package com.myproject.insider.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.insider.entity.ShowSeatPricing;

public interface ShowSeatPricingRepository extends JpaRepository<ShowSeatPricing, Long> {

    List<ShowSeatPricing> findByEventShow_Id(Long eventShowId);

    Optional<ShowSeatPricing> findByEventShow_IdAndEventCategory_Id(Long eventShowId, Long eventCategoryId);

    boolean existsByEventCategory_Id(Long eventCategoryId);
}