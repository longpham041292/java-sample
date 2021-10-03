package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;

public class CreditWalletDTO {

	@JsonProperty("owner_uuid")
	private String ownerUuid;
	
	@JsonProperty("owner_name")
	private String ownerName;
	
	@JsonProperty("total_credit")
	private Integer totalCredit = 0;
	
	@JsonProperty("available_credit")
	private Integer availableCredit = 0;
	
	@JsonProperty("level")
	private CreditWalletLevel level = CreditWalletLevel.DEFAULT;
	
	@JsonProperty("accumulation_amount")
	private Double accumulationAmount = 0d;
	
	@JsonProperty("active")
	private Boolean active;
	
	@JsonProperty("next_expired_package")
	private ExpiredCreditPackage nextExpiredPackage;
	
	public class ExpiredCreditPackage {
		@JsonProperty("expired_date")
		private Long expiredDate;
		
		@JsonProperty("expired_credit")
		private Integer expiredCredit;
		
		public Long getExpiredDate() {
			return expiredDate;
		}
		
		public void setExpiredDate(Long expiredDate) {
			this.expiredDate = expiredDate;
		}
		
		public Integer getExpiredCredit() {
			return expiredCredit;
		}
		
		public void setExpiredCredit(Integer expiredCredit) {
			this.expiredCredit = expiredCredit;
		}
	}

	public CreditWalletDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public CreditWalletDTO(CreditWalletEntity entity, final String ownerName) {
		ownerUuid = entity.getOwnerUuid();
		this.ownerName = ownerName;
		totalCredit = entity.getTotalCredit();
		availableCredit = entity.getAvailableCredit();
		level = entity.getLevel();
		active = entity.getActive();
	}
	
	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Integer getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(Integer totalCredit) {
		this.totalCredit = totalCredit;
	}

	public Integer getAvailableCredit() {
		return availableCredit;
	}

	public void setAvailableCredit(Integer availableCredit) {
		this.availableCredit = availableCredit;
	}

	public CreditWalletLevel getLevel() {
		return level;
	}

	public void setLevel(CreditWalletLevel level) {
		this.level = level;
	}

	public Double getAccumulationAmount() {
		return accumulationAmount;
	}

	public void setAccumulationAmount(Double accumulationAmount) {
		this.accumulationAmount = accumulationAmount;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public ExpiredCreditPackage getNextExpiredPackage() {
		return nextExpiredPackage;
	}

	public void setNextExpiredPackage(ExpiredCreditPackage nextExpiredPackage) {
		this.nextExpiredPackage = nextExpiredPackage;
	}
}
