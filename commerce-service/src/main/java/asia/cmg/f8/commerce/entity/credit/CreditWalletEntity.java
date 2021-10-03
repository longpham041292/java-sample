package asia.cmg.f8.commerce.entity.credit;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "credit_wallets", uniqueConstraints = @UniqueConstraint(name = "ownerUuid_partner_UN", columnNames = {"owner_uuid", "partner"}))
public class CreditWalletEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
		
	@Column(name = "owner_uuid", nullable = false, length = 50)
	@JsonProperty("owner_uuid")
	private String ownerUuid;
	
	@Column(name = "owner_name", length = 255)
	@JsonProperty("owner_name")
	private String ownerName;
	
	@Column(name = "total_credit", columnDefinition = "int not null default 0")
	@JsonProperty("total_credit")
	private Integer totalCredit = 0;
	
	@Column(name = "available_credit", columnDefinition = "int not null default 0")
	@JsonProperty("available_credit")
	private Integer availableCredit = 0;
	
	@Column(name = "level", columnDefinition = "int not null default 0")
	@JsonProperty("level")
	@Enumerated(EnumType.ORDINAL)
	private CreditWalletLevel level = CreditWalletLevel.DEFAULT;
	
	@Column(name = "accumulation_amount", columnDefinition = "double not null default 0")
	@JsonProperty("accumulation_amount")
	private Double accumulationAmount = 0d;
	
	@Column(name = "active", columnDefinition = "boolean not null default 1")
	private Boolean active = true;
	
	@Column(name = "partner", columnDefinition = "int default 0")
	@Enumerated(EnumType.ORDINAL)
	private Partner partner = Partner.LEEP;
	
	@CreationTimestamp
	@JsonIgnore
	private LocalDateTime created_date;
	
	@UpdateTimestamp
	@JsonIgnore
	private LocalDateTime modified_date;
		
	public CreditWalletEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public CreditWalletEntity(final String ownerUuid, final Boolean active) {
		this.ownerUuid = ownerUuid;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
}
