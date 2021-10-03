package asia.cmg.f8.profile.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.profile.domain.entity.ShortUserEntity;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.repository.QueryRepositoryCustom;
import asia.cmg.f8.profile.dto.PagedResponse;


@Service
public class PTInfoService {

	@Autowired
	QueryRepositoryCustom queryRepositoryCustom;
	
	public List<Object> getPTsByFilter(final String queryStr) {
		return queryRepositoryCustom.getPTsByFilter(queryStr);
	}
	
	public PagedResponse<ShortUserEntity> convertPTsInfo(final List<UserEntity> userList) {
			List<ShortUserEntity> pTInfoList = userList.stream()
					.map(userEntity -> {
							return ShortUserEntity.builder()
									.withUuid(userEntity.getUuid())
									.withAvatar(Optional.ofNullable(userEntity.getPicture()).filter(StringUtils::isNotEmpty).orElse(StringUtils.EMPTY))
									.withUsername(userEntity.getUsername())
									.withFullname(userEntity.getName())
									.withLevel(Optional.ofNullable(userEntity.getLevel()).filter(StringUtils::isNotEmpty).orElse(StringUtils.EMPTY))
									.withBio(userEntity.getProfile() == null ? "" : (userEntity.getProfile().getBio() == null ? "" : userEntity.getProfile().getBio()))
									.withRated(userEntity.getProfile() == null ? 0 : (userEntity.getProfile().getRated() == null ? 0 : userEntity.getProfile().getRated()))
									.build();
							}
						)
					.filter(object -> object != null)
					.collect(Collectors.toList());
			PagedResponse<ShortUserEntity> pTInfoResponse = new PagedResponse<ShortUserEntity>();
			pTInfoResponse.setCount(pTInfoList.size());
			pTInfoResponse.setEntities(pTInfoList);
		return pTInfoResponse;
	}
}
