package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class BookingShortDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    final Long id;
    final LocalDateTime start;
    final LocalDateTime end;
    final Long itemId;
}
