package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.CommonConstants.X_SHARER_USER_ID;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    @SneakyThrows
    void findBookingByIdTest() {
        Long id = 1L;
        Long userId = 10L;
        String name = "test name";
        String description = "test description";
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 1, 1);
        long itemId = 100L;
        Boolean available = true;
        ItemShortDto itemShortDto = ItemShortDto.builder().id(itemId).name(name).description(description).available(available).build();
        Long bookerId = 2L;
        String bookerName = "test name";
        String bookerEmail = "test@test.com";
        UserDto userDto = UserDto.builder().id(bookerId).email(bookerEmail).name(bookerName).build();
        BookingDto bookingToGetDto = BookingDto.builder().id(id).start(start).end(end).item(itemShortDto).booker(userDto).build();
        Mockito.when(bookingService.findBookingById(userId, id)).thenReturn(bookingToGetDto);
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(id)
                .start(start)
                .end(end)
                .item(ItemShortDto.builder().id(itemId).name(name).description(description).available(available).build())
                .booker(UserDto.builder().id(bookerId).email(bookerEmail).name(bookerName).build())
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/bookings/{bookingId}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        BookingDto actualBookingDto = mapper.readValue(responseBody, BookingDto.class);
        Assertions.assertEquals(expectedBookingDto, actualBookingDto);
        Assertions.assertEquals(expectedBookingDto.getStart(), actualBookingDto.getStart());
        Assertions.assertEquals(expectedBookingDto.getEnd(), actualBookingDto.getEnd());
        Assertions.assertEquals(expectedBookingDto.getItem().getName(), actualBookingDto.getItem().getName());
        Assertions.assertEquals(expectedBookingDto.getItem().getDescription(), actualBookingDto.getItem().getDescription());
        Assertions.assertEquals(expectedBookingDto.getItem().getAvailable(), actualBookingDto.getItem().getAvailable());
        Assertions.assertEquals(expectedBookingDto.getBooker().getName(), actualBookingDto.getBooker().getName());
    }

    @Test
    @SneakyThrows
    void findBookingsByStateTest() {
        Long bookingId = 1L;
        Long userId = 10L;
        String name = "test name";
        String description = "test description";
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 1, 1);
        long itemId = 100L;
        Boolean available = true;
        Long bookerId = 2L;
        String bookerName = "test name";
        String bookerEmail = "test@test.com";
        BookingState bookingState = BookingState.ALL;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(ItemShortDto.builder().id(itemId).name(name).description(description).available(available).build())
                .booker(UserDto.builder().id(bookerId).email(bookerEmail).name(bookerName).build())
                .build();
        Collection<BookingDto> expectedBookingDtos = List.of(expectedBookingDto);
        Mockito.when(bookingService.findBookingsByState(userId, bookingState, 0, 10))
                .thenReturn(List.of(expectedBookingDto));

        MvcResult mvcResult = mockMvc.perform(get("/bookings?state={state}", bookingState)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Collection<BookingDto> actualBookingDto = mapper.readValue(responseBody, new TypeReference<Collection<BookingDto>>() {
        });
        Assertions.assertIterableEquals(expectedBookingDtos, actualBookingDto);
    }

    @Test
    @SneakyThrows
    void findBookingsOwnerByStateTest() {
        Long bookingId = 1L;
        Long userId = 10L;
        String name = "test name";
        String description = "test description";
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 1, 1);
        long itemId = 100L;
        Boolean available = true;
        Long bookerId = 2L;
        String bookerName = "test name";
        String bookerEmail = "test@test.com";
        BookingState bookingState = BookingState.ALL;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(ItemShortDto.builder().id(itemId).name(name).description(description).available(available).build())
                .booker(UserDto.builder().id(bookerId).email(bookerEmail).name(bookerName).build())
                .build();
        Collection<BookingDto> expectedBookingDtos = List.of(expectedBookingDto);
        Mockito.when(bookingService.findBookingsOwnerByState(userId, bookingState, 0, 10))
                .thenReturn(List.of(expectedBookingDto));

        MvcResult mvcResult = mockMvc.perform(get("/bookings/owner?state={state}", bookingState)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Collection<BookingDto> actualBookingDto = mapper.readValue(responseBody, new TypeReference<Collection<BookingDto>>() {
        });
        Assertions.assertIterableEquals(expectedBookingDtos, actualBookingDto);
    }

    @Test
    @SneakyThrows
    void createTest() {
        Long userId = 10L;
        String userName = "user name";
        String email = "email@email.com";
        Long id = 1L;
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 1, 1);
        long itemId = 100L;
        String name = "test";
        String description = "test description";
        Boolean available = true;
        BookingShortDto bookingToSaveShortDto = BookingShortDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(id)
                .item(ItemShortDto.builder().id(userId).name(name).description(description).available(available).build())
                .start(start)
                .end(end)
                .booker(UserDto.builder().id(userId).name(userName).email(email).build())
                .build();

        Mockito.when(bookingService.create(userId, bookingToSaveShortDto)).thenReturn(BookingDto.builder()
                .id(id)
                .item(ItemShortDto.builder().id(userId).name(name).description(description).available(available).build())
                .start(start)
                .end(end)
                .booker(UserDto.builder().id(userId).name(userName).email(email).build())
                .build());

        MvcResult mvcResult = mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingToSaveShortDto))
                        .header(X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        BookingDto actualBookingDto = mapper.readValue(responseBody, BookingDto.class);
        Assertions.assertNotNull(actualBookingDto);
        Assertions.assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    @SneakyThrows
    void updateTest() {
        Long userId = 10L;
        String userName = "user name";
        String email = "email@email.com";
        Long id = 1L;
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 1, 1);
        long itemId = 100L;
        String name = "test";
        String description = "test description";
        Boolean available = true;
        BookingDto expectedApprovedBookingDto = BookingDto.builder()
                .id(id)
                .item(ItemShortDto.builder().id(userId).name(name).description(description).available(available).build())
                .start(start)
                .end(end)
                .booker(UserDto.builder().id(userId).name(userName).email(email).build())
                .status(String.valueOf(BookingStatus.APPROVED))
                .build();
        BookingDto expectedRejectedBookingDto = BookingDto.builder()
                .id(id)
                .item(ItemShortDto.builder().id(userId).name(name).description(description).available(available).build())
                .start(start)
                .end(end)
                .booker(UserDto.builder().id(userId).name(userName).email(email).build())
                .status(String.valueOf(BookingStatus.REJECTED))
                .build();
        Mockito.when(bookingService.update(userId, id, true)).thenReturn(BookingDto.builder()
                .id(id)
                .item(ItemShortDto.builder().id(userId).name(name).description(description).available(available).build())
                .start(start)
                .end(end)
                .booker(UserDto.builder().id(userId).name(userName).email(email).build())
                .status(String.valueOf(BookingStatus.APPROVED))
                .build());
        Mockito.when(bookingService.update(userId, id, false)).thenReturn(BookingDto.builder()
                .id(id)
                .item(ItemShortDto.builder().id(userId).name(name).description(description).available(available).build())
                .start(start)
                .end(end)
                .booker(UserDto.builder().id(userId).name(userName).email(email).build())
                .status(String.valueOf(BookingStatus.REJECTED))
                .build());

        MvcResult mvcResultApproved = mockMvc.perform(patch("/bookings/" + id + "?approved={approved}", true)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult mvcResultRejected = mockMvc.perform(patch("/bookings/" + id + "?approved={approved}", false)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResultApproved.getResponse().getContentAsString();
        BookingDto actualApprovedBookingDto = mapper.readValue(responseBody, BookingDto.class);
        Assertions.assertNotNull(actualApprovedBookingDto);
        Assertions.assertEquals(expectedApprovedBookingDto, actualApprovedBookingDto);
        Assertions.assertEquals(expectedApprovedBookingDto.getStatus(), actualApprovedBookingDto.getStatus());
        responseBody = mvcResultRejected.getResponse().getContentAsString();
        BookingDto actualRejectedBookingDto = mapper.readValue(responseBody, BookingDto.class);
        Assertions.assertNotNull(actualRejectedBookingDto);
        Assertions.assertEquals(expectedRejectedBookingDto, actualRejectedBookingDto);
        Assertions.assertEquals(expectedRejectedBookingDto.getStatus(), actualRejectedBookingDto.getStatus());
    }
}