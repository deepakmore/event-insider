package com.myproject.insider.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.myproject.insider.enums.SeatInventoryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seat_inventory", uniqueConstraints = @UniqueConstraint(columnNames = { "show_id", "seat_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private EventShow eventShow;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatInventoryStatus status;

    @Column(name = "booking_id")
    private Long bookingId;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "seatInventory")
    @Builder.Default
    private List<BookingSeat> bookingSeats = new ArrayList<>();
}