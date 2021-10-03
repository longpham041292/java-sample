package asia.cmg.f8.commerce.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import asia.cmg.f8.commerce.dto.onepay.PspType;

@Entity
@Table(name = "onepay_instruments")
public class OnepayInstrumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "instrument_id", nullable = false)
	private String instrumentId;
	
	@Column(name = "user_id", nullable = false)
	private Long onepayUserId;

	@Column(name = "type")
	private String type;

	@Column(name = "name")
	private String name;

	@Column(name = "number")
	private String number;

	@Column(name = "number_hash")
	private String numberHash;

	@Column(name = "created_date", nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	@Column(name = "merchant_id")
	private String merchantId;

	@Column(name = "merchant_txn_ref")
	private String merchantTxnRef;

	@Column(name = "token_id")
	private String tokenId;

	@Column(name = "token_expire_month")
	private Integer tokenExpireMonth;

	@Column(name = "token_expire_year")
	private Integer tokenExpireYear;

	@Column(name = "token_cvv")
	private String tokenCvv;

	@Column(name = "token_number")
	private String tokenNumber;

	@Column(name = "psp_id")
	@Enumerated(EnumType.STRING)
	private PspType pspId;

	@Column(name = "brand_id")
	private String brandId;

	@Column(name = "brand_name")
	private String brandName;

	public Long getOnepayUserId() {
		return onepayUserId;
	}

	public void setOnepayUserId(Long onepayUserId) {
		this.onepayUserId = onepayUserId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getNumberHash() {
		return numberHash;
	}

	public void setNumberHash(String numberHash) {
		this.numberHash = numberHash;
	}

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

	public Integer getTokenExpireMonth() {
		return tokenExpireMonth;
	}

	public void setTokenExpireMonth(Integer tokenExpireMonth) {
		this.tokenExpireMonth = tokenExpireMonth;
	}

	public Integer getTokenExpireYear() {
		return tokenExpireYear;
	}

	public void setTokenExpireYear(Integer tokenExpireYear) {
		this.tokenExpireYear = tokenExpireYear;
	}

	public String getTokenCvv() {
		return tokenCvv;
	}

	public void setTokenCvv(String tokenCvv) {
		this.tokenCvv = tokenCvv;
	}

	public String getTokenNumber() {
		return tokenNumber;
	}

	public void setTokenNumber(String tokenNumber) {
		this.tokenNumber = tokenNumber;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public PspType getPspId() {
		return pspId;
	}

	public void setPspId(PspType pspId) {
		this.pspId = pspId;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}
}
