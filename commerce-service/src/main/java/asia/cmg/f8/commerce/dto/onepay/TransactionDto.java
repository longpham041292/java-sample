package asia.cmg.f8.commerce.dto.onepay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionDto {
	@JsonProperty(value = "psp_id")
	private PspType pspId;

	@JsonProperty(value = "merchant_id")
	private String merchantId;

	@JsonProperty(value = "merchant_txn_ref")
	private String merchantTxtRef;

	public TransactionDto(final PspType pspId, final String merchantId, final String merchantTxtRef) {
		this.pspId = pspId;
		this.merchantId = merchantId;
		this.merchantTxtRef = merchantTxtRef;
	}

	public PspType getPspId() {
		return pspId;
	}

	public void setPspId(PspType pspId) {
		this.pspId = pspId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantTxtRef() {
		return merchantTxtRef;
	}

	public void setMerchantTxtRef(String merchantTxtRef) {
		this.merchantTxtRef = merchantTxtRef;
	}

}
