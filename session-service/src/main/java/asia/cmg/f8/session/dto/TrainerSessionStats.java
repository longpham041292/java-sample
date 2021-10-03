package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author tung.nguyenthanh
 *
 */
public class TrainerSessionStats {

    @JsonProperty("time")
    private final long time;

    @JsonProperty("amount")
    private final double amount;

    private final String currency;

    public TrainerSessionStats(final long time, final double amount,
            final String currency) {
        super();
        this.time = time;
        this.amount = amount;
        this.currency = currency;
    }

    public long getTime() {
        return time;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

}
