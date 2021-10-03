package asia.cmg.f8.session.service;

import java.util.Set;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.dto.BookingResponse;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.dto.TimeSlot;

/**
 * Created on 2/16/17.
 */
public interface SessionBookingService {

    /**
     * Handle booking session of given user.
     *
     * @param timeSlotList the booking time slot.
     * @param userId       the user id.
     * @param trainerId    the trainer id.
     * @param account      the current logged-in account
     * @return the {@link BookingResponse}
     */
    BookingResponse doBooking(Set<TimeSlot> timeSlotList, String userId, String trainerId, String packageId, Account account, ClubDto club);
}
