package asia.cmg.f8.session.dto;


public class AvailabilitiesPreferTimeRequest {

    private Integer month;

    private Integer year;

    private Long startTime;

    private Long endTime;

    private Long startDate;

    private Long endDate;

    public Integer getMonth() {
        return month;
    }

    public void setMonth(final Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(final Integer year) {
        this.year = year;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(final Long endTime) {
        this.endTime = endTime;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(final Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(final Long endDate) {
        this.endDate = endDate;
    }
}
