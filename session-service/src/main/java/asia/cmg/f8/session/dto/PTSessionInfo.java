package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * @author tung.nguyenthanh
 */
public class PTSessionInfo {

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("session_burned")
    @Nullable
    private Integer sessionBurned;

    @JsonProperty("session_number")
    private Integer sessionNumber;

    @JsonProperty("expired_date")
    @Nullable
    private Long expireDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("price")
    private String price;

	@JsonProperty("total_price")
	private Double totalPrice;

    @JsonProperty("commission")
    private Double commission;

    @JsonProperty("package_session_uuid")
    private String packageSessionUuid;

    @JsonProperty("order_code")
    private String orderCode;

	@JsonProperty("order_uuid")
	private String orderUuid;

	@JsonProperty("contract_number")
	private String contractNumber;

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getOrderUuid() {
		return orderUuid;
	}

	public void setOrderUuid(String orderUuid) {
		this.orderUuid = orderUuid;
	}

	public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public Integer getSessionBurned() {
        return sessionBurned;
    }

    public void setSessionBurned(final Integer sessionBurned) {
        this.sessionBurned = sessionBurned;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(final Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(final Long expireDate) {
        this.expireDate = expireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(final Double commission) {
        this.commission = commission;
    }

    public String getPackageSessionUuid() {
        return packageSessionUuid;
    }

    public void setPackageSessionUuid(final String packageSessionUuid) {
        this.packageSessionUuid = packageSessionUuid;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(final String orderCode) {
        this.orderCode = orderCode;
    }

}
