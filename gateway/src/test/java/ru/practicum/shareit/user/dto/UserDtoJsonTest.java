package ru.practicum.shareit.user.dto;

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
public class UserDtoJsonTest {
    private final JacksonTester<UserRequestDto> json;

    @Test
    @SneakyThrows
    void testUserRequestDto() {
        UserRequestDto userRequestDto = new UserRequestDto(
                1L,
                "test user",
                "test@test.com");

        JsonContent<UserRequestDto> result = json.write(userRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@test.com");
    }
}
