package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestDto> toItemRequestDto(Collection<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    public static ItemRequestFullDto toItemRequestFullDto(ItemRequest itemRequest, Collection<Item> items) {
        return ItemRequestFullDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemMapper.toItemForItemRequestDto(items))
                .build();
    }

    public static List<ItemRequestFullDto> toItemRequestFullDto(Collection<ItemRequest> itemRequests, Collection<Item> items) {
        List<ItemRequestFullDto> itemRequestFullDtos = new ArrayList<>();
        Map<Long, List<Item>> itemsMap = Objects.isNull(items) ? new HashMap<>() : items.stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestFullDtos.add(ItemRequestFullDto.builder()
                    .id(itemRequest.getId())
                    .description(itemRequest.getDescription())
                    .created(itemRequest.getCreated())
                    .items(ItemMapper.toItemForItemRequestDto(itemsMap.get(itemRequest.getId())))
                    .build());
        }
        return itemRequestFullDtos;
    }


    public static ItemRequest toItemRequest(ItemRequestShortDto itemRequestShortDto, User requestor) {
        return ItemRequest.builder()
                .description(itemRequestShortDto.getDescription())
                .requestor(requestor)
                .build();
    }
}
