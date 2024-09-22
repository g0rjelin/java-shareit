package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.common.Marker;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static ru.practicum.shareit.common.CommonConstants.X_SHARER_USER_ID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllItemsByOwnerId(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all items of ownerId={}, from={}, size={}", ownerId, from, size);
        return itemClient.getAllItemsByOwnerId(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@PathVariable @Min(1) Long id) {
        log.info("Get item with id={}", id);
        return itemClient.getItemById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items with text={}, from={}, size={}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) Long userId, @Validated({Marker.OnCreate.class}) @RequestBody ItemRequestDto newItemRequestDto) {
        log.info("Creating item {}, userId={}", newItemRequestDto, userId);
        return itemClient.createItem(userId, newItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(X_SHARER_USER_ID) Long userId, @Validated({Marker.OnUpdate.class}) @RequestBody ItemRequestDto updItemRequestDto, @PathVariable @Min(1) Long itemId) {
        log.info("Updating itemId={} with {}, userId={}", itemId, updItemRequestDto, userId);
        return itemClient.updateItem(userId, itemId, updItemRequestDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable @Min(1) Long itemId, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Adding comment {}, itemId={}, userId={}", commentRequestDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentRequestDto);
    }

}
