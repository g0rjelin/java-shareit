package ru.practicum.shareit.item.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

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
class ItemServiceImplTests {

    private final ItemService itemService;

    private final EntityManager em;

    @Test
    void getUserItems_fullIntegrationTest() {
        String userName = "test user";
        String userEmail = "test@test.com";
        String commentUserName = "test comment user";
        String commentUserEmail = "comment@comment.com";
        Query query = em.createNativeQuery("insert into users(name, email) values (:name, :email), (:commentName, :commentEmail)");
        query.setParameter("name", userName);
        query.setParameter("email", userEmail);
        query.setParameter("commentName", commentUserName);
        query.setParameter("commentEmail", commentUserEmail);
        query.executeUpdate();
        TypedQuery<Long> getIdQuery = em.createQuery("select id from User where email = :email", Long.class);
        getIdQuery.setParameter("email", userEmail);
        Long userId = getIdQuery.getSingleResult();
        getIdQuery.setParameter("email", commentUserEmail);
        Long commentAuthorId = getIdQuery.getSingleResult();
        String itemName = "test item";
        String itemDescription = "test item description";
        Boolean isAvailable = true;
        query = em.createNativeQuery("insert into items(name, description, is_available, owner_id) values (:name, :description, :isAvailable, :ownerId)");
        query.setParameter("name", itemName);
        query.setParameter("description", itemDescription);
        query.setParameter("isAvailable", isAvailable);
        query.setParameter("ownerId", userId);
        query.executeUpdate();
        getIdQuery = em.createQuery("select id from Item where name = :itemName", Long.class);
        getIdQuery.setParameter("itemName", itemName);
        Long itemId = getIdQuery.getSingleResult();
        String commentText = "test comment text";
        LocalDateTime dateTime = LocalDateTime.of(2024, 8, 11, 12, 30);
        query = em.createNativeQuery("insert into comments(text, item_id, author_id, created) values (:text, :itemId, :authorId, :created)");
        query.setParameter("text", commentText);
        query.setParameter("itemId", itemId);
        query.setParameter("authorId", commentAuthorId);
        query.setParameter("created", dateTime);
        query.executeUpdate();
        getIdQuery = em.createQuery("select id from Comment where text = :commentText", Long.class);
        getIdQuery.setParameter("commentText", commentText);
        Long commentId = getIdQuery.getSingleResult();
        LocalDateTime prevBookingStartDate = LocalDateTime.of(2024, 1, 1, 12, 30);
        LocalDateTime prevBookingEndDate = LocalDateTime.of(2024, 1, 1, 18, 30);
        LocalDateTime nextBookingStartDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).plusDays(1);
        LocalDateTime nextBookingEndDate = nextBookingStartDate.plusHours(10);
        BookingStatus bookingStatus = BookingStatus.APPROVED;
        query = em.createNativeQuery("insert into bookings(start_date, end_date, item_id, booker_id, status) values " +
                "(:prevBookingStartDate, :prevBookingEndDate, :itemId, :bookerId, :status)," +
                "(:nextBookingStartDate, :nextBookingEndDate, :itemId, :bookerId, :status)");
        query.setParameter("prevBookingStartDate", prevBookingStartDate);
        query.setParameter("prevBookingEndDate", prevBookingEndDate);
        query.setParameter("nextBookingStartDate", nextBookingStartDate);
        query.setParameter("nextBookingEndDate", nextBookingEndDate);
        query.setParameter("itemId", itemId);
        query.setParameter("bookerId", commentAuthorId);
        query.setParameter("status", bookingStatus.toString());
        query.executeUpdate();
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .name(itemName)
                .description(itemDescription)
                .comments(List.of(CommentDto.builder().id(commentId).text(commentText).authorName(commentUserName).created(dateTime).build()))
                .nextBooking(nextBookingStartDate)
                .lastBooking(prevBookingEndDate)
                .available(isAvailable)
                .build();
        Collection<ItemDto> expectedItemDtos = List.of(expectedItemDto);

        Collection<ItemDto> actualItemDtos = itemService.findAllItemsByOwnerId(userId);

        assertThat(actualItemDtos, hasSize(expectedItemDtos.size()));
        for (ItemDto actualItemDto : actualItemDtos) {
            assertThat(expectedItemDtos, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(actualItemDto.getName())),
                    hasProperty("description", equalTo(actualItemDto.getDescription())),
                    hasProperty("available", equalTo(actualItemDto.getAvailable())),
                    hasProperty("lastBooking", equalTo(actualItemDto.getLastBooking())),
                    hasProperty("nextBooking", equalTo(actualItemDto.getNextBooking())),
                    hasProperty("request", nullValue())
            )));
            for (CommentDto actualCommentDto : actualItemDto.getComments()) {
                assertThat(expectedItemDto.getComments(), hasItem(allOf(
                        hasProperty("id", notNullValue()),
                        hasProperty("text", equalTo(actualCommentDto.getText())),
                        hasProperty("authorName", equalTo(actualCommentDto.getAuthorName())),
                        hasProperty("created", equalTo(actualCommentDto.getCreated()))
                )));
            }
        }
    }
}
