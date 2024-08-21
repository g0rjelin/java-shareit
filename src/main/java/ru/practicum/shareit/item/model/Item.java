package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class Item {
    private Long id;
    @NotNull
    private String name;
    private String description;
    private Boolean available;
    @NotNull
    private User owner;
    private ItemRequest request;
}
