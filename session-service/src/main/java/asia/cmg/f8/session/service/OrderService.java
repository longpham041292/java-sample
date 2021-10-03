package asia.cmg.f8.session.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.entity.SessionPackageStatus;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.OrderRepository;
import asia.cmg.f8.session.repository.SessionPackageRepository;
import asia.cmg.f8.session.wrapper.dto.AvailableSessionWithOrder;
import asia.cmg.f8.session.wrapper.service.OrderSessionWrapperService;

/**
 * Created on 12/25/16.
 */
@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderSessionWrapperService orderSessionWrapperService;
	private final SessionPackageRepository sessionPackageRepository;
	private final SessionService sessionService;

	public OrderService(final OrderRepository orderRepository, OrderSessionWrapperService orderSessionWrapperService,
			SessionPackageRepository sessionPackageRepository, SessionService sessionService) {
		this.orderRepository = orderRepository;
		this.orderSessionWrapperService = orderSessionWrapperService;
		this.sessionPackageRepository = sessionPackageRepository;
		this.sessionService = sessionService;
	}

	@Transactional(readOnly = true)
	public boolean isContracting(final String userId, final String trainerId) {
		final List<SessionPackageStatus> validPackageStatus = SessionPackageStatus.getValidSessionPackageStatus();
		final Integer numOfValid = orderRepository.countValidOrderByTrainerAndUser(userId, trainerId, validPackageStatus);
		return numOfValid > 0;
	}

	@Transactional
	public void cancelInvalidSessions(final LocalDate expiredDate, final String orderUuid) {
		SessionPackageEntity sessionPackage = sessionPackageRepository.findOneByOrderUuid(orderUuid).get();
		if (sessionPackage != null) {
			final List<String> availableStatus = SessionStatus.getReviewSessionStatus().stream().map(Enum::name)
					.collect(Collectors.toList());
			List<AvailableSessionWithOrder> pendingSessions = orderSessionWrapperService.getAvailableSessionWithOrder(
					sessionPackage.getUserUuid(), sessionPackage.getPtUuid(), availableStatus);
			Timestamp timestampExpiredDate = Timestamp.valueOf(expiredDate.atTime(LocalTime.MAX));
			pendingSessions.forEach(session -> {
				if (session.getOrderUuid().equals(orderUuid) && timestampExpiredDate.before(session.getStartTime())) {
					sessionService.cancelSession(session.getSessionUuid());
				}
			});
		}
	}
}
