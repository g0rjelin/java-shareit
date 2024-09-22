package ru.practicum.shareit.item.dto;

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
class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void testItemRequestDto() {
        Long id = 1L;
        int expectedId = 1;
        String name = "item name";
        String description = "item description";
        Boolean available = Boolean.TRUE;
        Long request = 10L;
        int expectedRequest = 10;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .requestId(request)
                .build();

        JsonContent<ItemRequestDto> jsonContent = json.write(itemRequestDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(expectedId);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(name);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo(description);
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(available);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(expectedRequest);
    }
}