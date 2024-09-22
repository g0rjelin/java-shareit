package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
    String ITEM_NOT_FOUND_MSG = "Вещь с id = %d не найдена";

    List<Item> findAllByOwnerId(Long ownerId);

    boolean existsItemsByOwnerId(Long ownerId);

    default Item getItemById(Long itemId) {
        return findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));
    }
}
