package asia.cmg.f8.session.internal.service;

import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.repository.SessionHistoryRepository;

@Service
public class InternalMVSessionDailyService {

	public static final Logger LOG = LoggerFactory.getLogger(InternalMVSessionDailyService.class);

	@Inject
	private SessionHistoryRepository sessionHistoryRepository;

	@Transactional
	@Async
	public void runSessionDailyView() {
		LOG.info("Start query session history at {}", new Date());
		final String currentZone = ZoneDateTimeUtils.getCurrentZoneOffset().getId();
		sessionHistoryRepository.updateSessionHistoryByTimeRange(currentZone);
	}
}
