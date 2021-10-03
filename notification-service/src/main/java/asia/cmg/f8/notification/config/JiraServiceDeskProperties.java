package asia.cmg.f8.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 12/29/16.
 */
@Component
@ConfigurationProperties(prefix = "jiraServiceDesk")
public class JiraServiceDeskProperties {
    private String userName;
    private String password;
    private String serviceDeskId;
    private String anonymousUserRole;
    private String ptRole;
    private String euRole;


    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getServiceDeskId() {
        return serviceDeskId;
    }

    public void setServiceDeskId(final String serviceDeskId) {
        this.serviceDeskId = serviceDeskId;
    }

    public String getAnonymousUserRole() {
        return anonymousUserRole;
    }

    public void setAnonymousUserRole(final String anonymousUserRole) {
        this.anonymousUserRole = anonymousUserRole;
    }

    public String getPtRole() {
        return ptRole;
    }

    public void setPtRole(final String ptRole) {
        this.ptRole = ptRole;
    }

    public String getEuRole() {
        return euRole;
    }

    public void setEuRole(final String euRole) {
        this.euRole = euRole;
    }
}
