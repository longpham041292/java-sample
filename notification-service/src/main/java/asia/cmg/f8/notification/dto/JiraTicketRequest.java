package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created on 12/30/16.
 */
public class JiraTicketRequest {
    @NotEmpty
    @JsonProperty("summary")
    private String summary;

    @JsonProperty("description")
    private String description;

    @JsonProperty("source")
    private String source;

    @NotEmpty
    @JsonProperty("username")
    private String userName;

    @NotEmpty
    @JsonProperty("email")
    private String email;

    @NotEmpty
    @JsonProperty("name")
    private String name;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("role")
    private String role;

    @NotNull
    @JsonProperty("topic")
    private Integer topic;

    @JsonProperty("reported_userid")
    private String reportedUserId;

    @JsonProperty("reported_postid")
    private String reportedPostId;

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public Integer getTopic() {
        return topic;
    }

    public void setTopic(final Integer topic) {
        this.topic = topic;
    }

    public String getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(final String reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReportedPostId() {
        return reportedPostId;
    }

    public void setReportedPostId(final String reportedPostId) {
        this.reportedPostId = reportedPostId;
    }
}
