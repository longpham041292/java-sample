package asia.cmg.f8.gateway.client;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import asia.cmg.f8.gateway.security.dto.AccountTokenDTO;
import asia.cmg.f8.gateway.security.dto.UserSignup;

@Component
public class UserApiClientFallbackImpl implements UserApiClient {

	@Override
	public AccountTokenDTO getUserToken(String userUuid) {
		return null;
	}

	@Override
	public AccountTokenDTO saveUserToken(AccountTokenDTO accountTokenDTO) {
		return null;
	}

	@Override
	public AccountTokenDTO getByAccessToken(String accessToken) {
		return null;
	}

	@Override
	public AccountTokenDTO getByAccessTokenAndRefreshToken(String accessToken, String refreshToken) {
		return null;
	}
}
