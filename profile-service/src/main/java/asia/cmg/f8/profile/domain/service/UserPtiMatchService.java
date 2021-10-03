package asia.cmg.f8.profile.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.profile.database.entity.UserPtiMatchEntity;
import asia.cmg.f8.profile.domain.repository.UserPTiMatchRepository;
import asia.cmg.f8.profile.dto.UserPTiMatchDTO;


@Service
public class UserPtiMatchService {

	@Autowired
	UserPTiMatchRepository userPtiMatchRepo;
	
	public UserPTiMatchDTO getByEuUuidAndPtUuid(final String euUuid, final String ptUuid) {
		Optional<UserPtiMatchEntity> response = userPtiMatchRepo.getByEuUuidAndPtUuid(euUuid, ptUuid);
		if(response != null && response.isPresent()) {
			return new UserPTiMatchDTO(response.get());
		}
		return null;
	}
	
	public Boolean isUserPtiAvailable(final String uuid) {
		return userPtiMatchRepo.countPtiResultByUserUuid(uuid) > 0;
	}
}
