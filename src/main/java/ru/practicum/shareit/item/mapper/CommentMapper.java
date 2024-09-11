package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(author)
                .item(item)
                .build();
    }

    public static List<CommentDto> toCommentDto(Iterable<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(toCommentDto(comment));
        }
        return commentDtos;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();

    }
}
