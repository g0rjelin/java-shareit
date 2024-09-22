package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestDtoJsonTest {
    private final JacksonTester<RequestDto> json;

    @Test
    @SneakyThrows
    void testRequestDto() {
        RequestDto requestDto = new RequestDto(
                "request description");

        JsonContent<RequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("request description");
    }
}