package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.CommonConstants.X_SHARER_USER_ID;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private final ObjectMapper mapper;
    private final MockMvc mockMvc;

    @MockBean
    private ItemService itemService;


    @Test
    @SneakyThrows
    void findAllItemsByOwnerIdTest() {
        long userId = 10L;
        Long id = 1L;
        String name = "test name";
        String description = "test description";
        Boolean available = true;
        ItemDto expectedItemDto = ItemDto.builder().id(id).name(name).description(description).available(available).build();
        Collection<ItemDto> expectedItemDtos = List.of(expectedItemDto);
        Mockito.doReturn(List.of(ItemDto.builder().id(id).name(name).description(description).available(available).build())).when(itemService).findAllItemsByOwnerId(userId);

        MvcResult mvcResult = mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Collection<ItemDto> actualItemDto = mapper.readValue(responseBody, new TypeReference<Collection<ItemDto>>() {
        });
        Assertions.assertIterableEquals(expectedItemDtos, actualItemDto);
    }

    @Test
    @SneakyThrows
    void findItemByIdTest() {
        Long id = 1L;
        String name = "test name";
        String description = "test description";
        ItemDto itemToGetDto = ItemDto.builder().id(id).name(name).description(description).build();
        Mockito.when(itemService.findItemById(id)).thenReturn(itemToGetDto);
        ItemDto expectedItemDto = ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
        MvcResult mvcResult = mockMvc.perform(get("/items/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        ItemDto actualItemDto = mapper.readValue(responseBody, ItemDto.class);
        Assertions.assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    @SneakyThrows
    void searchItemsTest() {
        Long id = 1L;
        String text = "test";
        String name = "test name";
        String description = "test description";
        Boolean available = true;
        ItemShortDto expectedItemShortDto = ItemShortDto.builder().id(id).name(name).description(description).available(available).build();
        Collection<ItemShortDto> expectedItemShortDtos = List.of(expectedItemShortDto);
        Mockito.doReturn(List.of(ItemDto.builder().id(id).name(name).description(description).available(available).build())).when(itemService).searchItems(anyString());

        MvcResult mvcResult = mockMvc.perform(get("/items/search?text={text}",text)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Collection<ItemShortDto> actualItemShortDto = mapper.readValue(responseBody, new TypeReference<Collection<ItemShortDto>>() {
        });
        Assertions.assertIterableEquals(expectedItemShortDtos, actualItemShortDto);
    }

    @Test
    @SneakyThrows
    void createTest() {
        Long userId = 10L;
        Long id = 1L;
        String name = "test";
        String description = "test description";
        ItemShortDto itemToSaveShortDto = ItemShortDto.builder().description(description).name(name).build();
        ItemShortDto expectedItemShortDto = ItemShortDto.builder().id(id).description(description).name(name).build();
        Mockito.when(itemService.create(userId, itemToSaveShortDto)).thenReturn(expectedItemShortDto);

        MvcResult mvcResult = mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemToSaveShortDto))
                        .header(X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemShortDto actualItemShortDto = mapper.readValue(responseBody, ItemShortDto.class);
        Assertions.assertNotNull(actualItemShortDto);
        Assertions.assertEquals(expectedItemShortDto, actualItemShortDto);
    }

    @Test
    @SneakyThrows
    void updateTest() {
        Long userId = 10L;
        Long id = 1L;
        String updDescription = "update description";
        String updName = "update";
        ItemShortDto itemToUpdateDto = ItemShortDto.builder().name(updName).description(updDescription).build();
        ItemShortDto expectedUpdateItemShortDto = ItemShortDto.builder().id(id).description(updDescription).name(updName).build();
        Mockito.when(itemService.update(userId, id, itemToUpdateDto)).thenReturn(expectedUpdateItemShortDto);

        MvcResult mvcResult = mockMvc.perform(patch("/items/" + id)
                        .content(mapper.writeValueAsString(itemToUpdateDto))
                        .header(X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        ItemShortDto actualUpdateItemShortDto = mapper.readValue(responseBody, ItemShortDto.class);
        Assertions.assertNotNull(actualUpdateItemShortDto);
        Assertions.assertEquals(expectedUpdateItemShortDto, actualUpdateItemShortDto);
        Assertions.assertEquals(expectedUpdateItemShortDto.getDescription(), actualUpdateItemShortDto.getDescription());
        Assertions.assertEquals(expectedUpdateItemShortDto.getName(), actualUpdateItemShortDto.getName());
    }

    @Test
    @SneakyThrows
    void addCommentTest() {
        Long userId = 10L;
        Long id = 1L;
        Long itemId = 100L;
        String text = "text";
        String authorName = "author";
        LocalDateTime created = LocalDateTime.now();
        CommentShortDto commentToAddShortDto = new CommentShortDto(text);
        CommentDto expectedCommentDto = CommentDto.builder()
                .id(id).authorName(authorName).text(text).created(created).build();
        Mockito.when(itemService.addComment(anyLong(), anyLong(), any(CommentShortDto.class))).thenReturn(
                CommentDto.builder().id(id).authorName(authorName).text(text).created(created).build());

        MvcResult mvcResult = mockMvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentToAddShortDto))
                        .header(X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        CommentDto actualCommentShortDto = mapper.readValue(responseBody, CommentDto.class);
        Assertions.assertNotNull(actualCommentShortDto);
        Assertions.assertEquals(expectedCommentDto, actualCommentShortDto);
    }
}