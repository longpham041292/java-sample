package asia.cmg.f8.session.wrapper.dto;


public class TrainerRevenueDaily {
    private Integer year;
    private String ptUuid;
    private Double ptFee;
    private Double commission;

    public TrainerRevenueDaily(final Integer year, final String ptUuid, final Double ptFee,
            final Double commission) {
        super();
        this.year = year;
        this.ptUuid = ptUuid;
        this.ptFee = ptFee;
        this.commission = commission;
    }

    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(final Integer year) {
        this.year = year;
    }

    public Double getPtFee() {
        return ptFee;
    }

    public void setPtFee(final Double ptFee) {
        this.ptFee = ptFee;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(final Double commission) {
        this.commission = commission;
    }


}
