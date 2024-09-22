package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookItemRequestDtoJsonTest {
    private final JacksonTester<BookItemRequestDto> json;

    @Test
    public void testBookItemRequestDto() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Long itemId = 1L;
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(itemId, start, end);

        JsonContent<BookItemRequestDto> jsonContent = json.write(bookItemRequestDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    }

}