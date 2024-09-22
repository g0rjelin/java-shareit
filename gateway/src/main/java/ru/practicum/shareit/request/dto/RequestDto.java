package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class RequestDto {
    @NotBlank
    @Size(min = 1, max = 1000)
    final String description;
}
