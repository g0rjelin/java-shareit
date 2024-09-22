package ru.practicum.shareit.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bookings", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "start_date")
    LocalDateTime start;

    @Column(name = "end_date")
    LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    User booker;

    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
