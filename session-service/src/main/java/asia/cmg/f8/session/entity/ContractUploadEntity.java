package asia.cmg.f8.session.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "contract_upload_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ContractUploadEntity {

	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
    @Column(name = "member_barcode", nullable = false)
    private String memberBarcode;
    
    @Column(name = "create_date")
    private String createDate;
	
    @Column(name = "start_date")
    private String startDate;
	
    @Column(name = "expiration_date")
    private String expirationDate; 
	
    @Column(name = "contract_number")
    private String contractNumber;
	
    private String type;
	
    @Column(name = "item_group") 
    private String itemGroup;
	
    private String status;
	
    @Column(name = "price_per_session")
    private Double pricePerSession;
	
    @Column(name = "total_amount")
    private Double totalAmount;
	
    @Column(name = "total_session")
    private Integer totalSession;
	
    @Column(name = "total_used")
    private Integer totalUsed;
	
    @Column(name = "total_remain")
    private Integer totalRemain;
	
    @Column(name = "trainer_code")
    private String trainerCode;
	
    
    @Column(name = "contract_import")
    private Boolean contractImport;
    
    public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getMemberBarcode() {
		return memberBarcode;
	}

	public void setMemberBarcode(final String memberBarcode) {
		this.memberBarcode = memberBarcode;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(final String createDate) {
		this.createDate = createDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(final String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(final String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(final String itemGroup) {
		this.itemGroup = itemGroup;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public Double getPricePerSession() {
		return pricePerSession;
	}

	public void setPricePerSession(final Double pricePerSession) {
		this.pricePerSession = pricePerSession;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(final Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getTotalSession() {
		return totalSession;
	}

	public void setTotalSession(final Integer totalSession) {
		this.totalSession = totalSession;
	}

	public Integer getTotalUsed() {
		return totalUsed;
	}

	public void setTotalUsed(final Integer totalUsed) {
		this.totalUsed = totalUsed;
	}

	public Integer getTotalRemain() {
		return totalRemain;
	}

	public void setTotalRemain(final Integer totalRemain) {
		this.totalRemain = totalRemain;
	}

	public String getTrainerCode() {
		return trainerCode;
	}

	public void setTrainerCode(final String trainerCode) {
		this.trainerCode = trainerCode;
	}

	public Boolean getContractImport() {
		return contractImport;
	}

	public void setContractImport(final Boolean contractImport) {
		this.contractImport = contractImport;
	}
	
	
   
}
