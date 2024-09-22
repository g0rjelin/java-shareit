package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.common.Marker;
import ru.practicum.shareit.validation.NullOrNotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class UserRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    final Long id;

    @NotBlank(groups = {Marker.OnCreate.class})
    @NullOrNotBlank(groups = {Marker.OnUpdate.class})
    @Size(min = 1, max = 100, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    final String name;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(min = 1, max = 320, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    final String email;
}