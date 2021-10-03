package asia.cmg.f8.gateway.security.api;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.gateway.client.UserApiClient;
import asia.cmg.f8.gateway.security.apple.AppleUserInfoAPI;
import asia.cmg.f8.gateway.security.dto.AccountTokenDTO;
import asia.cmg.f8.gateway.security.dto.AppleUserPayloadDTO;
import asia.cmg.f8.gateway.security.dto.AppleVerifyResponse;
import asia.cmg.f8.gateway.security.exception.InValidAuthenticationException;
import asia.cmg.f8.gateway.security.token.JwtProperties;
import asia.cmg.f8.gateway.security.token.JwtTokenFactoryImpl;

@RestController
public class AccountTokenApi {

	@Autowired
	private UserApiClient userApiClient;
	
	@Autowired
	private JwtProperties jwtProperties;
	
	@Autowired
    private JwtTokenFactoryImpl jwtFactoryImpl;
	
	@Autowired
	private AppleUserInfoAPI appleUserInfoAPI;
	
	@Value("${security.apple.client_id}")
	private String APPLE_CLIENT_ID;
	@Value("${security.apple.client_secret_key}")
	private String APPLE_CLIENT_SECRET;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountTokenApi.class);
	
	@RequestMapping(value = "/token/refresh", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> refreshToken( @RequestBody AccountTokenDTO request) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			if(request.getAccessToken() == null) {
				LOGGER.info("Access token input is empty");
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Access token input is empty"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
			
			AccountTokenDTO accountTokenDTO = userApiClient.getByAccessTokenAndRefreshToken(request.getAccessToken(), request.getRefreshToken());
			
			if(accountTokenDTO == null) {
				LOGGER.info("Access token or refresh token not found");
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Access token or refresh token not found"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			} 
			
			if (accountTokenDTO.getExpiredAt() > System.currentTimeMillis()){
				Map<String, String> data = new HashMap<String, String>();
				data.put("access_token", accountTokenDTO.getAccessToken());
				data.put("refresh_token", accountTokenDTO.getRefreshToken());
				apiResponse.setData(data);
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
			
			if (accountTokenDTO.getRefreshExpiredAt() > System.currentTimeMillis()){
				long currentTime = System.currentTimeMillis();
				long newExpiredAt = currentTime + jwtProperties.getExpiresIn() * 1000;
				String newAccesstoken = jwtFactoryImpl.encode(accountTokenDTO.getUuid(), newExpiredAt);
				
				accountTokenDTO.setAccessToken(newAccesstoken);
				accountTokenDTO.setExpiredAt(newExpiredAt);
				
				AccountTokenDTO newAccountTokenDTO = userApiClient.saveUserToken(accountTokenDTO);
				if(newAccountTokenDTO == null) {
					LOGGER.info("Generated new token failed for user uuid {}", accountTokenDTO.getUuid());
					apiResponse.setStatus(ErrorCode.FAILED.withDetail("Generated new token failed"));
					return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
				} else {
					LOGGER.info("Generated new token successfully");
					
					Map<String, String> data = new HashMap<String, String>();
					data.put("access_token", newAccountTokenDTO.getAccessToken());
					data.put("refresh_token", newAccountTokenDTO.getRefreshToken());
					apiResponse.setData(data);
					return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
				}
			}
			
			LOGGER.info("Refresh token has expired");
			apiResponse.setStatus(ErrorCode.FAILED.withDetail("Refresh token has expired"));
			return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			LOGGER.info("Generated new token failed: {}", e);
			apiResponse.setStatus(ErrorCode.FAILED);
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/token/apple/refresh", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> refreshToken(@RequestHeader("apple_code") String appleCode) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			AppleVerifyResponse dto = appleUserInfoAPI.authAppleID("authorization_code", APPLE_CLIENT_ID,
					APPLE_CLIENT_SECRET, appleCode, "" // empty refresh token
			);
			
			Map<String, String> data = new HashMap<String, String>();
			data.put("apple_refresh_token", dto.getRefreshToken());
			apiResponse.setData(data);
		} catch (Exception e) {
			LOGGER.info("Invalid Apple Code or user is deactivated", e);
			apiResponse.setStatus(ErrorCode.FAILED);
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	public AppleVerifyResponse appleGenerateToken(String appleCode) {
		try {
			return appleUserInfoAPI.authAppleID("authorization_code", APPLE_CLIENT_ID,
					APPLE_CLIENT_SECRET, appleCode, "" // empty refresh token
			);
		} catch (Exception e) {
			throw new InValidAuthenticationException("Invalid Apple Code or user is deactivated, " + e);
		}
	}
}
