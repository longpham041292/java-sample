package asia.cmg.f8.gateway.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created on 10/20/16.
 */
@ConfigurationProperties("jwt")
public class JwtProperties {

    private String keyPass;
    private String alias;
    private Integer expiresIn;
    private Integer refreshExpiresIn;

    public String getKeyPass() {
        return keyPass;
    }

    public void setKeyPass(final String keyPass) {
        this.keyPass = keyPass;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(final Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

	public Integer getRefreshExpiresIn() {
		return refreshExpiresIn;
	}

	public void setRefreshExpiresIn(Integer refreshExpiresIn) {
		this.refreshExpiresIn = refreshExpiresIn;
	}
}
