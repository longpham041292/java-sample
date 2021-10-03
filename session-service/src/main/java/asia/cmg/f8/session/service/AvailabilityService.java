package asia.cmg.f8.session.service;

import static asia.cmg.f8.common.util.ZoneDateTimeUtils.convertFromUTCToLocalDateTime;
import static java.util.stream.Collectors.toList;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.AvailabilitiesPreferTimeRequest;
import asia.cmg.f8.session.dto.Availability;
import asia.cmg.f8.session.dto.AvailabilityEUResponse;
import asia.cmg.f8.session.dto.AvailabilityRequest;
import asia.cmg.f8.session.dto.TimeSlotEU;
import asia.cmg.f8.session.dto.AvailabilityEU;
import asia.cmg.f8.session.entity.AvailabilityEntity;
import asia.cmg.f8.session.repository.AvailabilityRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.Instant;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created on 11/22/16.
 */
@Service
public class AvailabilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityService.class);

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityService(final AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional(readOnly = true)
    public List<AvailabilityEntity> getAvailabilityByUser(final int month, final int year,
                                                          final String userId) {
        final DateRange range = DateRange.createFrom(year, month);

        LOGGER.info("GET availability for user {} from {} to {}", userId, range.getStartTime(),
                range.getEndTime());

        return availabilityRepository.findAllByUserAtGivenTimeRange(range.getStartTime(),
                range.getEndTime(), userId);
    }

    /**
     * This process will override all existing availabilities of current with
     * new availability list from {@link AvailabilityRequest}
     *
     * @param request
     *            the set availability request
     * @param userId
     *            the user id.
     * @return true if it's success
     */
    @Transactional
    public boolean setAvailabilityByUser(final AvailabilityRequest request, final String userId) {

        // if day off then remove availability for days
        final List<Long> dayoffs = request.getListDayOff();
        if (!dayoffs.isEmpty()) {
            dayoffs.forEach(day -> {
                final LocalDateTime localDay = convertFromUTCToLocalDateTime(day);
                final DateRange range = DateRange.createFrom(localDay.getYear(),
                        localDay.getMonthValue(), localDay.getDayOfMonth());
                final LocalDateTime startTime = range.getStartTime();
                final LocalDateTime endTime = range.getEndTime();

                availabilityRepository.deleteExistingAvailability(startTime, endTime, userId);
            });
            return true;
        }

        if (request.getListAvailability().isEmpty()) {
            return false;
        }
        final int monthOfYear = request.getMonth();
        final int year = request.getYear();

        final List<AvailabilityEntity> availabilities = buildAvailabilityEntities(request, userId);

        // filter date and delete if exist
        final Set<LocalDate> newDates = availabilities.stream()
                .map(availability -> availability.getStartedTime().toLocalDate())
                .collect(Collectors.toSet());

        newDates.forEach(date -> {
            final DateRange range = DateRange.createFrom(year, monthOfYear, date.getDayOfMonth());

            LOGGER.info("SET availability for user {} from {} to {}", userId, range.getStartTime(),
                    range.getEndTime());

            /**
             * Calculate start and end time base on given year and
             * month_of_year. the start time will be first day at 00:00:00. The
             * end time will be last day of month at 23:59:59
             */
            final LocalDateTime startTime = range.getStartTime();
            final LocalDateTime endTime = range.getEndTime();

            availabilityRepository.deleteExistingAvailability(startTime, endTime, userId);
        });

        // then re-create
        return availabilityRepository.save(availabilities).iterator().hasNext();
    }

    static class DateRange {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        private DateRange(final LocalDateTime startTime, final LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public static DateRange createFrom(final int year, final int monthOfYear) {
            final LocalDate startDate = LocalDate.of(year, monthOfYear, 1);
            /**
             * Calculate start and end time base on given year and
             * month_of_year. the start time will be first day at 00:00:00. The
             * end time will be last day of month at 23:59:59
             */
            final LocalDateTime startTime = startDate.atTime(0, 0, 0);
            final LocalDateTime endTime = startDate.with(TemporalAdjusters.lastDayOfMonth())
                    .atTime(23, 59, 59);
            return new DateRange(startTime, endTime);
        }

        public static DateRange createFrom(final int year, final int monthOfYear,
                                           final int dayOfMonth) {
            final LocalDate startDate = LocalDate.of(year, monthOfYear, dayOfMonth);
            /**
             * Calculate start and end time base on given year and
             * month_of_year. the start time will be first day at 00:00:00. The
             * end time will be last day of month at 23:59:59
             */
            final LocalDateTime startTime = startDate.atTime(0, 0, 0);
            final LocalDateTime endTime = startDate.atTime(23, 59, 59);
            return new DateRange(startTime, endTime);
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }
    }

    /**
     * Build list of {@link AvailabilityEntity} base on given
     * {@link AvailabilityRequest}. The start and end time will be converted to
     * local time.
     *
     * @param request
     *            the request
     * @param userUuid
     *            the user's uuid.
     * @return list of {@link AvailabilityEntity}.
     */
    private List<AvailabilityEntity> buildAvailabilityEntities(final AvailabilityRequest request,
                                                               final String userUuid) {
        return request
                .getListAvailability()
                .stream()
                .map(timeSlot -> {
                    final LocalDateTime startDateTime = convertFromUTCToLocalDateTime(timeSlot
                            .getStartTime());
                    final LocalDateTime endDateTime = convertFromUTCToLocalDateTime(timeSlot
                            .getEndTime());
                    final AvailabilityEntity entity = new AvailabilityEntity();
                    entity.setStartedTime(startDateTime);
                    entity.setEndedTime(endDateTime);
                    entity.setUuid(UUID.randomUUID().toString());
                    entity.setUserId(userUuid);
                    return entity;
                }).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<AvailabilityEUResponse> getAvailabilityWithPreferTimeByTrainer(
            final AvailabilitiesPreferTimeRequest preferTime, final String trainerId) {
        if (preferTime == null) {
            throw new IllegalArgumentException("Prefer time are missing for this action.");
        }

        final LocalDate now = LocalDate.now();
        final LocalTime startTime = LocalDateTime.of(now, LocalTime.of(0, 0))
                .plusSeconds(preferTime.getStartTime()).atZone(ZoneOffset.systemDefault()).toLocalTime();
        final LocalTime endTime = LocalDateTime.of(now, LocalTime.of(0, 0)).plusSeconds(preferTime.getEndTime())
                .atZone(ZoneOffset.systemDefault()).toLocalTime();

        final LocalDateTime start ;
        final LocalDateTime end ;

        final Integer year = preferTime.getYear();
        final Integer monthOfYear = preferTime.getMonth();

        if(year != null && monthOfYear !=null ) {
            final DateRange range = DateRange.createFrom(year, monthOfYear);
            start = range.getStartTime();
            end = range.getEndTime();
        }else {
            start = LocalDateTime.ofInstant(Instant.ofEpochSecond(preferTime.getStartDate()), ZoneId.systemDefault());
            end = LocalDateTime.ofInstant(Instant.ofEpochSecond(preferTime.getEndDate()), ZoneId.systemDefault());
        }
        final List<AvailabilityEntity> availabilityEntities = availabilityRepository
                .findAllByUserAtGivenTimeRange(start, end, trainerId);
        final List<AvailabilityEU> availabilityEUList = convertEntityToAvailabilityEU(availabilityEntities);

        /**
         * Group all availabilities by date.
         */
        final Map<LocalDate, List<TimeSlotEU>> availabilityGroupByDate = availabilityEUList
                .stream().collect(
                        Collectors.groupingBy(AvailabilityEU::date,
                                Collectors.mapping(AvailabilityEU::getTimeSlot, toList())));

        return availabilityGroupByDate
                .entrySet()
                .stream()
                .map(entry -> {

                    final LocalDate date = entry.getKey();

                    final List<TimeSlotEU> sortedTimeSlots = entry.getValue().stream()
                            .sorted(Comparator.comparing(TimeSlotEU::startTime)).collect(toList());
                    final List<TimeSlotEU> dayTimeSlots = convertTimeSlot(sortedTimeSlots);

                    if (!dayTimeSlots.isEmpty()) {

                        final List<TimeSlotEU> matched = dayTimeSlots.stream()
                                .filter(slot -> isDesiredTimeRange(slot, startTime, endTime))
                                .collect(toList());

                        if (!matched.isEmpty()) { // it's green
                            final List<Availability> availabilities = convertTimeSlotToAvailability(
                                    date, matched);

                            return AvailabilityEUResponse
                                    .builder()
                                    .withDate(
                                            LocalDateTime.of(date, LocalTime.of(0, 0))
                                                    .toEpochSecond(ZoneOffset.UTC))
                                    .withIsMatchPrefer(true).withAvailability(availabilities)
                                    .build();
                        } else { // it's orange
                            final List<Availability> availabilities = convertTimeSlotToAvailability(
                                    date, dayTimeSlots);
                            return AvailabilityEUResponse
                                    .builder()
                                    .withDate(
                                            LocalDateTime.of(date, LocalTime.of(0, 0))
                                                    .toEpochSecond(ZoneOffset.UTC))
                                    .withIsMatchPrefer(false).withAvailability(availabilities)
                                    .build();
                        }
                    } else {
                        return null;
                    }
                }).filter(list -> !Objects.isNull(list)).collect(Collectors.toList());
    }

    public void setupAvailablityForTrainer(final String userUuid, final LocalDateTime startTime,
                                           final LocalDateTime targetTime) {
        final List<AvailabilityEntity> availabilities = new ArrayList<>();

        LocalDateTime currentTime = startTime;
        while (currentTime.isBefore(targetTime)) {
            final LocalDateTime endSlotTime = currentTime.plusMinutes(30);
            final AvailabilityEntity entity = new AvailabilityEntity();
            entity.setStartedTime(currentTime);
            entity.setEndedTime(endSlotTime);
            entity.setUuid(UUID.randomUUID().toString());
            entity.setUserId(userUuid);
            availabilities.add(entity);
            currentTime = endSlotTime;
        }

        availabilityRepository.save(availabilities);
    }

    /**
     * Re-build time slots at different time-length from half an hour to ONE
     * hour.
     *
     * @param timeSlotEUList
     *            list of time slot with half an hour time-length
     * @return list of time slots with ONE hour time-length
     */
    private List<TimeSlotEU> convertTimeSlot(final List<TimeSlotEU> timeSlotEUList) {
        final List<TimeSlotEU> hourSlot = new ArrayList<>();
        for (int i = 0; i < timeSlotEUList.size() - 1; i++) {
            if (timeSlotEUList.get(i).endTime().compareTo(timeSlotEUList.get(i + 1).startTime()) == 0) {
                hourSlot.add(TimeSlotEU.builder().withStartTime(timeSlotEUList.get(i).startTime())
                        .withEndTime(timeSlotEUList.get(i + 1).endTime()).build());
            }
        }
        return hourSlot;
    }

    /**
     * Convert list of {@link AvailabilityEntity} to {@link AvailabilityEU}.
     *
     * @param availabilityEntities
     *            the availabilities
     * @return list of {@link AvailabilityEU}
     */
    private List<AvailabilityEU> convertEntityToAvailabilityEU(
            final List<AvailabilityEntity> availabilityEntities) {
        return availabilityEntities
                .stream()
                .map(entity -> {
                    final TimeSlotEU timeSlotEU = TimeSlotEU.builder()
                            .withStartTime(entity.getStartedTime().toLocalTime())
                            .withEndTime(entity.getEndedTime().toLocalTime()).build();

                    return AvailabilityEU.builder().withDate(entity.getStartedTime().toLocalDate())
                            .withTimeSlot(timeSlotEU).build();
                }).collect(toList());
    }

    private List<Availability> convertTimeSlotToAvailability(final LocalDate date,
                                                             final List<TimeSlotEU> timeSlotList) {
        final List<Availability> availabilities = new ArrayList<>();
        for (final TimeSlotEU slot : timeSlotList) {
            final Availability availability = Availability
                    .builder()
                    .startDate(
                            ZoneDateTimeUtils.convertToSecondUTC(LocalDateTime.of(date,
                                    slot.startTime())))
                    .endDate(
                            ZoneDateTimeUtils.convertToSecondUTC(LocalDateTime.of(date,
                                    slot.endTime()))).build();
            availabilities.add(availability);
        }
        return availabilities;
    }

    private boolean isDesiredTimeRange(final TimeSlotEU timeSlot, final LocalTime startTime,
                                       final LocalTime endTime) {
        return (timeSlot.startTime().isAfter(startTime.minusMinutes(1)) || LocalTime.MIDNIGHT
                .equals(startTime)) && timeSlot.endTime().isBefore(endTime.plusMinutes(1));
    }

}
