package asia.cmg.f8.report.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.report.entity.database.CreditTransactionStatus;
import asia.cmg.f8.report.entity.database.CreditTransactionType;

public class WalletActivityDto {

	private Long id;
	
	@JsonProperty("credit_wallet_id")
	private Long creditWalletId;
	
	@JsonProperty("owner_uuid")
	private String ownerUuid;
	
	@JsonProperty("owner_image")
	private String ownerImage;

	@JsonProperty("partner_uuid")
	private String partnerUuid;

	@JsonProperty("partner_name")
	private String partnerName;
	
	@JsonProperty("partner_code")
	private String partnerCode;
	
	@JsonProperty("partner_image")
	private String partnerImage;
	
	@JsonProperty("credit_amount")
	private Integer creditAmount;

	@JsonProperty("transaction_type")
	private Integer transactionType;
	
	@JsonProperty("transaction_status")
	private Integer transactionStatus = 1;
	
	@JsonProperty("description_params")
	private List<String> descriptionParams;
	
	@JsonProperty("created_date")
	private long createdDate;
	
	
	/**
	 * @param id
	 * @param creditWalletId
	 * @param ownerUuid
	 * @param partnerUuid
	 * @param partnerName
	 * @param partnerCode
	 * @param partnerImage
	 * @param creditAmount
	 * @param transactionType
	 * @param transactionStatus
	 * @param description
	 * @param created_date
	 */
	public WalletActivityDto(Long id, Long creditWalletId, String ownerUuid, String ownerImage, String partnerUuid, String partnerName,
			String partnerCode, String partnerImage, Integer creditAmount, CreditTransactionType transactionType,
			CreditTransactionStatus transactionStatus, String descriptionParams, long created_date) {
		
		this.id = id;
		this.creditWalletId = creditWalletId;
		this.ownerUuid = ownerUuid;
		this.ownerImage = ownerImage;
		this.partnerUuid = partnerUuid;
		this.partnerName = partnerName;
		this.partnerCode = partnerCode;
		this.partnerImage = partnerImage;
		this.creditAmount = creditAmount;
		this.transactionType = transactionType.ordinal();
		this.transactionStatus = transactionStatus.ordinal();
		this.descriptionParams = this.parseDescriptionParams(descriptionParams);
		this.createdDate = created_date;
	}

	private List<String> parseDescriptionParams(String textSplitBySemicolon) {
		List<String> result = new ArrayList<String>();
		
		if(!StringUtils.isEmpty(textSplitBySemicolon)) {
			String[] array = textSplitBySemicolon.split(";");
			result = Arrays.asList(array);
		}
		
		return result;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreditWalletId() {
		return creditWalletId;
	}

	public void setCreditWalletId(Long creditWalletId) {
		this.creditWalletId = creditWalletId;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public String getPartnerUuid() {
		return partnerUuid;
	}

	public void setPartnerUuid(String partnerUuid) {
		this.partnerUuid = partnerUuid;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public String getPartnerImage() {
		return partnerImage;
	}

	public void setPartnerImage(String partnerImage) {
		this.partnerImage = partnerImage;
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Integer getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(Integer transactionType) {
		this.transactionType = transactionType;
	}

	public Integer getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(Integer transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public List<String> getDescriptionParams() {
		return descriptionParams;
	}

	public void setDescriptionParams(List<String> descriptionParams) {
		this.descriptionParams = descriptionParams;
	}

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	public String getOwnerImage() {
		return ownerImage;
	}

	public void setOwnerImage(String ownerImage) {
		this.ownerImage = ownerImage;
	}
}
