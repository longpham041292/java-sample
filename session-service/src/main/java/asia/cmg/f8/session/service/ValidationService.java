package asia.cmg.f8.session.service;

import static asia.cmg.f8.session.repository.SessionSpecification.overlapWithBookedSessionCondition;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.BookingResponse;
import asia.cmg.f8.session.dto.ReservationSlot;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionPackageStatus;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.OrderRepository;
import asia.cmg.f8.session.repository.SessionRepository;
import asia.cmg.f8.session.rule.booking.BookingInput;
import asia.cmg.f8.session.rule.booking.BookingValidationContext;
import asia.cmg.f8.session.rule.booking.ValidationResult;
import asia.cmg.f8.session.rule.booking.ValidationStrategy;
import asia.cmg.f8.session.utils.TimeSlotUtil;
import asia.cmg.f8.session.wrapper.dto.ActiveOrderTimeRange;
import asia.cmg.f8.session.wrapper.service.OrderSessionWrapperService;

/**
 * Created on 12/9/16.
 */
@Service
public class ValidationService {

    private final DoubleBookingService doubleBookingService;
    private final OrderRepository orderRepository;
    private final SessionRepository sessionRepository;
    private final Set<ValidationStrategy> strategies;
    private final BasicUserRepository basicUserRepository;
    private final OrderSessionWrapperService orderSessionWrapperService;
    private static final Logger LOG = LoggerFactory.getLogger(ValidationService.class);

    @Inject
    public ValidationService(final DoubleBookingService doubleBookingService,
                             final OrderRepository orderRepository,
                             final SessionRepository sessionRepository,
                             final Set<ValidationStrategy> strategies,
                             final BasicUserRepository basicUserRepository,
                             final OrderSessionWrapperService orderSessionWrapperService) {
        this.doubleBookingService = doubleBookingService;
        this.orderRepository = orderRepository;
        this.sessionRepository = sessionRepository;
        this.strategies = new LinkedHashSet<>(strategies);
        this.basicUserRepository = basicUserRepository;
        this.orderSessionWrapperService = orderSessionWrapperService;
    }

    public BookingResponse checkDoubleBooking(final BookingInput bookingInput) {
        return doubleBookingService.checkDoubleBooking(bookingInput);
    }

    public ValidationResult bookingValidation(final BookingInput bookingInput) {
        // TODO refactor later.
        final BookingValidationContext ctxBooking = new BookingValidationContext(strategies);
        return ctxBooking.execute(bookingInput);
    }

    public boolean checkOverlappingWithBookedSession(final BookingInput bookingInput) {
    	boolean hasError = false;
    	
    	final List<SessionEntity> listSessionHasBooked = sessionRepository.findAll(overlapWithBookedSessionCondition(bookingInput));
    	if(null != listSessionHasBooked) {
    		for(final SessionEntity entity : listSessionHasBooked) {
    			LOG.info("Check overlap booking session entity status {} and startTime {}", entity.getStatus(), entity.getStartTime());
    			//User had booked training session before but it was overdue
    			if(SessionStatus.PENDING.equals(entity.getStatus()) && LocalDateTime.now().compareTo(entity.getStartTime()) > 0) {
    				continue;
    			}
    			else {//User had booked the training before. Should not allow to book overlaps
    				hasError = true;
    				break;
    			}
    		}
    	}
    	return hasError;
    }

    public LocalDateTime getMaxValidExpiredDate(final BookingInput bookingInput) {
        final List<String> validSessionPackage = SessionPackageStatus.getValidSessionPackageStatus().stream().map(Enum::name).collect(Collectors.toList());
        final List<String> bookingStatus = SessionStatus.getBookingStatus().stream().map(Enum::name).collect(Collectors.toList());
        final Object[] maxValidExpiredDateByUser = orderRepository.getMaxValidExpiredDateByUser(bookingInput.getUserId(), bookingInput.getTrainerId(), validSessionPackage, bookingStatus);

        if (maxValidExpiredDateByUser.length == 0 ||
                Objects.isNull(maxValidExpiredDateByUser[0])) {
            return null;
        }

        final Object[] row = (Object[]) maxValidExpiredDateByUser[0];

        // return expired date if it's exist.
        if (!Objects.isNull(row[0])) {
            return ZoneDateTimeUtils.convertFromUTCToLocalDateTime(((BigInteger) row[0]).longValue());
        } else {
            // return expired date which based on first booked session.
            if (!Objects.isNull(row[2])) {
                return ZoneDateTimeUtils.convertFromUTCToLocalDateTime(((BigInteger) row[2]).longValue())
                        .plusDays(((Integer) row[1]).longValue());
            } else {
                // return expired date which based on first reserving session.
                final Optional<ReservationSlot> firstSlot = bookingInput.getReservationSlotList().stream()
                        .filter(slot ->
                                Objects.isNull(slot.getConfirmed()) || (
                                        Objects.isNull(slot.getSessionId()) &&
                                                !Objects.isNull(slot.getConfirmed()) &&
                                                slot.getConfirmed()))
                        .sorted(Comparator.comparing(ReservationSlot::getStartTime))
                        .findFirst();
                if (firstSlot.isPresent()) {
                    return firstSlot.get().getStartTime().plusDays(((Integer) row[1]).longValue());
                }
            }
        }

        return null;
    }

    /**
     * Validate reservation list if there is any reservation slot is out of expired range of activated order of given user.
     *
     * @param bookingInput the booking input.
     * @return true if has error
     */
    public boolean validateBookingInputTimeRange(final BookingInput bookingInput) {
        final List<String> queryStatus = Arrays
                .asList(SessionStatus.COMPLETED, SessionStatus.CONFIRMED, SessionStatus.PENDING,
                        SessionStatus.BURNED, SessionStatus.EU_CANCELLED).stream().map(Enum::name)
                .collect(Collectors.toList());
        final List<String> validPackageStatus = SessionPackageStatus.getValidSessionPackageStatus()
                .stream().map(Enum::name).collect(Collectors.toList());
        
        final List<ActiveOrderTimeRange> activeOrders = orderSessionWrapperService
                .getActiveOrdersAndTimeRange(bookingInput.getUserId(), bookingInput.getTrainerId(), bookingInput.getPackageUuid(),
                        queryStatus, validPackageStatus);

        if (activeOrders.isEmpty()) {
            return false;
        }

        final Optional<ReservationSlot> result = bookingInput
                .getReservationSlotList()
                .stream()
                .filter(reservationSlot -> !isValidTime(
                        reservationSlot.getStartTime().atZone(ZoneOffset.systemDefault()).toEpochSecond(),
                        activeOrders)).findFirst();

        return result.isPresent();
    }

    public boolean checkActivatedUser(final String userId, final String trainerId) {
        return basicUserRepository.getActivatedUserByUuid(userId, trainerId).size() == 2;
    }


    private boolean isValidTime(final long time, final List<ActiveOrderTimeRange> activeOrders) {
        final Optional<ActiveOrderTimeRange> result = activeOrders.stream()
                .filter(activeOrder -> isTimeFitOrder(time, activeOrder)).findFirst();
        return result.isPresent();
    }

    private boolean isTimeFitOrder(final long timeInSec, final ActiveOrderTimeRange activeOrder) {
        final long timeInMillis = timeInSec * 1000;
        if (activeOrder.getExpireTime() == null) {
            return timeInSec < TimeSlotUtil.estimateExpireDate(activeOrder.getMinStartTime(),
                    activeOrder.getLimitDays());
        }
        return timeInMillis < activeOrder.getExpireTime().getTime();
    }

}
