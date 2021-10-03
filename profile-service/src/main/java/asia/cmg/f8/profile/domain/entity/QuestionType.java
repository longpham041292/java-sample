package asia.cmg.f8.profile.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuestionType {
    @JsonProperty("multi")
    MULTI,
    @JsonProperty("single")
    SINGLE,
    @JsonProperty("guide")
    GUIDE;
}
