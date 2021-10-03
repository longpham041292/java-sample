package asia.cmg.f8.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    private String defaultLanguage;
    private String protocol;
    private String accessActuatorRole;

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(final String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(final String protocol) {
		this.protocol = protocol;
	}

	public String getAccessActuatorRole() {
		return accessActuatorRole;
	}

	public void setAccessActuatorRole(String accessActuatorRole) {
		this.accessActuatorRole = accessActuatorRole;
	}
    
    
}
