package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemForItemRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    final Long id;
    final String name;
    final Long ownerId;
}
