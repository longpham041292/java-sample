package asia.cmg.f8.session.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "crm")
public class CrmConfig {
	
    private String wcfUrl;
    private String defaultClubCode;
    private String defaultPtCode;

	public String getWcfUrl() {
		return wcfUrl;
	}

	public void setWcfUrl(String wcfUrl) {
		this.wcfUrl = wcfUrl;
	}

	public String getDefaultClubCode() {
		return defaultClubCode;
	}

	public void setDefaultClubCode(String defaultClubCode) {
		this.defaultClubCode = defaultClubCode;
	}

	public String getDefaultPtCode() {
		return defaultPtCode;
	}

	public void setDefaultPtCode(String defaultPtCode) {
		this.defaultPtCode = defaultPtCode;
	}
}
