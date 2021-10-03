package asia.cmg.f8.gateway.security.token;

import asia.cmg.f8.gateway.config.GatewayProperties;
import asia.cmg.f8.gateway.security.auth.TokenAuthentication;
import asia.cmg.f8.gateway.security.api.UserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.util.Assert;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 10/20/16.
 */
public class JwtTokenFactoryImpl implements JwtTokenFactory, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFactoryImpl.class);
    public static final int DEFAULT_EXPIRE_TIME = 3600;
    
    private static final String USER_UUID = "user_uuid";
    private static final String ISSUE_AT = "issue_at";
    private static final String EXPIRE_AT = "expire_at";
    private static final String REFRESH_EXPIRE_AT = "refresh_expire_at";

    private final KeyPair keyPair;
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final GatewayProperties gatewayProperties;

    private Signer signer;

    public JwtTokenFactoryImpl(final KeyPair keyPair,
                               final ObjectMapper objectMapper, final JwtProperties jwtProperties,
                               final GatewayProperties gatewayProperties) {
        this.keyPair = keyPair;
        this.objectMapper = objectMapper;
        this.jwtProperties = jwtProperties;
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public String encode(final TokenAuthentication authentication) throws IOException {

        final UserDetail userDetail = authentication.getUserDetail();
        final Integer exp = jwtProperties.getExpiresIn() == null ? DEFAULT_EXPIRE_TIME : jwtProperties.getExpiresIn();

        final Map<String, Object> map = new HashMap<>();
        map.put("uuid", userDetail.getUuid());
        map.put("exp", System.currentTimeMillis() + exp);
        map.put("userType", userDetail.getUserType());

        map.put("usergridAccessToken", authentication.getToken());
        map.put("authorities", authentication.getRoles()); // don't change this and don't send user_name to downstream.

        String language = userDetail.getLanguage();
        if (StringUtils.isEmpty(language)) {
            language = gatewayProperties.getDefaultLanguage();
        }
        map.put("language", language);
        
        final String content = objectMapper.writeValueAsString(map);
        return JwtHelper.encode(content, signer).getEncoded();
    }

    @Override
    @SuppressWarnings("PMD")
    public void afterPropertiesSet() throws Exception {
        final PrivateKey privateKey = keyPair.getPrivate();
        Assert.state(privateKey instanceof RSAPrivateKey, "KeyPair must be an RSA ");
        signer = new RsaSigner((RSAPrivateKey) privateKey);
        final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // TODO this variable should be a member from this class if we support decode logic.
        final SignatureVerifier verifier = new RsaVerifier(publicKey);

        // verify signer.
        final byte[] test = "test".getBytes();
        try {
            verifier.verify(test, signer.sign(test));
            LOGGER.info("Signing and verification RSA keys match");
        } catch (final InvalidSignatureException e) {
            LOGGER.error("Signing and verification RSA keys do not match", e);
        }
    }

	@Override
	public String encode(String userUuid, long expireAt) throws IOException {
		final Map<String, Object> map = new HashMap<>();
		long currentTime = System.currentTimeMillis();
		
        map.put(USER_UUID, userUuid);
        map.put(ISSUE_AT, currentTime);
        map.put(EXPIRE_AT, expireAt);
        
        final String content = objectMapper.writeValueAsString(map);
        return JwtHelper.encode(content, signer).getEncoded();
	}

	@Override
	public JwtTokenDTO decode(String token) throws Exception {
		try {
			Jwt jwt = JwtHelper.decode(token);
		    String claims = jwt.getClaims();
		    JsonParser jsonParser = JsonParserFactory.getJsonParser();
		    Map<String, Object> claimMap = jsonParser.parseMap(claims);
		    String userUuid = (String)claimMap.get(USER_UUID);
		    Long issueAt = (Long)claimMap.get(ISSUE_AT);
		    Long expireAt = (Long)claimMap.get(EXPIRE_AT);
		    Long refreshExpireAt = (Long)claimMap.get(REFRESH_EXPIRE_AT);
		    return new JwtTokenDTO(userUuid, issueAt, expireAt, refreshExpireAt);
		} catch (Exception e) {
			return null;
		}
	}
}
