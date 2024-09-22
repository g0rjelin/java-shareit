package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.common.Marker;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ItemShortDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    final Long id;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(min = 1, max = 100, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    final String name;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(min = 1, max = 1000, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    final String description;

    @NotNull(groups = {Marker.OnCreate.class})
    final Boolean available;

    final Long request;
}
