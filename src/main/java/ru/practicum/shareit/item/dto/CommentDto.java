package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class CommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    final Long id;
    @NotBlank
    final String text;
    final String authorName;
    final LocalDateTime created;
}
