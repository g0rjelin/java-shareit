package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

import static ru.practicum.shareit.common.CommonConstants.X_SHARER_USER_ID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                 @RequestBody ItemRequestShortDto requestDto) {
        return itemRequestService.create(userId, requestDto);
    }

    @GetMapping
    public Collection<ItemRequestFullDto> getRequests(@RequestHeader(X_SHARER_USER_ID) long requestorId,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getItemRequestsByRequestorId(requestorId, from, size);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto>  getAllRequests(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllOtherItemRequestsByUserId(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ItemRequestFullDto getRequest(@PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }
}
