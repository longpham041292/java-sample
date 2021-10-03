package asia.cmg.f8.report.dto;

public class CounterResponse {

    private String name;
    private String total;
    private Integer trend;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(final String total) {
        this.total = total;
    }

    public Integer getTrend() {
        return trend;
    }

    public void setTrend(final Integer trend) {
        this.trend = trend;
    }

}
