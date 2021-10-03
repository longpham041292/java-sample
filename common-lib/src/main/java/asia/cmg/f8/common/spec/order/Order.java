package asia.cmg.f8.common.spec.order;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import asia.cmg.f8.common.spec.Identifiable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface Order extends Identifiable {

    @JsonProperty("code")
    String getCode();

    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
    @Nullable
    Date getCreatedDate();

    @JsonProperty("sub_total")
    Double getSubTotal();

    @JsonProperty("total_price")
    Double getTotalPrice();

    @JsonProperty("currency")
    String getCurrency();

    @JsonProperty("user_uuid")
    String getUserUuid();

    @JsonProperty("pt_uuid")
    String getPtUuid();

    @JsonProperty("order_status")
    OrderStatus getOrderStatus();

    @JsonProperty("payment_status")
    PaymentStatus getPaymentStatus();

    @JsonProperty("created_time")
    @Nullable
    Long getCreatedTime();
    
    @JsonProperty("free_order")
    @Nullable
    Boolean getFreeOrder();

    @JsonProperty("products")
    List<? extends OrderEntry> getProducts();
}
