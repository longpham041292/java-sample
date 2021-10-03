package asia.cmg.f8.commerce.dto.onepay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequestDto {
	@JsonProperty(value = "merchant_id")
	private String merchantId;

	@JsonProperty(value = "merchant_txn_ref")
	private String merchantTxnRef;

	@JsonProperty(value = "user_id")
	private String userId;

	@JsonProperty(value = "amount")
	private Double amount;

	@JsonProperty(value = "currency")
	private String currency;

	private PaymentOrderRequestDto order;

	private PaymentTokenRequestDto token;

	@JsonProperty(value = "return_url")
	private String returnUrl;
	
	@JsonProperty(value = "cancel_url")
	private String cancelUrl;
	
	private PaymentTerminalRequestDto terminal;

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantTxnRef() {
		return merchantTxnRef;
	}

	public void setMerchantTxnRef(String merchantTxnRef) {
		this.merchantTxnRef = merchantTxnRef;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public PaymentOrderRequestDto getOrder() {
		return order;
	}

	public void setOrder(PaymentOrderRequestDto order) {
		this.order = order;
	}

	public PaymentTokenRequestDto getToken() {
		return token;
	}

	public void setToken(PaymentTokenRequestDto token) {
		this.token = token;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	public PaymentTerminalRequestDto getTerminal() {
		return terminal;
	}

	public void setTerminal(PaymentTerminalRequestDto terminal) {
		this.terminal = terminal;
	}

}
