package asia.cmg.f8.common.spec.view;

import asia.cmg.f8.common.spec.commerce.Product;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Represent a materialized view of sold {@link Product}. This view is used for admin
 * <p>
 * Refer story: https://ssl.cmg.asia/docs/display/MOR/Web+Portal+-+Admin+-+PT+Overview
 * <p>
 * Created on 11/17/16.
 */
public interface OrderView4Admin {

    @JsonProperty("trainer_uuid")
    String getTrainerUuid();

    @JsonProperty("user_uuid")
    String getUserUuid();

    @JsonProperty("picture")
    String getPicture();

    @JsonProperty("user_name")
    String getUserName();

    @JsonProperty("product_uuid")
    String getProductUuid();

    @JsonProperty("total_sessions")
    int getTotalSessions();

    @JsonProperty("burned_sessions")
    int getBurnedSessions();

    @JsonProperty("commission")
    int getCommission();

    @JsonProperty("expired_date")
    Date getExpiredDate();
}
