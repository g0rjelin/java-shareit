package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> findAllItemsByOwnerId(Long ownerId);

    Item getItemById(Long itemId);

    ItemDto findItemById(Long itemId);

    Collection<ItemShortDto> searchItems(String text);

    ItemShortDto create(Long userId, ItemShortDto newItemShortDto);

    ItemShortDto update(Long userId, Long itemId, ItemShortDto updItemShortDto);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

}
