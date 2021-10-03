package asia.cmg.f8.user.api;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.user.entity.AccountTokenEntity;
import asia.cmg.f8.user.repository.AccountTokenRepository;

@RestController
public class AccountTokenApi {
	
	@Autowired
	private AccountTokenRepository accountTokenRepo;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountTokenApi.class);  
	
	@RequestMapping(value = "/users/{uuid}/token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public AccountTokenEntity getTokenByUserUuid(@PathVariable(name = "uuid") final String userUuid) throws Exception {
		try {
			Optional<AccountTokenEntity> optAccountToken = accountTokenRepo.findByUuid(userUuid);
			if(optAccountToken.isPresent()) {
				return optAccountToken.get();
			} else {
				LOGGER.info("Token of owner {} not existed", userUuid);
				throw new Exception("Token of owner not existed");
			}
		} catch (Exception e) {
			throw new Exception("Get token failed: " + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/users/token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public AccountTokenEntity getByAccessToken(@RequestParam(name = "leep_access_token") final String accessToken) throws Exception {
		try {
			AccountTokenEntity accountToken = accountTokenRepo.findByAccessToken(accessToken);
			if(accountToken != null) {
				return accountToken;
			} else {
				LOGGER.info("Token {} not existed", accessToken);
				throw new Exception("Token not existed");
			}
		} catch (Exception e) {
			throw new Exception("Get token failed: " + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/users/token/refresh-token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public AccountTokenEntity getByAccessTokenAndRefreshToken(@RequestParam(name = "leep_access_token") final String accessToken,
															@RequestParam(name = "leep_refresh_token") final String refreshToken) throws Exception {
		try {
			AccountTokenEntity accountToken = accountTokenRepo.findByAccessTokenAndRefreshToken(accessToken, refreshToken);
			if(accountToken != null) {
				return accountToken;
			} else {
				LOGGER.info("Access token {} and refresh token not existed", accessToken, refreshToken);
				throw new Exception("Access token and refresh token not existed");
			}
		} catch (Exception e) {
			throw new Exception("Get getByAccessTokenAndRefreshToken failed: " + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/users/token", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public AccountTokenEntity saveUserAccessToken(@RequestBody @Valid AccountTokenEntity request) throws Exception {
		try {
			return accountTokenRepo.save(request);
		} catch (Exception e) {
			LOGGER.info("Save token of owner {} failed", request.getUuid());
			throw new Exception("Save token failed: " + e.getMessage());
		}
	}
}
