package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.ServiceUtils;

import java.util.Collection;
import java.util.Collections;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final CommentRepository commentRepository;

    static final String ITEM_NOT_FOUND_MSG = "Вещь с id = %d не найдена";
    static final String OWNER_NOT_FOUND_MSG = "Вещь с id = %d обновляется пользователем с id = %d, не являющимся владельцем";
    static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    static final String COMMENT_NOT_ALLOWED_MSG = "Оставлять комментарий можно только бравшему вещь в аренду";
    private final BookingRepository bookingRepository;

    @Override
    public Collection<ItemDto> findAllItemsByOwnerId(Long ownerId) {
        getUserById(ownerId);
        Collection<Item> items = itemRepository.findAllByOwnerId(ownerId);
        Collection<Comment> comments = commentRepository.findByItem_Owner_Id(ownerId);
        Collection<Booking> bookings = bookingRepository.findByItem_Owner_Id(ownerId);
        return ItemMapper.toItemOwnerDto(items, comments, bookings);
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        Item item = getItemById(itemId);
        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);
        return ItemMapper.toItemDto(item, comments);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_MSG, itemId)));
    }

    @Override
    public Collection<ItemShortDto> searchItems(String text) {
        BooleanExpression containsNameOrDescription = QItem.item.name.containsIgnoreCase(text).or(QItem.item.description.containsIgnoreCase(text));
        BooleanExpression isAvailable = QItem.item.available.isTrue();
        return text.isEmpty() ? Collections.emptySet() : ItemMapper.toItemDto(itemRepository.findAll(isAvailable.and(containsNameOrDescription)));
    }

    @Override
    public ItemShortDto create(Long userId, ItemShortDto newItemShortDto) {
        Item newItem = ItemMapper.toItem(newItemShortDto, getUserById(userId));
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemShortDto update(Long userId, Long itemId, ItemShortDto updItem) {
        User owner = getUserById(userId);
        Item oldItem = getItemById(itemId);
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format(OWNER_NOT_FOUND_MSG, itemId, userId));
        }
        oldItem.setName(ServiceUtils.getDefaultIfNull(updItem.getName(), oldItem.getName()));
        oldItem.setDescription(ServiceUtils.getDefaultIfNull(updItem.getDescription(), oldItem.getDescription()));
        oldItem.setAvailable(ServiceUtils.getDefaultIfNull(updItem.getAvailable(), oldItem.getAvailable()));
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto) {
        User author = getUserById(authorId);
        Item item = getItemById(itemId);
        if (!bookingRepository.existValidBooking(authorId, itemId)) {
            throw new BadRequestException(COMMENT_NOT_ALLOWED_MSG);
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, author, item)));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
    }

}
