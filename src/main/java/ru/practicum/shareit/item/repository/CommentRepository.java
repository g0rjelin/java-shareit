package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findAllByItemId(Long itemId);

    @Query("select comment " +
            "from Comment as comment " +
            "where comment.item.owner.id = ?1")
    Collection<Comment> findAllByItemOwnerId(Long ownerId);
}
