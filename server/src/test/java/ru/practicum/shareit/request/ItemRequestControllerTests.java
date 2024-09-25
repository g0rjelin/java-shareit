package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.CommonConstants.X_SHARER_USER_ID;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTests {

    private final ObjectMapper mapper;
    private final MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;


    @Test
    @SneakyThrows
    void addItemRequestTest() {
        long userId = 1L;
        long id = 1L;
        String description = "Test";
        LocalDateTime localDateTime = LocalDateTime.now();
        ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto(description);
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder().id(id).description(description).created(localDateTime).build();
        Mockito.when(itemRequestService.create(anyLong(), any(ItemRequestShortDto.class))).thenReturn(ItemRequestDto.builder().id(id).description(description).created(localDateTime).build());

        MvcResult mvcResult = mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestShortDto))
                        .header(X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemRequestDto actualItemRequestDto = mapper.readValue(responseBody, ItemRequestDto.class);
        Assertions.assertNotNull(actualItemRequestDto);
        Assertions.assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }

    @Test
    @SneakyThrows
    void getRequestsTest() {
        long userId = 10L;
        long id = 1L;
        String description = "Test";
        LocalDateTime localDateTime = LocalDateTime.now();
        ItemRequestFullDto expectedItemRequestFullDto = ItemRequestFullDto.builder().id(id).description(description).created(localDateTime).build();
        Collection<ItemRequestFullDto> expectedItemRequestFullDtos = List.of(expectedItemRequestFullDto);

        Mockito.when(itemRequestService.getItemRequestsByRequestorId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(ItemRequestFullDto.builder().id(id).description(description).created(localDateTime).build()));

        MvcResult mvcResult = mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Collection<ItemRequestFullDto> actualItemRequestFullDto = mapper.readValue(responseBody, new TypeReference<Collection<ItemRequestFullDto>>() {});
        Assertions.assertIterableEquals(expectedItemRequestFullDtos, actualItemRequestFullDto);
    }

    @Test
    @SneakyThrows
    void getAllRequestsTest() {
        long userId = 10L;
        long id = 1L;
        String description = "Test";
        LocalDateTime localDateTime = LocalDateTime.now();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder().id(id).description(description).created(localDateTime).build();
        Collection<ItemRequestDto> expectedItemRequestDtos = List.of(expectedItemRequestDto);
        Mockito.when(itemRequestService.getAllOtherItemRequestsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(ItemRequestDto.builder().id(id).description(description).created(localDateTime).build()));

        MvcResult mvcResult = mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Collection<ItemRequestDto> actualItemRequestDto = mapper.readValue(responseBody, new TypeReference<Collection<ItemRequestDto>>() {});
        Assertions.assertIterableEquals(expectedItemRequestDtos, actualItemRequestDto);

    }

    @Test
    @SneakyThrows
    void getRequestTest() {
        long id = 1L;
        String description = "Test";
        LocalDateTime localDateTime = LocalDateTime.now();
        ItemRequestFullDto expectedItemRequestFullDto = ItemRequestFullDto.builder().id(id).description(description).created(localDateTime).build();
        Mockito.when(itemRequestService.getItemRequestById(id)).thenReturn(ItemRequestFullDto.builder().id(id).description(description).created(localDateTime).build());

        MvcResult mvcResult = mockMvc.perform(get("/requests/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemRequestFullDto actualItemRequestFullDto = mapper.readValue(responseBody, ItemRequestFullDto.class);
        Assertions.assertEquals(expectedItemRequestFullDto, actualItemRequestFullDto);
    }
}
