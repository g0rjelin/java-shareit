package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoJsonTest {
    private final JacksonTester<CommentDto> json;

    @Test
    @SneakyThrows
    void testCommentDto() {
        Long commentId = 100L;
        int expectedCommentId = 100;
        String text = "test comment";
        String author = "author";
        Boolean available = Boolean.TRUE;
        LocalDateTime createdBooking = LocalDateTime.now();
        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .text(text)
                .authorName(author)
                .created(createdBooking)
                .build();

        JsonContent<CommentDto> jsonContent = json.write(commentDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(expectedCommentId);
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo(text);
        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo(author);
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo(createdBooking.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}