package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class BookingShortDto {
    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @FutureOrPresent
    @NotNull
    final LocalDateTime start;
    @Future
    @NotNull
    final LocalDateTime end;
    final Long itemId;
}
