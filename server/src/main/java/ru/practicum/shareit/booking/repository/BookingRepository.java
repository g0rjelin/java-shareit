package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByItem_Owner_Id(Long itemId);

    @Query("select booking " +
            "from Booking as booking " +
            "where booking.booker.id = ?1" +
            "  and (?2 = 'ALL'" +
            "    or ?2 = 'CURRENT' and CURRENT_TIMESTAMP between booking.start and booking.end" +
            "    or ?2 = 'PAST' and booking.status = 'APPROVED' and booking.end < CURRENT_TIMESTAMP" +
            "    or ?2 = 'FUTURE' and booking.status ='APPROVED' and booking.end >= CURRENT_TIMESTAMP" +
            "    or ?2 = 'WAITING' and booking.status = 'WAITING'" +
            "    or ?2 = 'REJECTED' and booking.status = 'REJECTED') " +
            "order by booking.start desc")
    Page<Booking> findAllByState(Long bookerId, String state, Pageable pageable);

    @Query("select booking " +
            "from Booking as booking " +
            "where booking.item.owner.id = ?1" +
            "  and (?2 = 'ALL'" +
            "    or ?2 = 'CURRENT' and CURRENT_TIMESTAMP between booking.start and booking.end" +
            "    or ?2 = 'PAST' and booking.status = 'APPROVED' and booking.end < CURRENT_TIMESTAMP" +
            "    or ?2 = 'FUTURE' and booking.status ='APPROVED' and booking.end >= CURRENT_TIMESTAMP" +
            "    or ?2 = 'WAITING' and booking.status = 'WAITING'" +
            "    or ?2 = 'REJECTED' and booking.status = 'REJECTED') " +
            "order by booking.start desc")
    Page<Booking> findAllOwnerByState(Long ownerId, String state, Pageable pageable);

    @Query("select (count(*) = 1) " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "  and b.item.id = ?2 " +
            "  and b.status = 'APPROVED' " +
            "  and b.end <= CURRENT_TIMESTAMP")
    boolean existValidBooking(Long authorId, Long itemId);

    @Query("select (count(b) != 0) as has_intersection " +
            "from Booking as b " +
            "where b.item.id = ?3 " +
            "  and b.status = 'APPROVED'" +
            "  and b.start <= ?2 and b.end >= ?1")
    boolean existIntersectingBookingDatesForItem(LocalDateTime start, LocalDateTime end, Long itemId);
}
