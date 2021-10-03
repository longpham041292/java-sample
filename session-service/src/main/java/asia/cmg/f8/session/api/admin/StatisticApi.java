package asia.cmg.f8.session.api.admin;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.session.dto.BasicTrainerEarningStatisticDto;
import asia.cmg.f8.session.dto.SpendingStatisticDTO;
import asia.cmg.f8.session.entity.BookingServiceType;
import asia.cmg.f8.session.internal.service.CreditSessionBookingService;
import asia.cmg.f8.session.service.CreditBookingService;
import asia.cmg.f8.session.service.UserService;

/**
 * 
 * @author Phong Khac
 *
 */
@RestController
public class StatisticApi {
	
    private static final String START_TIME = "from";
    private static final String END_TIME = "to";
    private final static String FULL_NAME = "full_name";
    private final static String KEYWORD = "keyword";
    
	@Autowired
    private UserService userService;
	
	@Autowired
	private CreditSessionBookingService bookingSessionService;
	
	@Autowired
	private CreditBookingService bookingService;
	
	@RequestMapping(value = "/admin/v1/statistic/users/active/count", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<?> getTotalActiveUser(@RequestParam(name = START_TIME) final Long startTime,
												@RequestParam(name = END_TIME) final Long endTime,
												@RequestParam(name = "userType") final String userType) {
		int total = 0;
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), TimeZone.getDefault().toZoneId());
		
		if(UserType.EU.name().equalsIgnoreCase(userType) || UserType.PT.name().equalsIgnoreCase(userType) ) {
			total = userService.countActiveUser(userType, start, end);
		}
		return new ResponseEntity<>(Collections.singletonMap("total", total), HttpStatus.OK);
	}

	/**
	 * total user in the app filter by user type (PT, EU) in a range time
	 * @author phong
	 * @param startTime
	 * @param endTime
	 * @param userType
	 * @return
	 */
	@RequestMapping(value = "/admin/v1/statistic/users/registered/count", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<?> countTotalUserByRangeTime(@RequestParam(name = START_TIME) final Long startTime,
													@RequestParam(name = END_TIME) final Long endTime,
													@RequestParam(name = "userType") final String userType) {
		int total = 0;
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		if(UserType.EU.name().equalsIgnoreCase(userType) || UserType.PT.name().equalsIgnoreCase(userType) ) {
			total = userService.countTotalUserByRangeTime(userType, start, end);
		}
		return new ResponseEntity<>(Collections.singletonMap("total", total), HttpStatus.OK);
	}

	/**
	 * total user in the app filter by user type (PT, EU)
	 * @author phong
	 * @param userType
	 * @return
	 */
	@RequestMapping(value= "/admin/v1/statistic/users/count", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> countTotalUser(@RequestParam(name = "userType") final String userType){ 
		int total = 0;
		if(UserType.EU.name().equalsIgnoreCase(userType) || UserType.PT.name().equalsIgnoreCase(userType) ) {
			total = userService.countTotalUser(userType);
		}
		return new ResponseEntity<>(Collections.singletonMap("total", total), HttpStatus.OK);
	}
	/**
	 * total PT has been approved in range time
	 * @author phong
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value= "/admin/v1/statistic/users/approved/count", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> countApprovedTrainerByRange(@RequestParam(name = START_TIME) final Long startTime,
															@RequestParam(name = END_TIME) final Long endTime){ 
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		final int total = userService.countApprovedTrainerByRange(start, end);
		return new ResponseEntity<>(Collections.singletonMap("total", total), HttpStatus.OK);
	}
	/**
	 * Total PT has booking in range time
	 * @author phong
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value= "/admin/v1/statistic/users/booked/count", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> countTotalPtHasBookingByRange(@RequestParam(name = START_TIME) final Long startTime,
															@RequestParam(name = END_TIME) final Long endTime){ 
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		final int total = userService.countTotalPtHasBookingByRange(start, end);
		return new ResponseEntity<>(Collections.singletonMap("total", total), HttpStatus.OK);
	}
	/**
	 * Session booking statistic report 
	 * @author phong
	 * @param startTime
	 * @param endTime
	 * @return completed, burned, canncelled
	 */
	@RequestMapping(value= "/admin/v1/statistic/sessions/booking", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> getNumbersOfBookingByRange(@RequestParam(name = START_TIME) final Long startTime,
															@RequestParam(name = END_TIME) final Long endTime){ 
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		HashMap<String, BigInteger> responseData = bookingSessionService.countStatusBookingByRange(start, end);
		return new ResponseEntity<>(responseData, HttpStatus.OK);
	}
	
	/**
	 * Booking CANCELLED report statistic
	 * @author phong
	 * @param startTime
	 * @param endTime
	 * @param serviceType 0,1,2
	 * @return
	 */
	@RequestMapping(value= "/admin/v1/statistic/booking/cancelled", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> countCancelledBookingByRange(@RequestParam(name = START_TIME) final Long startTime,
															@RequestParam(name = END_TIME) final Long endTime, 
															@RequestParam(name = "serviceType") final Integer serviceType){ 
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		HashMap<String, Long> responseData = bookingSessionService.countCancelledBookingByRange(start, end, serviceType);
		return new ResponseEntity<>(responseData, HttpStatus.OK);
	}
	
	/**
	 * Chart to show LEEP coins earning by weekly, Monthly, yearly
	 * @author phong
	 * @param ptUuid
	 * @param startTime
	 * @param endTime
	 * @return List<Time, amount>
	 */
	@RequestMapping(value = "/admin/v1/statistic/trainers/{ptUuid}/earning", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> getTrainerEarningByRangeTime(@PathVariable("ptUuid") String ptUuid,
															@RequestParam(name = START_TIME) final Long startTime,
															@RequestParam(name = END_TIME) final Long endTime) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		
		final List<BasicTrainerEarningStatisticDto> earningData = bookingSessionService.getAllCreditSesionsBookingOfTrainer(start, end, ptUuid);
		
		return new ResponseEntity<>(earningData, HttpStatus.OK);
	}
	
	/**
	 * Get statistic info: New clients, session completed, total LEEP coins earn
	 * @author phong
	 * @param ptUuid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value = "/admin/v1/statistic/trainers/{ptUuid}/sessions", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> getTrainerStatisticOverview(@PathVariable("ptUuid") String ptUuid,
															@RequestParam(name = START_TIME) final Long startTime,
															@RequestParam(name = END_TIME) final Long endTime) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		
		final HashMap<String, Integer> trainerStatistic = bookingSessionService.getTrainerStatisticOverview(start, end, ptUuid);
		
		return new ResponseEntity<>(trainerStatistic, HttpStatus.OK);
	}
	/**
	 * Description: Get all number of used | not used | cancelled of E_Ticket and Class 
	 * @author phong
	 * @param startTime
	 * @param endTime
	 * @param serviceType
	 * @return
	 */
	@RequestMapping(value = "/admin/v1/statistic/bookings/overview", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> getNumberOfClubAccess(@RequestParam(name = START_TIME) final Long startTime,
												@RequestParam(name = END_TIME) final Long endTime,
												@RequestParam(name = "serviceType") final Integer serviceType) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		
		HashMap<String, BigInteger> responseData = new HashMap<String, BigInteger>();
		if(serviceType == BookingServiceType.ETICKET.ordinal() || serviceType == BookingServiceType.CLASS.ordinal()) {
			responseData = bookingService.getStatisticNumberClubAndEticket(start, end, serviceType);
		}
		return new ResponseEntity<>(responseData, HttpStatus.OK);
	}
	/** 
	 * Description: List date and credit amount of the user spending
	 * @author phong
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	
	@RequestMapping(value = "/admin/v1/statistic/bookings/users/{uuid}/spending", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> getSpendingHistoryByUuid(@PathVariable("uuid") String uuid,
												@RequestParam(name = START_TIME) final Long startTime,
												@RequestParam(name = END_TIME) final Long endTime) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		List<SpendingStatisticDTO> responseData = bookingService.getSpendingHistory(start, end, uuid);
		return new ResponseEntity<>(responseData, HttpStatus.OK);
	}
	/**
	 * Description: get number club and class of used and not used of the user
	 * @author phong
	 * @param uuid userUuid
	 * @param startTime
	 * @param endTime
	 * @param serviceType 1: eticket, 2: class
	 * @return
	 */
	
	@RequestMapping(value = "/admin/v1/statistic/bookings/users/{uuid}/overview", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> getStatisticNumberClubAndEticketByUuid(@PathVariable("uuid") String uuid,
												@RequestParam(name = START_TIME) final Long startTime,
												@RequestParam(name = END_TIME) final Long endTime,
												@RequestParam(name = "serviceType") final Integer serviceType) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		
		HashMap<String, BigInteger> responseData = new HashMap<String, BigInteger>();
		if(serviceType == BookingServiceType.ETICKET.ordinal() || serviceType == BookingServiceType.CLASS.ordinal()) {
			responseData = bookingService.getStatisticNumberClubAndEticket(start, end, serviceType, uuid);
		}
		return new ResponseEntity<>(responseData, HttpStatus.OK);
	}
	/**
	 * Description: get number club and class has booking in range time of system
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value = "/admin/v1/statistic/bookings/clubs/active/count", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> getTotalClubHasBooking(@RequestParam(name = START_TIME) final Long startTime,
													@RequestParam(name = END_TIME) final Long endTime) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		
		
		Integer total = bookingService.getTotalClubHasBooking(start, end);
		return new ResponseEntity<>(Collections.singletonMap("total", total), HttpStatus.OK);
	}
}
