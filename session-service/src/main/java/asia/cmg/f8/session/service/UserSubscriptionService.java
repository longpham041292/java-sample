package asia.cmg.f8.session.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.event.OrderSubscriptionCompletedEvent;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.session.dto.PageResponse;
import asia.cmg.f8.session.dto.UserSubscriptionDto;
import asia.cmg.f8.session.entity.UserSubscriptionEntity;
import asia.cmg.f8.session.repository.UserSubscriptionRepository;

@Service
public class UserSubscriptionService {

	@Autowired
	private UserSubscriptionRepository userSubscribeRepository;

	public PageResponse<UserSubscriptionDto> getMySubscribes(final String userUuid, UserType userType,
			final Boolean active, final Pageable pageable) {

		Page<UserSubscriptionDto> resultSet = null;
		if (userType.equals(UserType.EU)) {
			resultSet = userSubscribeRepository.getUserSubscriptionsByEu(userUuid, active, pageable);
		} else {
			resultSet = userSubscribeRepository.getUserSubscriptionsByPt(userUuid, active, pageable);
		}

		// List<BasicUserInfo> resultDtos =
		// resultSet.getContent().stream().map(userEntity ->
		// BasicUserInfo.convertFromEntity(userEntity)).collect(Collectors.toList());
		List<UserSubscriptionDto> resultDtos = resultSet.getContent();
		final PageResponse<UserSubscriptionDto> pagedResult = new PageResponse<>();
		pagedResult.setCount(resultSet.getTotalElements());
		pagedResult.setEntities(resultDtos);

		return pagedResult;
	}

	@Transactional
	public UserSubscriptionEntity createUserSubscription(final OrderSubscriptionCompletedEvent event) {
		UserSubscriptionEntity entity = new UserSubscriptionEntity();
		entity.setStartTime(
				LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getStartTime()), TimeZone.getDefault().toZoneId()));
		entity.setEndTime(
				LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getEndTime()), TimeZone.getDefault().toZoneId()));
		entity.setEuUuid(String.valueOf(event.getEuUuid()));
		entity.setPtUuid(String.valueOf(event.getPtUuid()));
		entity.setNumberOfMonth(event.getNumberOfMonth());
		entity.setLimitDay(event.getLimitDay());
		entity.setOrderUuid(String.valueOf(event.getOrderUuid()));
		entity.setSubscriptionId(event.getSubscriptionId());
		entity.setUuid(UUID.randomUUID().toString());
		entity = userSubscribeRepository.save(entity);
		return entity;
	}
}
