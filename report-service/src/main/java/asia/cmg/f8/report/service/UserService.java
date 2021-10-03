package asia.cmg.f8.report.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import asia.cmg.f8.report.dto.CreditBookingSessionStatus;
import asia.cmg.f8.report.repository.BasicUserRepository;

@Service
@SuppressWarnings("PMD.ExcessiveImports")
public class UserService {

	private static final String DD_LLLL_YYYY = "dd/LLLL/yyyy";

	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	private final BasicUserRepository userRepository;

	public UserService(final BasicUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public int countActiveUser(final String userType, final LocalDateTime start, final LocalDateTime end) {
		return userRepository.countActiveUser(userType, start, end);
	}

	public int countTotalUser(String userType) {
		return userRepository.countTotalUser(userType);
	}

	public int countTotalPtHasBookingByRange(LocalDateTime start, LocalDateTime end) {
		List<Integer> statusCode = Arrays.asList(CreditBookingSessionStatus.COMPLETED.ordinal(),
				CreditBookingSessionStatus.BURNED.ordinal());
		return userRepository.countTotalPtHasBookingByRange(start, end, statusCode);
	}

	public Integer countTotalUserByRangeTime(String userType, LocalDateTime start, LocalDateTime end) {
		return userRepository.countTotalUserByRangeTime(userType, start, end);
	}

	public int countApprovedTrainerByRange(LocalDateTime start, LocalDateTime end) {
		return userRepository.countApprovedTrainerByRange(start, end);
	}
}
