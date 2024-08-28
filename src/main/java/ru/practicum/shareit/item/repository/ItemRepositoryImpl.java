package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    final Map<Long, Item> items;
    final Map<Long, Map<Long,Item>> itemsByOwner;

    @Override
    public Collection<Item> findAllItemsByOwnerId(Long id) {
        return itemsByOwner.get(id).values();
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> searchItems(String text) {
        return items.values().stream()
                .filter(i -> (i.getDescription().toLowerCase().contains(text.toLowerCase()) || i.getName().toLowerCase().contains(text.toLowerCase()))
                        && i.getAvailable())
                .collect(Collectors.toSet());
    }

    @Override
    public Item create(Item newItem) {
        newItem.setId(getNextId());
        items.put(newItem.getId(), newItem);
        itemsByOwner.computeIfAbsent(newItem.getOwner().getId(), k -> new HashMap<>()).put(newItem.getId(), newItem);
        log.info("Вещь {} добавлена", newItem);
        return newItem;
    }

    @Override
    public Item update(Item updItem) {
        items.put(updItem.getId(), updItem);
        itemsByOwner.get(updItem.getOwner().getId()).put(updItem.getId(), updItem);
        log.info("Вещь {} обновлена", updItem);
        return updItem;
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
