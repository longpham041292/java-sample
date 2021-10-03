package asia.cmg.f8.session.entity.credit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "credit_wallets")
public class BasicWalletEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
		
	@Column(name = "owner_uuid", nullable = false, length = 50)
	private String ownerUuid;
	
	@Column(name = "total_credit")
	private Integer totalCredit = 0;
	
	@Column(name = "available_credit")
	private Integer availableCredit = 0;
	
	@Column(name = "active")
	private Boolean active;
	
	public BasicWalletEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public BasicWalletEntity(final String ownerUuid, final Boolean active) {
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
}
