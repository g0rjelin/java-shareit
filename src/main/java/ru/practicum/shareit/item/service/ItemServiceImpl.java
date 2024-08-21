package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.ServiceUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserService userService;

    static final String ITEM_NOT_FOUND_MSG = "Вещь с id = %d не найдена";
    static final String OWNER_NOT_FOUND_MSG = "Вещь с id = %d обновляется пользователем с id = %d, не являющимся владельцем";

    @Override
    public Collection<ItemDto> findAllItemsByOwnerId(Long userId) {
        userService.getUserById(userId);
        return itemRepository.findAllItemsByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        return ItemMapper.toItemDto(getItemById(itemId));
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        return text.isEmpty() ? Collections.emptySet() : itemRepository.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    public ItemDto create(Long userId, ItemDto newItemDto) {
        User owner = userService.getUserById(userId);
        Item newItem = ItemMapper.toItem(newItemDto);
        newItem.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.create(newItem));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto updItem) {
        User owner = userService.getUserById(userId);
        Item oldItem = getItemById(itemId);
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format(OWNER_NOT_FOUND_MSG, itemId, userId));
        }
        oldItem.setName(ServiceUtils.getDefaultIfNull(updItem.getName(), oldItem.getName()));
        oldItem.setDescription(ServiceUtils.getDefaultIfNull(updItem.getDescription(), oldItem.getDescription()));
        oldItem.setAvailable(ServiceUtils.getDefaultIfNull(updItem.getAvailable(), oldItem.getAvailable()));
        return ItemMapper.toItemDto(itemRepository.update(oldItem));
    }
}
