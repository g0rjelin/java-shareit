package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> findAllItemsByOwnerId(Long userId);

    Item getItemById(Long itemId);

    ItemDto findItemById(Long itemId);

    Collection<ItemDto> searchItems(String text);

    ItemDto create(Long userId, ItemDto newItemDto);

    ItemDto update(Long userId, Long itemId, ItemDto updItemDto);
}
