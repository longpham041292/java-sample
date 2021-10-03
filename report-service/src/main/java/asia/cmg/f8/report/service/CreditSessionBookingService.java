package asia.cmg.f8.report.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import asia.cmg.f8.report.dto.BasicTrainerEarningStatisticDto;
import asia.cmg.f8.report.dto.BookingServiceDTO;
import asia.cmg.f8.report.dto.CreditBookingSessionStatus;
import asia.cmg.f8.report.dto.PTClientsDTO;
import asia.cmg.f8.report.dto.PageResponse;
import asia.cmg.f8.report.entity.database.CreditSessionBookingEntity;
import asia.cmg.f8.report.repository.CreditSessionBookingRepository;

@Service
public class CreditSessionBookingService {

	private static final Logger LOG = LoggerFactory.getLogger(CreditSessionBookingService.class);
	private static final String SESSION_COMPLETED = "session_completed";
	private static final String TOTAL_LEEP_COIN = "total_leep_coin";

	@Autowired
	private CreditSessionBookingRepository sessionBookingRepo;

	public List<BasicTrainerEarningStatisticDto> getAllCreditSesionsBookingOfTrainer(LocalDateTime start,
			LocalDateTime end, String ptUuid) {
		List<CreditBookingSessionStatus> completedCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED,
				CreditBookingSessionStatus.BURNED);
		final List<CreditSessionBookingEntity> result = sessionBookingRepo
				.findByStartTimeBetweenAndPtUuidAndStatusInOrderByStartTime(start, end, ptUuid, completedCode);
		final List<BasicTrainerEarningStatisticDto> stats = new ArrayList<BasicTrainerEarningStatisticDto>();

		result.stream().forEach(entry -> {
			final Long time = entry.getCreatedDate().atZone(ZoneId.systemDefault()).toEpochSecond();
			BasicTrainerEarningStatisticDto dto = new BasicTrainerEarningStatisticDto(time, entry.getCreditAmount());
			stats.add(dto);
		});
		return stats;
	}

	public HashMap<String, BigInteger> countStatusBookingByRange(LocalDateTime start, LocalDateTime end) {
		List<Integer> statusCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED.ordinal(),
				CreditBookingSessionStatus.BURNED.ordinal(), CreditBookingSessionStatus.CANCELLED.ordinal());
		List<Object[]> dataCouting = sessionBookingRepo.countStatusBookingByRange(start, end, statusCode);

		HashMap<String, BigInteger> map = new HashMap<String, BigInteger>();
		map.put(CreditBookingSessionStatus.COMPLETED.name().toLowerCase(), BigInteger.valueOf(0));
		map.put(CreditBookingSessionStatus.BURNED.name().toLowerCase(), BigInteger.valueOf(0));
		map.put(CreditBookingSessionStatus.CANCELLED.name().toLowerCase(), BigInteger.valueOf(0));
		for (Object[] row : dataCouting) {
			final BigInteger count = (BigInteger) row[1];

			final Integer status = (Integer) row[0];
			if (status == CreditBookingSessionStatus.COMPLETED.ordinal()) {
				map.replace(CreditBookingSessionStatus.COMPLETED.name().toLowerCase(), count);
			} else if (status == CreditBookingSessionStatus.BURNED.ordinal()) {
				map.replace(CreditBookingSessionStatus.BURNED.name().toLowerCase(), count);
			} else if (status == CreditBookingSessionStatus.CANCELLED.ordinal()) {
				map.replace(CreditBookingSessionStatus.CANCELLED.name().toLowerCase(), count);
			}
		}

		return map;
	}

	public HashMap<String, Integer> getTrainerStatisticOverview(LocalDateTime start, LocalDateTime end, String ptUuid) {
		List<Integer> completedCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED.ordinal(),
				CreditBookingSessionStatus.BURNED.ordinal());
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("new_clients", 0);
		map.put("completed", 0);
		map.put("earning", 0);
		Integer newClient = sessionBookingRepo.getStatsNewClients(start, end, ptUuid);
		map.put("new_clients", newClient);
		List<Object[]> rowsData = sessionBookingRepo.getStatsTotalCompletedSession(start, end, ptUuid, completedCode);
		Object[] row = rowsData.get(0);
		if (row[0] != null) {
			Integer completed = ((BigInteger) row[0]).intValue();
			map.replace("completed", completed);
		}
		if (row[1] != null) {
			Integer earning = ((BigDecimal) row[1]).intValue();
			map.replace("earning", earning);
		}

		return map;
	}

	public HashMap<String, Long> countCancelledBookingByRange(LocalDateTime start, LocalDateTime end,
			Integer serviceType) {
		List<Integer> cancelledCode = Arrays.asList(CreditBookingSessionStatus.TRAINER_CANCELLED.ordinal(),
				CreditBookingSessionStatus.EU_CANCELLED.ordinal(), CreditBookingSessionStatus.CANCELLED.ordinal());
		List<Object[]> dataRaws = sessionBookingRepo.countCancelledSessionsBookingByRange(start, end, cancelledCode,
				serviceType);
		HashMap<String, Long> map = new HashMap<String, Long>();
		if (!dataRaws.isEmpty()) {
			for (Object[] row : dataRaws) {
				final long count = ((BigInteger) row[0]).longValue();
				final long totalCoint = ((BigDecimal) row[1]).longValue();
				map.put("count", count);
				map.put("total_coins", totalCoint);
			}
		} else {
			map.put("count", Long.valueOf(0));
			map.put("total_coins", Long.valueOf(0));
		}
		return map;
	}

	public List<BookingServiceDTO> sessionBookingManagement(String keyword, LocalDateTime start, LocalDateTime end, List<Integer> status,  Pageable pageable) {
		List<CreditBookingSessionStatus> statusEnum = new ArrayList<CreditBookingSessionStatus>();
		status.forEach(el-> {
			statusEnum.add(CreditBookingSessionStatus.values()[el]);
		});
		return sessionBookingRepo.sessionBookingManagement(keyword, start, end, statusEnum, pageable);
	}
	

	public PageResponse<PTClientsDTO> getPTClients(final String uuid, String filter, int page, int perPage) {
		List<PTClientsDTO> result = new ArrayList<PTClientsDTO>();
		Pageable pageable = new PageRequest(page, perPage);
		Page<Object[]> entitiesPage = filter == null ? sessionBookingRepo.getPTClients(uuid, pageable)
				: sessionBookingRepo.getPTClientsWithFilter(uuid, filter, pageable);
		List<Object[]> ptClients = entitiesPage.getContent();
		for (Object[] row : ptClients) {
			PTClientsDTO ptClientsDTO = new PTClientsDTO();
			ptClientsDTO.setAvatar(row[0] == null ? "" : row[0].toString());
			ptClientsDTO.setFullname(row[1] == null ? "" : row[1].toString());
			ptClientsDTO.setUsername(row[2] == null ? "" : row[2].toString());
			ptClientsDTO.setPhone(row[3] == null ? "" : row[3].toString());
			ptClientsDTO.setEmail(row[4] == null ? "" : row[4].toString());
			Map<String, BigInteger> totalSessionAndtotalLeepCoin = getTotalSessionCompletedAndTotalLeepCoin(uuid,
					row[5].toString());
			ptClientsDTO.setTotalCompletedSession(totalSessionAndtotalLeepCoin.get(SESSION_COMPLETED));
			ptClientsDTO.setTotalCredit(totalSessionAndtotalLeepCoin.get(TOTAL_LEEP_COIN));
			result.add(ptClientsDTO);
		}
		PageResponse<PTClientsDTO> pageResponse = new PageResponse<>();
		pageResponse.setEntities(result);
		pageResponse.setCount(entitiesPage.getTotalElements());
		return pageResponse;
	}
	
	private Map<String, BigInteger> getTotalSessionCompletedAndTotalLeepCoin(String ptUuid, String userUuid) {
		List<Object[]> rowData = sessionBookingRepo.getTotalSessionCompletedAndTotalLeepCoin(ptUuid, userUuid);
		Object[] rows = rowData.get(0);
		Map<String, BigInteger> result = new HashMap<String, BigInteger>();
		result.put(SESSION_COMPLETED, rows[0] == null ? BigInteger.valueOf(0) : (BigInteger) rows[0]);
		result.put(TOTAL_LEEP_COIN, rows[1] == null ? BigInteger.valueOf(0) : ((BigDecimal) rows[1]).toBigInteger());
		return result;
	}

	
}
