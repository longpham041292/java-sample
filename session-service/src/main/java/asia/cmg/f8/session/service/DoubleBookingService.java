package asia.cmg.f8.session.service;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.BasicUserInfo;
import asia.cmg.f8.session.dto.BookingResponse;
import asia.cmg.f8.session.dto.ReservationSlot;
import asia.cmg.f8.session.dto.ReviewListResponse;
import asia.cmg.f8.session.dto.ReviewTimeSlot;
import asia.cmg.f8.session.dto.TimeSlot;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.SessionRepository;
import asia.cmg.f8.session.rule.booking.BookingInput;
import asia.cmg.f8.session.utils.TimeSlotUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static asia.cmg.f8.session.repository.SessionSpecification.doubleSessionCondition;
import static asia.cmg.f8.session.repository.SessionSpecification.doubleSessionWithUserInfoCondition;

/**
 * Created on 12/15/16.
 */
@Service
public class DoubleBookingService {

    private final SessionRepository sessionRepository;
    private final BasicUserRepository basicUserRepository;

    @Inject
    public DoubleBookingService(final SessionRepository sessionRepository,
                                final BasicUserRepository basicUserRepository) {
        this.sessionRepository = sessionRepository;
        this.basicUserRepository = basicUserRepository;
    }

    public List<SessionEntity> findAllWithDoubleBookingCondition(final BookingInput bookingInput) {
        return sessionRepository.findAll(doubleSessionCondition(bookingInput));
    }

    public BookingResponse checkDoubleBooking(final BookingInput bookingInput) {

        final boolean isConfirmed = bookingInput.getReservationSlotList().stream().findFirst().map(slot -> slot.getConfirmed() != null).orElse(Boolean.FALSE);

        if (bookingInput.getAccount().isPt() && !isConfirmed) {
            final List<SessionEntity> doubleSessionList = findAllWithDoubleBookingCondition(bookingInput);
            if (!doubleSessionList.isEmpty()) {
                final Set<TimeSlot> timeSlots = buildDoubleBookingList(bookingInput.getReservationSlotList(), doubleSessionList);

                return BookingResponse.builder()
                        .withResult(false)
                        .withBookingList(timeSlots)
                        .withUserInfo(null)
                        .build();
            }
        }

        return BookingResponse.builder().withResult(true).build();
    }

    private Set<TimeSlot> buildDoubleBookingList(final Set<ReservationSlot> reservationList, final List<SessionEntity> doubleBooking) {
        return reservationList.stream().map(slot -> {

            final TimeSlot timeSlot = new TimeSlot();
            timeSlot.setStartTime(ZoneDateTimeUtils.convertToSecondUTC(slot.getStartTime()));
            timeSlot.setEndTime(ZoneDateTimeUtils.convertToSecondUTC(slot.getEndTime()));
            timeSlot.setConflict(doubleBooking.stream().anyMatch(session -> checkConflictSession(session, slot)));
            //timeSlot.setConflict(doubleBooking.stream().allMatch(session -> checkConflictSession(session, slot)));
            
            final String sessionId = slot.getSessionId();

            if (sessionId != null && !sessionId.isEmpty()) {
                timeSlot.setSessionId(sessionId);
            }

            return timeSlot;
        }).collect(Collectors.toSet());
    }

    private boolean checkConflictSession(final SessionEntity session, final ReservationSlot slot) {
        final LocalDateTime startTime = session.getStartTime();
        final LocalDateTime endTime = session.getEndTime();
        //final  LocalDateTime currentDateTime = LocalDateTime.now();

        return ((startTime.isBefore(slot.getStartTime()) && endTime.isAfter(slot.getStartTime())) ||
                (startTime.isBefore(slot.getEndTime()) && endTime.isAfter(slot.getEndTime())) ||
                (startTime.isEqual(slot.getStartTime()) && endTime.isEqual(slot.getEndTime())));
   
       // return ((currentDateTime.isBefore(startTime))   || (currentDateTime.isEqual(startTime)));
        
       
    }

    @Transactional(readOnly = true)
    public ReviewListResponse getReviewSessionList(final Set<TimeSlot> timeSlotList,
                                                   final String userId,
                                                   final String trainerId,
                                                   final Account account) {
        if (Objects.isNull(timeSlotList) || timeSlotList.isEmpty()) {
            throw new IllegalArgumentException("Date slots is empty");
        }

        final Set<ReservationSlot> reservationList = TimeSlotUtil.parseTimeSlot(timeSlotList);

        final BookingInput bookingInput = BookingInput.builder()
                .withReservationSlotList(reservationList)
                .withUserId(userId)
                .withTrainerId(trainerId)
                .withAccount(account)
                .build();

        final List<SessionEntity> sessionEntities = sessionRepository.findAll(doubleSessionWithUserInfoCondition(bookingInput));
        if (!sessionEntities.isEmpty()) {
            BasicUserInfo currentUserInfo = null;
            final Optional<BasicUserEntity> currentUser = basicUserRepository.findOneByUuid(userId);
            if (currentUser.isPresent()) {
                currentUserInfo = BasicUserInfo.convertFromEntity(currentUser.get());
            }

            final Set<ReviewTimeSlot> timeSlots = buildConfirmDoubleBookingList(reservationList, sessionEntities, currentUserInfo);

            return ReviewListResponse.builder().withReviewList(timeSlots).build();
        }

        return ReviewListResponse.builder().withReviewList(Collections.emptySet()).build();
    }

    private Set<ReviewTimeSlot> buildConfirmDoubleBookingList(final Set<ReservationSlot> reservationList,
                                                              final List<SessionEntity> doubleBooking,
                                                              final BasicUserInfo currentUserInfo) {

        final Set<ReviewTimeSlot> timeSlots = new HashSet<>();

        //Add current reservation to review list
        reservationList.forEach(slot -> {

            final boolean isConflict = doubleBooking.stream().anyMatch(session -> checkConflictSession(session, slot));

            final ReviewTimeSlot reviewTimeSlot = ReviewTimeSlot.builder()
                    .withStartTime(ZoneDateTimeUtils.convertToSecondUTC(slot.getStartTime()))
                    .withEndTime(ZoneDateTimeUtils.convertToSecondUTC(slot.getEndTime()))
                    .withConfirmed(false)
                    .withSessionId(slot.getSessionId())
                    .withConflict(isConflict)
                    .withUserInfo(currentUserInfo)
                    .build();

            timeSlots.add(reviewTimeSlot);

        });

        //Add double booking to review list
        doubleBooking.forEach(session -> {
            final ReviewTimeSlot reviewTimeSlot = ReviewTimeSlot.builder()
                    .withStartTime(ZoneDateTimeUtils.convertToSecondUTC(session.getStartTime()))
                    .withEndTime(ZoneDateTimeUtils.convertToSecondUTC(session.getEndTime()))
                    .withConfirmed(session.getStatus() == SessionStatus.CONFIRMED)
                    .withSessionId(session.getUuid())
                    .withUserInfo(BasicUserInfo.convertFromEntity(session.getUser()))
                    .build();

            timeSlots.add(reviewTimeSlot);
        });

        return timeSlots;
    }
}
