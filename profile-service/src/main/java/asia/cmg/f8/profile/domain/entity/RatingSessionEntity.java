package asia.cmg.f8.profile.domain.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("squid:S2384")
public class RatingSessionEntity {

	@JsonProperty("uuid")
    private String uuid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("reviewer_id")
    private String reviewerId;

    @JsonProperty("reviewer_name")
    private String reviewerName;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("stars")
    private Double stars;

    @JsonProperty("reaction")
    private String reaction;

    @JsonProperty("reasons")
    private List<String> reasons;

    @JsonProperty("session_date")
    private Long sessionDate;

    @JsonProperty("comment")
    private String comment;
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(final String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(final String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(final Double stars) {
        this.stars = stars;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(final String reaction) {
        this.reaction = reaction;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(final List<String> reasons) {
        this.reasons = reasons;
    }

    public Long getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(final Long sessionDate) {
        this.sessionDate = sessionDate;
    }

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}
    
	
}
