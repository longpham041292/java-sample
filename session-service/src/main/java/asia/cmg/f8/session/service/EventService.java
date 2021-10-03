package asia.cmg.f8.session.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.BasicUserInfo;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.dto.EventsResponse;
import asia.cmg.f8.session.dto.SessionPackageInfo;
import asia.cmg.f8.session.dto.TimeSlot;
import asia.cmg.f8.session.entity.ScheduleEvent;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.entity.SessionType;
import asia.cmg.f8.session.repository.EventRepository;
import asia.cmg.f8.session.repository.ScheduleEventRepository;

/**
 * Created on 12/12/16.
 */
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ScheduleEventRepository scheduleEventRepository;

    public EventService(final EventRepository eventRepository,
            final ScheduleEventRepository scheduleEventRepository) {
        this.eventRepository = eventRepository;
        this.scheduleEventRepository = scheduleEventRepository;
    }

    @SuppressWarnings("PMD.NcssMethodCount")
    public List<EventsResponse> getAllEventByUser(final String userId, final LocalDateTime start,
                                                  final LocalDateTime end, final UserType userType,
                                                  final String version) {
        final List<Object[]> result = (userType == UserType.EU) ? eventRepository
                .getAllEventByEndUser(userId, start, end) : eventRepository.getAllEventByTrainer(
                userId, start, end);

        final BiPredicate<SessionStatus,LocalDateTime> biPredicateStatus = (sessionStatus, startTime) ->{
                boolean isPendingOldEvent = !(SessionStatus.PENDING.equals(sessionStatus) && startTime
                        .compareTo(LocalDateTime.now()) < 0);
                return isPendingOldEvent
                       && !SessionStatus.CANCELLED.equals(sessionStatus)
                       && !SessionStatus.TRAINER_CANCELLED.equals(sessionStatus);
                };
        final BiPredicate<SessionStatus,SessionStatus> checkConsistent = (sessionStatus, eventStatus)
                                            -> !(SessionStatus.OPEN.equals(sessionStatus)
                                                 && SessionStatus.PENDING.equals(eventStatus));

        final List<EventsResponse> eventList = result
                .stream()
                .filter(session -> {
                    final SessionStatus eventStatus = SessionStatus.valueOf(String
                            .valueOf(session[5]));
                    final SessionStatus sessionStatus = SessionStatus.valueOf(String
                            .valueOf(session[18]));
                    final LocalDateTime startTime = (LocalDateTime) session[3];
                    return biPredicateStatus.test(eventStatus,startTime) && checkConsistent.test(sessionStatus,eventStatus);
                })
                .map(session -> {

                    final EventsResponse sessionInfo = new EventsResponse();
                    sessionInfo.setEventId((String) session[16]);
                    sessionInfo.setBookedBy((String) session[17]);
                    sessionInfo.setSessionId((String) session[2]);
                    sessionInfo.setStatus(SessionStatus
                            .mappingClientCancelledSessionStatus(SessionStatus.valueOf(String
                                    .valueOf(session[5]))));

                    final TimeSlot timeSlot = new TimeSlot();

                    final LocalDateTime startTime = (LocalDateTime) session[3];
                    timeSlot.setStartTime(ZoneDateTimeUtils.convertToSecondUTC(startTime));

                    final LocalDateTime endTime = (LocalDateTime) session[4];
                    timeSlot.setEndTime(ZoneDateTimeUtils.convertToSecondUTC(endTime));

                    sessionInfo.setTimeSlot(timeSlot);

                    final BasicUserInfo basicUserEntity = new BasicUserInfo();
                    basicUserEntity.setAvatar((String) session[6]);
                    basicUserEntity.setUuid((String) session[0]);
                    basicUserEntity.setEmail((String) session[7]);
                    basicUserEntity.setName((String) session[8]);
                    basicUserEntity.setPhone((String) session[13]);
                    sessionInfo.setEndUser(basicUserEntity);

                    final BasicUserInfo basicTrainerEntity = new BasicUserInfo();
                    basicTrainerEntity.setAvatar((String) session[9]);
                    basicTrainerEntity.setUuid((String) session[1]);
                    basicTrainerEntity.setEmail((String) session[10]);
                    basicTrainerEntity.setName((String) session[11]);
                    basicTrainerEntity.setPhone((String) session[12]);
                    sessionInfo.setTrainer(basicTrainerEntity);

                    final SessionPackageInfo sessionPackageInfo = new SessionPackageInfo();
                    sessionPackageInfo.setSessionBurned((Integer) session[14]);
                    sessionPackageInfo.setSessionNumber((Integer) session[15]);
                    sessionInfo.setPackageInfo(sessionPackageInfo);

                    sessionInfo.setSessionType(SessionType.BOOKING);
                    if(session[19] != null) {
                        final LocalDateTime expiredDate = (LocalDateTime) session[19];
                        sessionInfo.setExpiredDate(ZoneDateTimeUtils.convertToSecondUTC(expiredDate));     	
                    }
                    
                    String bookingClubUuid = (String)session[20];
                	final ClubDto clubInfo = new ClubDto.Builder()
			        						.uuid(bookingClubUuid)
			        						.name((String)session[21])
			        						.address((String)session[22])
			        						.build();
            		sessionInfo.setClubInfo(clubInfo);
                    
                    String checkinClubUuid = (String)session[23];
                    if(!Objects.isNull(checkinClubUuid) && !checkinClubUuid.isEmpty()) {
                    	ClubDto checkinClub = new ClubDto.Builder()
			    							.uuid(checkinClubUuid)
			    							.name((String)session[24])
			    							.address((String)session[25])
			    							.build();
                		sessionInfo.setCheckinClub(checkinClub);
                    }
                    
                    return sessionInfo;
                }).collect(Collectors.toList());

        if (!StringUtils.isBlank(version)) {
            final List<EventsResponse> scheduleEventList = getScheduleEvents(userId, start, end);
            eventList.addAll(scheduleEventList);
        }

        eventList.sort(new Comparator<EventsResponse>() {
            @Override
            public int compare(final EventsResponse er1, final EventsResponse er2) {
                return er1.getTimeSlot().getStartTime().compareTo(er2.getTimeSlot().getStartTime());
            }
        });

        return eventList;
    }
    
    public void updateSessionEventTime(@Param("sessionEventUuid") final String sessionEventUuid, @Param("startTime") final LocalDateTime startTime, @Param("endTime") final LocalDateTime endTime) {
    	this.eventRepository.updateSessionEventTime(sessionEventUuid, startTime, endTime);
    }

    private List<EventsResponse> getScheduleEvents(final String userId, final LocalDateTime start,
            final LocalDateTime end) {
        final List<ScheduleEvent> scheduleEvents = scheduleEventRepository.findEventsInTimeRange(
                userId, start, end);

        return scheduleEvents
                .stream()
                .map(scheduleEvent -> {
                    final EventsResponse eventInfo = new EventsResponse();
                    eventInfo.setEventId(scheduleEvent.getUuid());

                    final TimeSlot timeSlot = new TimeSlot();
                    timeSlot.setStartTime(ZoneDateTimeUtils.convertToSecondUTC(scheduleEvent
                            .getStartedTime()));
                    timeSlot.setEndTime(ZoneDateTimeUtils.convertToSecondUTC(scheduleEvent
                            .getEndedTime()));
                    eventInfo.setTimeSlot(timeSlot);
                    eventInfo.setTitle(scheduleEvent.getTitle());
                    eventInfo.setSessionType(SessionType.PERSONAL_TIME);
                    return eventInfo;
                }).collect(Collectors.toList());
    }
}
