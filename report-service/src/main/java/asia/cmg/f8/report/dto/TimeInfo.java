package asia.cmg.f8.report.dto;

public class TimeInfo {

    private final long startTime;
    private final long middleTime;
    private final long endTime;

    public TimeInfo(final long startTime, final long middleTime, final long endTime) {
        super();
        this.startTime = startTime;
        this.middleTime = middleTime;
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getMiddleTime() {
        return middleTime;
    }

    public long getEndTime() {
        return endTime;
    }


}
