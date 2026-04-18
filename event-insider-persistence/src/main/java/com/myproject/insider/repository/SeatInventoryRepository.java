package com.myproject.insider.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myproject.insider.entity.SeatInventory;
import com.myproject.insider.enums.SeatInventoryStatus;

public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {

    List<SeatInventory> findByEventShow_Id(Long eventShowId);

    Optional<SeatInventory> findByEventShow_IdAndSeat_Id(Long eventShowId, Long seatId);

    boolean existsBySeat_Id(Long seatId);

    List<SeatInventory> findByBookingId(Long bookingId);

    @EntityGraph(attributePaths = "seat")
    @Query("select s from SeatInventory s where s.eventShow.id = :showId and s.status = :status")
    List<SeatInventory> findByShowAndStatusWithSeat(@Param("showId") Long showId,
                                                    @Param("status") SeatInventoryStatus status);

    @EntityGraph(attributePaths = "seat")
    @Query("select s from SeatInventory s where s.eventShow.id = :showId")
    List<SeatInventory> findByShowIdWithSeat(@Param("showId") Long showId);

    @Modifying(clearAutomatically = true)
    @Query("update SeatInventory s set s.status = :held, s.bookingId = :bookingId, s.updatedAt = :now, s.version = s.version + 1 "
            + "where s.id in :ids and s.status = :available and s.eventShow.id = :showId")
    int holdSeatsIfAvailable(@Param("ids") Collection<Long> ids, @Param("available") SeatInventoryStatus available,
                             @Param("held") SeatInventoryStatus held, @Param("bookingId") Long bookingId, @Param("showId") Long showId,
                             @Param("now") Instant now);

    @Modifying(clearAutomatically = true)
    @Query("update SeatInventory s set s.status = :booked, s.updatedAt = :now, s.version = s.version + 1 "
            + "where s.bookingId = :bookingId and s.status = :held")
    int markSeatsBooked(@Param("bookingId") Long bookingId, @Param("held") SeatInventoryStatus held,
                        @Param("booked") SeatInventoryStatus booked, @Param("now") Instant now);

    @Modifying(clearAutomatically = true)
    @Query("update SeatInventory s set s.status = :available, s.bookingId = null, s.updatedAt = :now, s.version = s.version + 1 "
            + "where s.bookingId = :bookingId and s.status = :held")
    int releaseHeldSeats(@Param("bookingId") Long bookingId, @Param("held") SeatInventoryStatus held,
                         @Param("available") SeatInventoryStatus available, @Param("now") Instant now);
}