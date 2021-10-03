package asia.cmg.f8.session.service;

import java.time.format.DateTimeFormatter;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import asia.cmg.f8.session.config.CrmConfig;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.OrderEntity;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.OrderRepository;
import asia.cmg.f8.session.repository.SessionRepository;

@Service
public class CrmService {

	private static final Logger LOG = LoggerFactory.getLogger(CrmService.class);

	private final SessionRepository sessionRepository;
	private final RestTemplate restTemplate;
	private final CrmConfig crmConfig;
	private final OrderRepository orderRepository;
	private final BasicUserRepository basicUserRepository;

	public CrmService(final SessionRepository sessionRepository, final RestTemplateBuilder restBuilder,
			final CrmConfig crmConfig, OrderRepository orderRepository, BasicUserRepository basicUserRepository) {
		this.sessionRepository = sessionRepository;
		this.restTemplate = restBuilder.build();
		this.crmConfig = crmConfig;
		this.orderRepository = orderRepository;
		this.basicUserRepository = basicUserRepository;
	}

	@Transactional
	public void syncCrmBurnedSession(final String sessionUuid) {
		final SessionEntity session = sessionRepository.findOneByUuid(sessionUuid).get();
		final OrderEntity order = orderRepository.findOneBySessionPackageUuid(session.getPackageUuid()).get();
		final BasicUserEntity userEntity = basicUserRepository.findOneByUuid(session.getPtUuid()).get();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss");
		
		final String clubCode = StringUtils.isEmpty(order.getOrderClubcode()) ? crmConfig.getDefaultClubCode() : order.getOrderClubcode();
		final String ptCode = StringUtils.isEmpty(userEntity.getUsercode()) ? crmConfig.getDefaultPtCode() : userEntity.getUsercode();

		String url = String.format("%s/BookingPTSession/%s/%s/%s/%s", crmConfig.getWcfUrl(), order.getContractNumber(),
				clubCode, session.getStartTime().format(formatter), ptCode);
		LOG.info("URL send to CRM: {}", url);
		String resResponse = StringUtils.EMPTY;
		try {
			resResponse = restTemplate.getForObject(url, String.class);
			LOG.info("Reponse from CRM: {}", resResponse);
		} catch (final Exception ex) {
			resResponse = "Error sendsend to CRM";
			LOG.error("Error sendsend to CRM {}", ex);
		}
		finally {
			session.setCrmSyncedMessage(resResponse);
			sessionRepository.save(session);
		}

	}
}
