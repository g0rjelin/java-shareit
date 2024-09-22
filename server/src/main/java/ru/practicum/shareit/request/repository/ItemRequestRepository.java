package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    String ITEMREQUEST_NOT_FOUND_MSG = "Запрос с id = %d не найден";

    Page<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long userId, Pageable pageable);

    Page<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long userId, Pageable pageable);

    Optional<ItemRequest> findById(Long id);

    default ItemRequest getItemRequestBy(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ITEMREQUEST_NOT_FOUND_MSG, id)));
    }
}
