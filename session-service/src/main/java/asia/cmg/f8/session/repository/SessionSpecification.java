package asia.cmg.f8.session.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import asia.cmg.f8.session.dto.ReservationSlot;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.rule.booking.BookingInput;

/**
 * Created on 12/10/16.
 */
public final class SessionSpecification {

    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String TRAINER_ID = "ptUuid";
    private static final String USER_ID = "userUuid";
    private static final String STATUS = "status";
    private static final String UUID = "uuid";

    private SessionSpecification() {
    }

    public static Specification<SessionEntity> doubleSessionCondition(final BookingInput bookingInput) {
        return (root, query, builder) -> {

            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get(TRAINER_ID), bookingInput.getTrainerId()));
            predicates.add(root.get(STATUS).in(SessionStatus.getReviewSessionStatus()));

            if (bookingInput.getReservationSlotList().size() == 1) {
                final ReservationSlot reservationSlot = bookingInput.getReservationSlotList()
                        .iterator().next();
                if (reservationSlot.getSessionId() != null
                        && reservationSlot.getConfirmed() == null) {
                    predicates
                            .add(builder.notEqual(root.get(UUID), reservationSlot.getSessionId()));
                }
            }


            final List<Predicate> datePredicates = new ArrayList<>();
            bookingInput.getReservationSlotList().forEach(slot -> {
                datePredicates.add(builder.between(
                        builder.literal(slot.getStartTime().plusMinutes(1)),
                        root.get(START_TIME),
                        root.get(END_TIME)));

                datePredicates.add(builder.between(
                        builder.literal(slot.getEndTime().minusMinutes(1)),
                        root.get(START_TIME),
                        root.get(END_TIME)));
            });
            return builder.and(builder.and(predicates.toArray(new Predicate[predicates.size()])), builder.or(datePredicates.toArray(new Predicate[datePredicates.size()])));
        };
    }

    public static Specification<SessionEntity> doubleSessionWithUserInfoCondition(final BookingInput bookingInput) {
        return (root, query, builder) -> {

            root.fetch("user", JoinType.LEFT);

            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get(TRAINER_ID), bookingInput.getTrainerId()));
            predicates.add(root.get(STATUS).in(SessionStatus.getReviewSessionStatus()));


            if (bookingInput.getReservationSlotList().size() == 1) {
                final ReservationSlot reservationSlot = bookingInput.getReservationSlotList()
                        .iterator().next();
                if (reservationSlot.getSessionId() != null
                        && reservationSlot.getConfirmed() == null) {
                    predicates
                            .add(builder.notEqual(root.get(UUID), reservationSlot.getSessionId()));
                }
            }

            final List<Predicate> datePredicates = new ArrayList<>();
            bookingInput.getReservationSlotList().forEach(slot -> {
                datePredicates.add(builder.between(
                        builder.literal(slot.getStartTime().plusMinutes(1)),
                        root.get(START_TIME),
                        root.get(END_TIME)));

                datePredicates.add(builder.between(
                        builder.literal(slot.getEndTime().minusMinutes(1)),
                        root.get(START_TIME),
                        root.get(END_TIME)));
            });

            return builder.and(
                    builder.and(predicates.toArray(new Predicate[predicates.size()])),
                    builder.or(datePredicates.toArray(new Predicate[datePredicates.size()])));
        };
    }

    public static Specification<SessionEntity> overlapWithBookedSessionCondition(final BookingInput bookingInput) {
        return (root, query, builder) -> {

            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get(USER_ID), bookingInput.getUserId()));
            predicates.add(root.get(STATUS).in(Arrays.asList(SessionStatus.PENDING, SessionStatus.CONFIRMED, SessionStatus.COMPLETED, SessionStatus.BURNED)));

            final List<Predicate> datePredicates = new ArrayList<>();
            bookingInput.getReservationSlotList().stream()
                    .filter(slot ->
                            Objects.isNull(slot.getConfirmed()) || (
                                    Objects.isNull(slot.getSessionId()) &&
                                            !Objects.isNull(slot.getConfirmed()) &&
                                            slot.getConfirmed())
                    )
                    .forEach(slot -> {
                        datePredicates.add(builder.between(
                                builder.literal(slot.getStartTime().plusMinutes(1)),
                                root.get(START_TIME),
                                root.get(END_TIME)));

                        datePredicates.add(builder.between(
                                builder.literal(slot.getEndTime().minusMinutes(1)),
                                root.get(START_TIME),
                                root.get(END_TIME)));
                    });

            return builder.and(
                    builder.and(predicates.toArray(new Predicate[predicates.size()])),
                    builder.or(datePredicates.toArray(new Predicate[datePredicates.size()])));
        };
    }

}
