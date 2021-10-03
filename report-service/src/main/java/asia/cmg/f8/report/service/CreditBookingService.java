package asia.cmg.f8.report.service;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import asia.cmg.f8.report.dto.BasicUserInfo;
import asia.cmg.f8.report.dto.BookingHistoryDTO;
import asia.cmg.f8.report.dto.BookingServiceDTO;
import asia.cmg.f8.report.dto.CreditBookingResponse;
import asia.cmg.f8.report.dto.CreditBookingSessionStatus;
import asia.cmg.f8.report.dto.CreditClassBookingResponse;
import asia.cmg.f8.report.dto.CreditETicketBookingResponse;
import asia.cmg.f8.report.dto.CreditSessionBookingResponseDTO;
import asia.cmg.f8.report.dto.PageResponse;
import asia.cmg.f8.report.dto.SpendingStatisticDTO;
import asia.cmg.f8.report.entity.database.BasicUserEntity;
import asia.cmg.f8.report.entity.database.BookingServiceType;
import asia.cmg.f8.report.entity.database.CreditBookingEntity;
import asia.cmg.f8.report.entity.database.CreditSessionBookingEntity;
import asia.cmg.f8.report.repository.BasicUserRepository;
import asia.cmg.f8.report.repository.CreditBookingRepository;

@Service
public class CreditBookingService {

	private static final Logger LOG = LoggerFactory.getLogger(CreditBookingService.class);

	@Autowired
	private CreditBookingRepository creditBookingRepo;
	
	@Autowired
	private BasicUserRepository userRepo;

	public final List<Integer> completedCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED.ordinal(),
			CreditBookingSessionStatus.BURNED.ordinal());
	public final List<Integer> bookedCode = Arrays.asList(CreditBookingSessionStatus.BOOKED.ordinal());
	public final List<Integer> cancelledCode = Arrays.asList(CreditBookingSessionStatus.CANCELLED.ordinal(),
			CreditBookingSessionStatus.EU_CANCELLED.ordinal(), CreditBookingSessionStatus.TRAINER_CANCELLED.ordinal());

	public HashMap<String, BigInteger> getStatisticNumberClubAndEticket(LocalDateTime start, LocalDateTime end,
			int serviceType) {
		HashMap<String, BigInteger> map = new HashMap<String, BigInteger>();

		if (serviceType == BookingServiceType.ETICKET.ordinal() || serviceType == BookingServiceType.CLASS.ordinal()) {
			map.put("not_used",
					creditBookingRepo.countTotalClubAndClassByStatusCode(start, end, serviceType, this.bookedCode));
			map.put("used",
					creditBookingRepo.countTotalClubAndClassByStatusCode(start, end, serviceType, this.completedCode));
			map.put("cancelled",
					creditBookingRepo.countTotalClubAndClassByStatusCode(start, end, serviceType, this.cancelledCode));
		}
		return map;
	}

	public List<SpendingStatisticDTO> getSpendingHistory(LocalDateTime start, LocalDateTime end, String uuid) {
		List<SpendingStatisticDTO> dto = creditBookingRepo.getSpendingStatistic(start, end, uuid);
		return dto;
	}

	public HashMap<String, BigInteger> getStatisticNumberClubAndEticket(LocalDateTime start, LocalDateTime end,
			Integer serviceType, String uuid) {
		HashMap<String, BigInteger> map = new HashMap<String, BigInteger>();

		if (serviceType == BookingServiceType.ETICKET.ordinal() || serviceType == BookingServiceType.CLASS.ordinal()) {
			map.put("not_used", creditBookingRepo.countTotalClubAndClassByStatusCodeAndUuid(start, end, serviceType,
					this.bookedCode, uuid));
			map.put("used", creditBookingRepo.countTotalClubAndClassByStatusCodeAndUuid(start, end, serviceType,
					this.completedCode, uuid));
			map.put("cancelled", creditBookingRepo.countTotalClubAndClassByStatusCodeAndUuid(start, end, serviceType,
					this.cancelledCode, uuid));
		}
		return map;
	}

	/**
	 * Get total club has booking in the range time
	 * 
	 * @author phong
	 * @param start
	 * @param end
	 * @return
	 */
	public Integer getTotalClubHasBooking(LocalDateTime start, LocalDateTime end) {

		return creditBookingRepo.getTotalClubHasBooking(start, end);
	}
	
	/**
	 * Get scheduled by uuid
	 * @author phong
	 * @param uuid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<CreditBookingResponse<?>> getScheduledBookingsByUuuid(String uuid, Long startTime, Long endTime) {
		List<CreditBookingResponse<?>> result = new ArrayList<CreditBookingResponse<?>>();
		try {
			//LocalDate fromDate = SessionUtil.convertToLocalDate(startTime);
			LocalDate fromDate  = Instant.ofEpochSecond(startTime ).atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = Instant.ofEpochSecond(endTime ).atZone(ZoneId.systemDefault()).toLocalDate();

			List<CreditBookingEntity> bookingEntities = new ArrayList<CreditBookingEntity>();
			BasicUserEntity user = userRepo.findOneByUuid(uuid).get();
			if (user.getUserType().equalsIgnoreCase("PT")) {
				bookingEntities = creditBookingRepo.getPTScheduledBookings(fromDate, toDate, uuid);
			} else {
				bookingEntities = creditBookingRepo.getEUScheduledBookings(fromDate, toDate, uuid);
			}

			for (CreditBookingEntity creditBookingEntity : bookingEntities) {
				switch (creditBookingEntity.getBookingType()) {
				case CLASS:
					CreditBookingResponse<CreditClassBookingResponse> classBookingResponse = new CreditBookingResponse<CreditClassBookingResponse>();
					CreditClassBookingResponse classBooking = new CreditClassBookingResponse(creditBookingEntity);
					classBookingResponse.setServiceType(BookingServiceType.CLASS);
					classBookingResponse.setBookingData(classBooking);

					result.add(classBookingResponse);
					break;
				case ETICKET:
					CreditBookingResponse<CreditETicketBookingResponse> eticketBookingResponse = new CreditBookingResponse<CreditETicketBookingResponse>();
					CreditETicketBookingResponse eticketBooking = new CreditETicketBookingResponse(creditBookingEntity);
					eticketBookingResponse.setServiceType(BookingServiceType.ETICKET);
					eticketBookingResponse.setBookingData(eticketBooking);

					result.add(eticketBookingResponse);
					break;
				case SESSION:
					BasicUserInfo userInfo = null;
					BasicUserInfo trainerInfo = null;
					CreditSessionBookingEntity session = creditBookingEntity.getSessions().get(0);
					BasicUserEntity userEntity = userRepo.findOneByUuid(session.getUserUuid()).get();
					if (userEntity != null) {
						userInfo = BasicUserInfo.convertFromEntity(userEntity);
					}
					BasicUserEntity trainerEntity = userRepo.findOneByUuid(session.getPtUuid()).get();
					if (trainerEntity != null) {
						trainerInfo = BasicUserInfo.convertFromEntity(trainerEntity);
					}
					CreditSessionBookingResponseDTO sessionBookingDTO = new CreditSessionBookingResponseDTO(
							creditBookingEntity, userInfo, trainerInfo);
					CreditBookingResponse<CreditSessionBookingResponseDTO> sessionBookingResponse = new CreditBookingResponse<CreditSessionBookingResponseDTO>();
					sessionBookingResponse.setServiceType(BookingServiceType.SESSION);
					sessionBookingResponse.setBookingData(sessionBookingDTO);

					result.add(sessionBookingResponse);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return result;
	}
	

	public PageResponse<BookingHistoryDTO> getBookingsHistory(final String uuid, final long fromDate, final long toDate,
			String partnerName, int page, int perPage) {
		final LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(fromDate),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(toDate),
				TimeZone.getDefault().toZoneId());
		List<CreditBookingSessionStatus> statuses = new ArrayList<CreditBookingSessionStatus>();
		statuses.add(CreditBookingSessionStatus.values()[3]);
		statuses.add(CreditBookingSessionStatus.values()[7]);
		Pageable pageable = new PageRequest(page, perPage);
		boolean isPTaccount = checkPTAccount(uuid);
		List<BookingServiceType> bookingType = new ArrayList<BookingServiceType>();
		if (isPTaccount) {
			bookingType.add(BookingServiceType.SESSION);
		} else {
			bookingType.add(BookingServiceType.SESSION);
			bookingType.add(BookingServiceType.CLASS);
			bookingType.add(BookingServiceType.ETICKET);
		}
		Page<BookingHistoryDTO> entitiesPage = partnerName == null
				? creditBookingRepo.getServiceHistoy(uuid, from, to, statuses, bookingType, pageable)
				: creditBookingRepo.getServiceHistoyByName(uuid, from, to, statuses, bookingType, partnerName, pageable);
		List<BookingHistoryDTO> result = entitiesPage.getContent();
		PageResponse<BookingHistoryDTO> pageResponse = new PageResponse<>();
		pageResponse.setEntities(result);
		pageResponse.setCount(entitiesPage.getTotalElements());
		return pageResponse;
	}
	private boolean checkPTAccount(final String uuid) {
		Optional<BasicUserEntity> basicUserEntity = userRepo.findOneByUuid(uuid);
		BasicUserEntity userEntity = basicUserEntity.get();
		return userEntity.getUserType().equalsIgnoreCase("pt");
	}
	
	public List<BookingServiceDTO> eticketBookingService(String keyword, LocalDateTime start, LocalDateTime end,
			List<Integer> status, List<BookingServiceType> type , Pageable pageable) {
		List<CreditBookingSessionStatus> statusEnum = new ArrayList<CreditBookingSessionStatus>();
		status.forEach(el-> {
			statusEnum.add(CreditBookingSessionStatus.values()[el]);
		});
		return creditBookingRepo.eticketBookingManagement(keyword, start, end, statusEnum, type, pageable);
	}

}
