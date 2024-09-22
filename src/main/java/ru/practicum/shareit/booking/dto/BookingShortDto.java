package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.validation.StartBeforeEndDateValid;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@StartBeforeEndDateValid
public class BookingShortDto {
    @FutureOrPresent
    @NotNull
    final LocalDateTime start;
    @Future
    @NotNull
    final LocalDateTime end;
    @NotNull
    final Long itemId;
}
