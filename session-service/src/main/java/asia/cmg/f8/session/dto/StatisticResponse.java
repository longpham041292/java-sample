package asia.cmg.f8.session.dto;

public class StatisticResponse {

    private String name;
    private Integer total;
    private Integer trend;

    public StatisticResponse() {
        // empty
    }

    public StatisticResponse(final String name, final Integer total, final Integer trend) {
        super();
        this.name = name;
        this.total = total;
        this.trend = trend;
    }

    public String getName() {
        return name;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getTrend() {
        return trend;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setTotal(final Integer total) {
        this.total = total;
    }

    public void setTrend(final Integer trend) {
        this.trend = trend;
    }

}
