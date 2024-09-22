package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class CommentShortDto {
    @NotBlank
    @Size(min = 1, max = 1000)
    final String text;

    @JsonCreator
    public CommentShortDto(@JsonProperty("text") String text) {
        this.text = text;
    }
}
