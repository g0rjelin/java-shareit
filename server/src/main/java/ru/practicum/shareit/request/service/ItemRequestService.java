package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestShortDto itemRequestShortDto);

    Collection<ItemRequestFullDto> getItemRequestsByRequestorId(Long requestorId, Integer from, Integer size);

    Collection<ItemRequestDto> getAllOtherItemRequestsByUserId(Long userId, Integer from, Integer size);

    ItemRequestFullDto getItemRequestById(Long requestId);
}
