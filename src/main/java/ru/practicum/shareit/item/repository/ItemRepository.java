package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Collection<Item> findAllItemsByOwnerId(Long id);

    Optional<Item> findItemById(Long id);

    Collection<Item> searchItems(String text);

    Item create(Item newItem);

    Item update(Item updItem);
}
