package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

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
class BookingServiceIT {

    private final BookingService bookingService;

    private final EntityManager em;

    @Test
    void findBookingsOwnerByState_fullIntegrationTest() {
        String userName = "test user";
        String userEmail = "test@test.com";
        String bookerUserName = "test booker user";
        String bookerUserEmail = "booker@booker.com";
        Query query = em.createNativeQuery("insert into users(name, email) values (:name, :email), (:bookerName, :bookerEmail)");
        query.setParameter("name", userName);
        query.setParameter("email", userEmail);
        query.setParameter("bookerName", bookerUserName);
        query.setParameter("bookerEmail", bookerUserEmail);
        query.executeUpdate();
        TypedQuery<Long> getIdQuery = em.createQuery("select id from User where email = :email", Long.class);
        getIdQuery.setParameter("email", userEmail);
        Long ownerId = getIdQuery.getSingleResult();
        getIdQuery.setParameter("email", bookerUserEmail);
        Long bookerId = getIdQuery.getSingleResult();
        String itemName = "test item";
        String itemDescription = "test item description";
        Boolean isAvailable = true;
        query = em.createNativeQuery("insert into items(name, description, is_available, owner_id) values (:name, :description, :isAvailable, :ownerId)");
        query.setParameter("name", itemName);
        query.setParameter("description", itemDescription);
        query.setParameter("isAvailable", isAvailable);
        query.setParameter("ownerId", ownerId);
        query.executeUpdate();
        getIdQuery = em.createQuery("select id from Item where name = :itemName", Long.class);
        getIdQuery.setParameter("itemName", itemName);
        Long itemId = getIdQuery.getSingleResult();
        LocalDateTime approvedBookingStartDate = LocalDateTime.of(2024, 1, 1, 12, 30);
        LocalDateTime approvedBookingEndDate = LocalDateTime.of(2024, 1, 1, 18, 30);
        LocalDateTime waitingBookingStartDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).plusDays(1);
        LocalDateTime waitingBookingEndDate = waitingBookingStartDate.plusHours(10);
        LocalDateTime rejectedBookingStartDate = LocalDateTime.of(2023, 1, 1, 12, 30);
        LocalDateTime rejectedBookingEndDate = LocalDateTime.of(2023, 1, 1, 18, 30);
        query = em.createNativeQuery("insert into bookings(start_date, end_date, item_id, booker_id, status) values " +
                "(:approvedBookingStartDate, :approvedBookingEndDate, :itemId, :bookerId, 'APPROVED')," +
                "(:rejectedBookingStartDate, :rejectedBookingEndDate, :itemId, :bookerId, 'REJECTED')," +
                "(:waitingBookingStartDate, :waitingBookingEndDate, :itemId, :bookerId, 'WAITING')");
        query.setParameter("approvedBookingStartDate", approvedBookingStartDate);
        query.setParameter("approvedBookingEndDate", approvedBookingEndDate);
        query.setParameter("waitingBookingStartDate", waitingBookingStartDate);
        query.setParameter("waitingBookingEndDate", waitingBookingEndDate);
        query.setParameter("rejectedBookingStartDate", rejectedBookingStartDate);
        query.setParameter("rejectedBookingEndDate", rejectedBookingEndDate);
        query.setParameter("itemId", itemId);
        query.setParameter("bookerId", bookerId);
        query.executeUpdate();
        ItemShortDto itemShortDto = ItemShortDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(true)
                .build();
        UserDto booker = UserDto.builder()
                .id(bookerId)
                .name(bookerUserName)
                .email(bookerUserEmail)
                .build();
        Collection<BookingDto> expectedBookingDtos = List.of(
                BookingDto.builder()
                        .id(1L)
                        .start(approvedBookingStartDate)
                        .end(approvedBookingEndDate)
                        .status(BookingStatus.APPROVED.toString())
                        .booker(booker)
                        .item(itemShortDto)
                        .build(),
                BookingDto.builder()
                        .id(2L)
                        .start(waitingBookingStartDate)
                        .end(waitingBookingEndDate)
                        .status(BookingStatus.WAITING.toString())
                        .booker(booker)
                        .item(itemShortDto)
                        .build(),
                BookingDto.builder()
                        .id(3L)
                        .start(rejectedBookingStartDate)
                        .end(rejectedBookingEndDate)
                        .status(BookingStatus.REJECTED.toString())
                        .booker(booker)
                        .item(itemShortDto)
                        .build()
        );

        Collection<BookingDto> actualBookingDtos = bookingService.findBookingsOwnerByState(ownerId, BookingState.ALL, 0, 10);

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