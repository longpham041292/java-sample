package asia.cmg.f8.gateway.security.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import asia.cmg.f8.gateway.client.UserApiClient;
import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.gateway.security.dto.AccountTokenDTO;
import asia.cmg.f8.gateway.security.dto.UserInfo;
import asia.cmg.f8.gateway.security.exception.InValidAuthenticationException;
import asia.cmg.f8.gateway.security.token.JwtProperties;
import asia.cmg.f8.gateway.security.token.JwtTokenFactoryImpl;
import asia.cmg.f8.gateway.security.usergrid.UserGridProperties;

@Component
public class TokenAuthenticationUtil {
	@Autowired
	private UserApiClient userApiClient;
	
	@Autowired
	private JwtTokenFactoryImpl jwtFactoryImpl;
	
	@Autowired
	private JwtProperties jwtProperties;
	
	@Autowired
	private UserGridProperties userGridProperties;
	
	public TokenAuthentication generateTokenAuthentication(UserInfo userInfo, UserDetail userDetail) {
		AccountTokenDTO tokenDTO = null;
		TokenAuthentication authentication = null;
		try {
			tokenDTO = userApiClient.getUserToken(userInfo.getUuid());
		} catch (Exception e) {

		}

		long expiredAt = System.currentTimeMillis() + jwtProperties.getExpiresIn() * 1000;
		long refreshExpiredAt = System.currentTimeMillis() + jwtProperties.getRefreshExpiresIn() * 1000;
		String accessToken = null;
		String refreshToken = null;
		
		try {
			accessToken = jwtFactoryImpl.encode(userInfo.getUuid(), expiredAt);
			refreshToken = jwtFactoryImpl.encode(userInfo.getUuid(), refreshExpiredAt);
		} catch (IOException e) {
			throw new InValidAuthenticationException("Can't generate JWT access token " + e.getMessage());
		}
		
		if (tokenDTO == null) { // if there is no record in DB
			String encodedPassword = "";
			tokenDTO = new AccountTokenDTO(userInfo.getUuid(), expiredAt, refreshExpiredAt, accessToken, refreshToken,
					encodedPassword);
			AccountTokenDTO updatedTokenDTO = userApiClient.saveUserToken(tokenDTO);
			authentication = new TokenAuthentication(updatedTokenDTO.getAccessToken(), updatedTokenDTO.getRefreshToken(), userGridProperties.getAccessTokenTtl(), userDetail);
		} else { // if access_token expire
			tokenDTO.setAccessToken(accessToken);
			tokenDTO.setRefreshToken(refreshToken);
			tokenDTO.setExpiredAt(expiredAt);
			tokenDTO.setRefreshExpiredAt(refreshExpiredAt);
			AccountTokenDTO updatedTokenDTO = userApiClient.saveUserToken(tokenDTO);
			authentication = new TokenAuthentication(updatedTokenDTO.getAccessToken(),updatedTokenDTO.getRefreshToken() , userGridProperties.getAccessTokenTtl(), userDetail);
		}

		return authentication;
	}
}
