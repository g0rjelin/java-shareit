package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static ru.practicum.shareit.common.CommonConstants.X_SHARER_USER_ID;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                 @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return itemRequestClient.addItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(X_SHARER_USER_ID) long requestorId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get requests with requestorId={}, from={}, size={}", requestorId, from, size);
        return itemRequestClient.getRequests(requestorId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all requests from other users with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId) {
        log.info("Get request with requestId={}", requestId);
        return itemRequestClient.getRequestById(requestId);
    }
}
