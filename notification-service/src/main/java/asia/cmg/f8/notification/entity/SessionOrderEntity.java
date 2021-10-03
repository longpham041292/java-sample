package asia.cmg.f8.notification.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/4/17.
 */
public class SessionOrderEntity {

    @JsonProperty("order_uuid")
    private String orderId;

    @JsonProperty("number_of_burned")
    private Integer sessionBurned;

    @JsonProperty("number_of_session")
    private Integer totalSession;

    public SessionOrderEntity() {
        //Empty constructor
    }

    public SessionOrderEntity(final String orderId,
                              final Integer sessionBurned,
                              final Integer totalSession) {
        this.orderId = orderId;
        this.sessionBurned = sessionBurned;
        this.totalSession = totalSession;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    public Integer getSessionBurned() {
        return sessionBurned;
    }

    public void setSessionBurned(final Integer sessionBurned) {
        this.sessionBurned = sessionBurned;
    }

    public Integer getTotalSession() {
        return totalSession;
    }

    public void setTotalSession(final Integer totalSession) {
        this.totalSession = totalSession;
    }
}
