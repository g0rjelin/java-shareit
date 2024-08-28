package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> findAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllItemsByOwnerId(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findItemById(@PathVariable @Min(1) Long id) {
        return itemService.findItemById(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(defaultValue = "") String text) {
        return itemService.searchItems(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto newItem) {
        return itemService.create(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto updItem, @PathVariable @Min(1) Long itemId) {
        return itemService.update(userId, itemId, updItem);
    }


}
