package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

import static ru.practicum.shareit.common.CommonConstants.X_SHARER_USER_ID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> findAllItemsByOwnerId(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemService.findAllItemsByOwnerId(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findItemById(@PathVariable Long id) {
        return itemService.findItemById(id);
    }

    @GetMapping("/search")
    public Collection<ItemShortDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemShortDto create(@RequestHeader(X_SHARER_USER_ID) Long userId, @RequestBody ItemShortDto newItem) {
        return itemService.create(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ItemShortDto update(@RequestHeader(X_SHARER_USER_ID) Long userId, @RequestBody ItemShortDto updItem, @PathVariable Long itemId) {
        return itemService.update(userId, itemId, updItem);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable Long itemId, @RequestBody CommentShortDto commentShortDto) {
        return itemService.addComment(userId, itemId, commentShortDto);
    }

}
