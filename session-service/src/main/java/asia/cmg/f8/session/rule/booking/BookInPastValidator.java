package asia.cmg.f8.session.rule.booking;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.UnmodifiableIterator;

import asia.cmg.f8.session.dto.ReservationSlot;



/**
 * Created on 12/15/16.
 */
public class BookInPastValidator implements ValidationStrategy<BooleanValidationResult> {

	public static final Logger LOGGER = LoggerFactory.getLogger(BookInPastValidator.class);
	
    @Override
    public BooleanValidationResult validate(final BookingInput bookingInput) {
		Boolean result = false;
		if (null != bookingInput.getReservationSlotList() && !bookingInput.getAccount().isAdmin()) {
			final UnmodifiableIterator<ReservationSlot> iterators = bookingInput.getReservationSlotList().iterator();
			while (iterators.hasNext()) {
				final ReservationSlot slot = iterators.next();
				if (ChronoUnit.MINUTES.between(LocalDateTime.now(), slot.getStartTime()) <= 0) {
					result = true;
					break;
				}
			}
		}
    	
        return new BooleanValidationResult(result, getValidationType().getMessage());
    
    }

    @Override
    public ValidationType getValidationType() {
        return ValidationType.BOOK_IN_PAST;
    }
}
