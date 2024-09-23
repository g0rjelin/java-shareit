package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    final ItemRequestRepository itemRequestRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    static final String REQUEST_NOT_FOUND_MSG = "Запрос с id = %d не найден";

    @Override
    public ItemRequestDto create(Long userId, ItemRequestShortDto itemRequestShortDto) {
        User requestor = userRepository.getUserById(userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestShortDto, requestor)));
    }

    @Override
    public Collection<ItemRequestFullDto> getItemRequestsByRequestorId(Long requestorId, Integer from, Integer size) {
        User requestor = userRepository.getUserById(requestorId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return ItemRequestMapper.toItemRequestFullDto(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId, page).getContent(), itemRepository.findAllByRequest_RequestorId(requestorId));
    }

    @Override
    public Collection<ItemRequestDto> getAllOtherItemRequestsByUserId(Long userId, Integer from, Integer size) {
        User user = userRepository.getUserById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, page).getContent());
    }

    @Override
    public ItemRequestFullDto getItemRequestById(Long requestId) {
        return ItemRequestMapper.toItemRequestFullDto(getRequestById(requestId), itemRepository.findAllByRequestId(requestId));
    }

    private ItemRequest getRequestById(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format(REQUEST_NOT_FOUND_MSG, requestId)));
    }
}
