package com.myproject.insider.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seat", uniqueConstraints = @UniqueConstraint(columnNames = { "show_id", "seat_number" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private EventShow eventShow;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Column(name = "row_label", length = 5)
    private String rowLabel;

    @Column(name = "column_number")
    private Integer columnNumber;

    @Column(name = "x_coordinate")
    private Float xCoordinate;

    @Column(name = "y_coordinate")
    private Float yCoordinate;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "seat")
    @Builder.Default
    private List<SeatInventory> seatInventories = new ArrayList<>();
}