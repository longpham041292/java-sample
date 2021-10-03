package asia.cmg.f8.notification.entity;

import java.util.List;

/**
 * Created on 10/19/16.
 */
public class UserGridResponse<E> {
    private String action;
    private String application;
    private Object params;
    private String path;
    private String uri;
    private String timestamp;
    private String duration;
    private String organization;
    private String applicationName;
    private int count;
    private List<E> entities;

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(final String application) {
        this.application = application;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(final String organization) {
        this.organization = organization;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(final String applicationName) {
        this.applicationName = applicationName;
    }

    public List<E> getEntities() {
        return entities;
    }

    public void setEntities(final List<E> entities) {
        this.entities = entities;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(final Object params) {
        this.params = params;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }
}
