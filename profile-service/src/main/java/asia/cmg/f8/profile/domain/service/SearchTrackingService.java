package asia.cmg.f8.profile.domain.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.profile.database.entity.SearchTrackingEntity;
import asia.cmg.f8.profile.domain.repository.SearchTrackingRepository;
import asia.cmg.f8.profile.dto.SearchTrackingRequest;

@Service
public class SearchTrackingService {
	
	@Autowired
	SearchTrackingRepository searchTrackingRepo;

	public List<SearchTrackingEntity> getTopLatestSearch(final String userUuid, final int limit) throws Exception {
		try {
			List<SearchTrackingEntity> resultList = searchTrackingRepo.getTopLatestSearch(userUuid, limit);
			return resultList;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public SearchTrackingEntity updateTrackingSearch(final SearchTrackingRequest trackingSearch, final String accountUuid) throws Exception {
		try {
			Optional<SearchTrackingEntity> result = searchTrackingRepo.findFirstByAccountUuidAndUserUuid(accountUuid, trackingSearch.userUuid);
			if(result.isPresent()) {
				SearchTrackingEntity entity = result.get();
				entity.setAvatar(trackingSearch.avatar);
				entity.setName(trackingSearch.name);
				entity.setModifiedDate(new Date());
				return searchTrackingRepo.save(entity);
			} else {
				SearchTrackingEntity entity = new SearchTrackingEntity();
				entity.setAvatar(trackingSearch.avatar);
				entity.setName(trackingSearch.name);
				entity.setUsername(trackingSearch.username);
				entity.setUserUuid(trackingSearch.userUuid);
				entity.setAccountUuid(accountUuid);
				entity.setUserType(trackingSearch.userType);
				return searchTrackingRepo.save(entity);
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
