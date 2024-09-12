package ru.practicum.shareit.booking.repository;

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
    Collection<Booking> findAllByState(Long bookerId, String state);

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
    Collection<Booking> findAllOwnerByState(Long ownerId, String state);

    @Query("select case when count(*) = 1 then true else false end " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "  and b.item.id = ?2 " +
            "  and b.status = 'APPROVED' " +
            "  and b.end <= CURRENT_TIMESTAMP")
    boolean existValidBooking(Long authorId, Long itemId);

    @Query("select case" +
            "        when count(*) != 0 " +
            "          and coalesce( min(case when b.start >= ?1 then b.start end), ?1) < ?2 " +
            "          and coalesce( max(case when b.end <= ?2 then b.end end), ?2) >= ?1" +
            "        then true" +
            "        else false" +
            "    end as has_intersection " +
            "from Booking as b " +
            "where b.item.id = ?3 " +
            "and b.status != 'REJECTED'")
    boolean existIntersectingBookingDatesForItem(LocalDateTime start, LocalDateTime end, Long itemId);
}
