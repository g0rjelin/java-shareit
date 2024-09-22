package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ItemRequestShortDto {
    final String description;

    @JsonCreator
    public ItemRequestShortDto(@JsonProperty("description") String description) {
        this.description = description;
    }
}
