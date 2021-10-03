package asia.cmg.f8.commerce.service;

import java.util.Locale;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import asia.cmg.f8.commerce.client.UserClient;
import asia.cmg.f8.commerce.dto.UserEntity;
import asia.cmg.f8.commerce.entity.BasicUserEntity;
import asia.cmg.f8.commerce.repository.BasicUserRepository;
import asia.cmg.f8.common.util.UserGridResponse;

@Service
public class UserService {

	private Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	
	private final BasicUserRepository userRepository;
	private final UserClient userClient;

	public UserService(final BasicUserRepository userRepository, final UserClient userClient) {
		this.userRepository = userRepository;
		this.userClient = userClient;
	}

	public BasicUserEntity getOneByUserCode(final String userCode) {
		return userRepository.findOneByUserCode(userCode).orElse(null);
	}
	
	public BasicUserEntity getOneByUuid(final String uuid) {
		return userRepository.findOneByUuid(uuid).orElse(null);
	}
	
    @Cacheable(cacheNames = "userLocaleCache", key = "#uuid")
    public Locale getLocale(final String uuid) {

        Locale locale = Locale.ENGLISH;
        final UserGridResponse<UserEntity> userInfo = userClient.getUser(uuid);

        if (userInfo != null && !CollectionUtils.isEmpty(userInfo.getEntities()) ) {
            final UserEntity user = userInfo.getEntities().iterator().next();
            final String language = user.getLanguage();
            if (language != null) {
                locale = new Locale(language);
                LOGGER.info("Loaded \"{}\" locale of user {}", locale, uuid);
            }
        }
        return locale;
    }
    
	public boolean checkVerifyPhone(String uuid) {
		 Optional<BasicUserEntity> userOptional =  userRepository.findOneByUuid(uuid);
		 if(userOptional.isPresent()) {
			 return  userOptional.get().getVerifyPhone();
		 }
		 return false;
	}
}
