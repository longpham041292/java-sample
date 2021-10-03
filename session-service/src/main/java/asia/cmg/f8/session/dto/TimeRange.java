package asia.cmg.f8.session.dto;

public class TimeRange {

    private Long fromDateTime;
    private Long toDateTime;

    public Long getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(final Long fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public Long getToDateTime() {
        return toDateTime;
    }

    public void setToDateTime(final Long toDateTime) {
        this.toDateTime = toDateTime;
    }
}
