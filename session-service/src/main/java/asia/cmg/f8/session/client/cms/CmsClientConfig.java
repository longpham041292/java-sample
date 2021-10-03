package asia.cmg.f8.session.client.cms;

public class CmsClientConfig {
	private static String token = "";
	
	// Expiration time in second
	private static long expiration = 0;
	
	public static String getAuthToken() {
		if(!token.isEmpty())
			return "Bearer " + token;
		return token;
	}

	public static void setToken(String token) {
		CmsClientConfig.token = token;
	}
	
	public static String getToken() {
		return token;
	}

	public static long getExpiration() {
		return expiration;
	}

	public static void setExpiration(long expiration) {
		CmsClientConfig.expiration = expiration;
	}
	
}
