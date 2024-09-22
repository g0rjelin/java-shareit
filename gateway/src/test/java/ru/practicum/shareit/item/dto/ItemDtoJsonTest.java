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
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void testItemDto() {
        Long id = 1L;
        int expectedId = 1;
        String name = "item name";
        String description = "item description";
        Boolean available = Boolean.TRUE;
        Long request = 10L;
        int expectedRequest = 10;
        LocalDateTime lastBooking = LocalDateTime.now().minusDays(1);
        LocalDateTime nextBooking = LocalDateTime.now().plusDays(1);
        LocalDateTime createdBooking = LocalDateTime.now();
        Long commentId = 100L;
        int expectedCommentId = 100;
        Collection<CommentDto> comments = List.of(new CommentDto(commentId, "test comment", "author", createdBooking));
        ItemDto itemDto = ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .request(request)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();

        JsonContent<ItemDto> jsonContent = json.write(itemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(expectedId);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(name);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo(description);
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(available);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.request").isEqualTo(expectedRequest);
        assertThat(jsonContent).extractingJsonPathStringValue("$.lastBooking").isEqualTo(lastBooking.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jsonContent).extractingJsonPathStringValue("$.nextBooking").isEqualTo(nextBooking.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jsonContent).extractingJsonPathNumberValue("$.comments.[0].id").isEqualTo(expectedCommentId);
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo("test comment");
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments.[0].authorName").isEqualTo("author");
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments.[0].created").isEqualTo(createdBooking.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}