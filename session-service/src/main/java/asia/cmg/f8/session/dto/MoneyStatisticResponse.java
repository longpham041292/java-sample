package asia.cmg.f8.session.dto;

public class MoneyStatisticResponse {

    private String name;
    private String total;
    private Integer trend;

    public MoneyStatisticResponse() {
        // empty
    }

    public MoneyStatisticResponse(final String name, final String total, final Integer trend) {
        super();
        this.name = name;
        this.total = total;
        this.trend = trend;
    }

    public String getName() {
        return name;
    }

    public String getTotal() {
        return total;
    }

    public Integer getTrend() {
        return trend;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setTotal(final String total) {
        this.total = total;
    }

    public void setTrend(final Integer trend) {
        this.trend = trend;
    }

}
