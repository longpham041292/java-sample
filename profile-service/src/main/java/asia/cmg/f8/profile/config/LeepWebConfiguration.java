package asia.cmg.f8.profile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "partner")
public class LeepWebConfiguration {
	
	@Value("${partner.leepWeb.domain}")
	private String domain;
	
	@Value("${partner.leepWeb.databaseUrl}")
	private String databaseUrl;
	
	@Value("${partner.leepWeb.databaseUser}")
	private String databaseUser;
	
	@Value("${partner.leepWeb.databasePassword}")
	private String databasePassword;
	
	public String getDomain() {
		return domain;
	}
	public String getDatabaseUrl() {
		return databaseUrl;
	}
	public String getDatabaseUser() {
		return databaseUser;
	}
	public String getDatabasePassword() {
		return databasePassword;
	}
}
