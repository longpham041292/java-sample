package asia.cmg.f8.common.spec.report;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TimeRange {
    @JsonProperty("week")
    WEEK,
    @JsonProperty("month")
    MONTH,
    @JsonProperty("year")
    YEAR,
    @JsonProperty("day")
    DAY;
}
