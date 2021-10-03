package asia.cmg.f8.session.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.client.ClubClient;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.entity.ClubEntity;

@Service
@Transactional(readOnly = true)
public class ClubService {

	@Autowired
	private ClubClient clubClient;
		
	public ClubEntity getClubByUuid(final String uuid) {
		
		try {
			UserGridResponse<ClubEntity> response = clubClient.getByUuid(uuid);
			if(!Objects.isNull(response)) {
				List<ClubEntity> clubs = response.getEntities();
				if(!clubs.isEmpty()) {
					return clubs.get(0);
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
