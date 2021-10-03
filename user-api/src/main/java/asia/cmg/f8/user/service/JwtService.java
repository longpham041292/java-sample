package asia.cmg.f8.user.service;

import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import asia.cmg.f8.user.entity.JwtResponse;

@Service
public class JwtService {
	  public static final String USERNAME = "username";
	  public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	  public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
//	  public static final String SECRET_KEY = "123456";
//	  public static final int EXPIRE_TIME = 86400000;
	  @Value( "${jwt.keyPass}" )
	  private String jwtKey;

	  @Value( "${jwt.access_expiresIn}" )
	  private int accessExpiresIn;
	  
	  @Value( "${jwt.refresh_expiresIn}" )
	  private int refreshExpiresIn;
	  
	public int getAccessExpiresIn() {
		return accessExpiresIn;
	}
	public int getRefreshExpiresIn() {
		return refreshExpiresIn;
	}
	public JwtResponse generateTokenLogin(String uuid) {
	    JwtResponse jwtResponse = new JwtResponse();
	    try {
	      // Create HMAC signer
	      JWSSigner signer = new MACSigner(generateShareSecret());
	      // ----- Generate Access Token
	      JWTClaimsSet.Builder builderAccess = new JWTClaimsSet.Builder();
	      builderAccess.claim(USERNAME, uuid);
	      builderAccess.expirationTime(generateAccessExpirationDate());
	      JWTClaimsSet claimsSetAccess = builderAccess.build();
	      SignedJWT signedAccessJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSetAccess);
	      // Apply the HMAC protection
	      signedAccessJWT.sign(signer);
	      // Serialize to compact form, produces something like
	      // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
	      jwtResponse.setAccessToken(signedAccessJWT.serialize());
	      
	      // ------ Generate Refresh Token
	      JWTClaimsSet.Builder builderRefresh = new JWTClaimsSet.Builder();
	      builderRefresh.claim(USERNAME, uuid);
	      builderRefresh.expirationTime(generateRefreshExpirationDate());
	      JWTClaimsSet claimsSetRefresh = builderRefresh.build();
	      SignedJWT signedRefreshJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSetRefresh);
	      // Apply the HMAC protection
	      signedRefreshJWT.sign(signer);
	      // Serialize to compact form, produces something like
	      // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
	      jwtResponse.setRefreshToken(signedRefreshJWT.serialize());
	      jwtResponse.setAccessExpiresIn(accessExpiresIn);
	      jwtResponse.setRefreshExpiresIn(refreshExpiresIn);
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return jwtResponse;
	  }
	
	public String generateToken(String uuid, String tokenType) {
	    try {
	      // Create HMAC signer
	      JWSSigner signer = new MACSigner(generateShareSecret());
	      // ----- Generate Access Token
	      JWTClaimsSet.Builder builderAccess = new JWTClaimsSet.Builder();
	      builderAccess.claim(USERNAME, uuid);
	      if(tokenType == REFRESH_TOKEN) {
	    	  builderAccess.expirationTime(generateAccessExpirationDate());	    	  
	      } else {
	    	  builderAccess.expirationTime(generateRefreshExpirationDate());
	      }
	      JWTClaimsSet claimsSetAccess = builderAccess.build();
	      SignedJWT signedAccessJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSetAccess);
	      // Apply the HMAC protection
	      signedAccessJWT.sign(signer);
	      // Serialize to compact form, produces something like
	      // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
	      return signedAccessJWT.serialize();
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return null;
	 }
	
	  private JWTClaimsSet getClaimsFromToken(String token) {
	    JWTClaimsSet claims = null;
	    try {
	      SignedJWT signedJWT = SignedJWT.parse(token);
	      JWSVerifier verifier = new MACVerifier(generateShareSecret());
	      if (signedJWT.verify(verifier)) {
	        claims = signedJWT.getJWTClaimsSet();
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return claims;
	  }
	  public Date generateAccessExpirationDate() {
	    return new Date(System.currentTimeMillis() + accessExpiresIn);
	  }
	  public Date generateRefreshExpirationDate() {
	    return new Date(System.currentTimeMillis() + refreshExpiresIn);
	  }
	  private Date getExpirationDateFromToken(String token) {
	    Date expiration = null;
	    JWTClaimsSet claims = getClaimsFromToken(token);
	    expiration = claims.getExpirationTime();
	    return expiration;
	  }
	  public String getUsernameFromToken(String token) {
	    String username = null;
	    try {
	      JWTClaimsSet claims = getClaimsFromToken(token);
	      username = claims.getStringClaim(USERNAME);
	    } catch (Exception e) {
//	      e.printStackTrace();
	    }
	    return username;
	  }
	  private byte[] generateShareSecret() {
	    // Generate 256-bit (32-byte) shared secret
	    byte[] sharedSecret = new byte[32];
	    sharedSecret = jwtKey.getBytes();
	    return sharedSecret;
	  }
	  private Boolean isTokenExpired(String token) {
	    Date expiration = getExpirationDateFromToken(token);
	    return expiration.before(new Date());
	  }
	  public Boolean validateTokenLogin(String token) {
	    if (token == null || token.trim().length() == 0) {
	      return false;
	    }
	    String username = getUsernameFromToken(token);
	    if (username == null || username.isEmpty()) {
	      return false;
	    }
	    if (isTokenExpired(token)) {
	      return false;
	    }
	    return true;
	  }

}
