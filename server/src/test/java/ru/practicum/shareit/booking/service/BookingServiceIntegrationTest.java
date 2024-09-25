package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class BookingServiceIntegrationTest {

    private final BookingService bookingService;

    private final EntityManager em;

    User owner;
    User booker;
    Item item;
    ItemShortDto itemShortDto;
    UserDto bookerDto;

    @BeforeEach
    void setUp() {
        String ownerUserName = "test user owner";
        String ownerUserEmail = "owner@test.com";
        owner = User.builder()
                .name(ownerUserName).email(ownerUserEmail).build();
        em.persist(owner);

        String bookerUserName = "test booker user";
        String bookerUserEmail = "booker@booker.com";
        booker = User.builder()
                .name(bookerUserName).email(bookerUserEmail).build();
        em.persist(booker);

        String itemName = "test item";
        String itemDescription = "test item description";
        Boolean isAvailable = true;
        item = Item.builder()
                .name(itemName).description(itemDescription).available(isAvailable).owner(owner).build();
        em.persist(item);

        itemShortDto = ItemShortDto.builder().id(item.getId()).name(itemName).description(itemDescription).available(isAvailable).build();
        bookerDto = UserDto.builder().id(booker.getId()).name(bookerUserName).email(bookerUserEmail).build();
    }

    @Test
    void findBookingsOwnerByState_fullIntegrationTest() {
        LocalDateTime approvedBookingStartDate = LocalDateTime.of(2024, 1, 1, 12, 30);
        LocalDateTime approvedBookingEndDate = LocalDateTime.of(2024, 1, 1, 18, 30);
        LocalDateTime waitingBookingStartDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).plusDays(1);
        LocalDateTime waitingBookingEndDate = waitingBookingStartDate.plusHours(10);
        LocalDateTime rejectedBookingStartDate = LocalDateTime.of(2023, 1, 1, 12, 30);
        LocalDateTime rejectedBookingEndDate = LocalDateTime.of(2023, 1, 1, 18, 30);
        BookingShortDto waitingBookingShortDto = BookingShortDto.builder()
                .itemId(item.getId())
                .start(waitingBookingStartDate)
                .end(waitingBookingEndDate)
                .build();
        BookingDto waitingBookingDto = bookingService.create(booker.getId(), waitingBookingShortDto);
        BookingShortDto approvedBookingShortDto = BookingShortDto.builder()
                .itemId(item.getId())
                .start(approvedBookingStartDate)
                .end(approvedBookingEndDate)
                .build();
        BookingDto approvedBookingDto = bookingService.create(booker.getId(), approvedBookingShortDto);
        approvedBookingDto = bookingService.update(owner.getId(), approvedBookingDto.getId(), true);
        BookingShortDto rejectedBookingShortDto = BookingShortDto.builder()
                .itemId(item.getId())
                .start(rejectedBookingStartDate)
                .end(rejectedBookingEndDate)
                .build();
        BookingDto rejectedBookingDto = bookingService.create(booker.getId(), rejectedBookingShortDto);
        rejectedBookingDto = bookingService.update(owner.getId(), rejectedBookingDto.getId(), false);

        Collection<BookingDto> expectedBookingDtos = List.of(
                BookingDto.builder()
                        .id(approvedBookingDto.getId())
                        .start(approvedBookingStartDate)
                        .end(approvedBookingEndDate)
                        .status(BookingStatus.APPROVED.toString())
                        .booker(bookerDto)
                        .item(itemShortDto)
                        .build(),
                BookingDto.builder()
                        .id(waitingBookingDto.getId())
                        .start(waitingBookingStartDate)
                        .end(waitingBookingEndDate)
                        .status(BookingStatus.WAITING.toString())
                        .booker(bookerDto)
                        .item(itemShortDto)
                        .build(),
                BookingDto.builder()
                        .id(rejectedBookingDto.getId())
                        .start(rejectedBookingStartDate)
                        .end(rejectedBookingEndDate)
                        .status(BookingStatus.REJECTED.toString())
                        .booker(bookerDto)
                        .item(itemShortDto)
                        .build()
        );

        Collection<BookingDto> actualBookingDtos = bookingService.findBookingsOwnerByState(owner.getId(), BookingState.ALL, 0, 10);

        assertThat(actualBookingDtos, hasSize(expectedBookingDtos.size()));
        for (BookingDto actualBookingDto : actualBookingDtos) {
            assertThat(expectedBookingDtos, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("start", equalTo(actualBookingDto.getStart())),
                    hasProperty("end", equalTo(actualBookingDto.getEnd())),
                    hasProperty("status", equalTo(actualBookingDto.getStatus())),
                    hasProperty("item", allOf(
                            hasProperty("id", notNullValue()),
                            hasProperty("name", equalTo(actualBookingDto.getItem().getName())),
                            hasProperty("description", equalTo(actualBookingDto.getItem().getDescription())),
                            hasProperty("available", equalTo(actualBookingDto.getItem().getAvailable())),
                            hasProperty("requestId", nullValue())
                    )),
                    hasProperty("booker", allOf(
                            hasProperty("id", notNullValue()),
                            hasProperty("name", equalTo(actualBookingDto.getBooker().getName())),
                            hasProperty("email", equalTo(actualBookingDto.getBooker().getEmail()))
                    ))
            )));
        }
    }
}