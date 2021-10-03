/**
 * 
 */
package asia.cmg.f8.commerce.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author khoa.bui
 *
 */
@Entity
@Table(name = "Promotion", uniqueConstraints = @UniqueConstraint(columnNames = { "couponCode" }))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PromotionEntity {

	@Column(name = "couponCode", nullable = false)
	private String couponCode;
	@Column(name = "endDate", nullable = false)
	private Long endDate;
	@Column(name = "startedDate", nullable = false)
	private Long startedDate;
	@Column(name = "discount")
	private Double discount;
	@Column(name = "freeSession")
	private Integer freeSession;
	@Column(name = "active", nullable = false)
	private Boolean active;
	@Column(name = "visibility")
	private Boolean visibility;
	@Column(name = "maxTotalUsage")
	private Integer maxTotalUsage;
	@Column(name = "maxIndividualUsage")
	private Integer maxIndividualUsage;
	@Column(name = "description")
	private String desc;
	@Column(name = "appliedGroup")
	private String appliedGroup;
	
	@Column(name = "pt_commission", columnDefinition = "double default 0")
	private Double ptCommission = 0d;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(final String couponCode) {
		this.couponCode = couponCode;
	}
	public Long getEndDate() {
		return endDate;
	}
	public void setEndDate(final Long endDate) {
		this.endDate = endDate;
	}
	public Long getStartedDate() {
		return startedDate;
	}
	public void setStartedDate(final Long startedDate) {
		this.startedDate = startedDate;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(final Double discount) {
		this.discount = discount;
	}
	public Integer getFreeSession() {
		return freeSession;
	}
	public void setFreeSession(final Integer freeSession) {
		this.freeSession = freeSession;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(final Boolean active) {
		this.active = active;
	}
	public Boolean getVisibility() {
		return visibility;
	}
	public void setVisibility(final Boolean visibility) {
		this.visibility = visibility;
	}
	public Long getId() {
		return id;
	}
	public void setId(final Long id) {
		this.id = id;
	}
	public Integer getMaxTotalUsage() {
		return maxTotalUsage;
	}
	public void setMaxTotalUsage(final Integer maxTotalUsage) {
		this.maxTotalUsage = maxTotalUsage;
	}
	public Integer getMaxIndividualUsage() {
		return maxIndividualUsage;
	}
	public void setMaxIndividualUsage(final Integer maxIndividualUsage) {
		this.maxIndividualUsage = maxIndividualUsage;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(final String desc) {
		this.desc = desc;
	}
	public String getAppliedGroup() {
		return appliedGroup;
	}
	public void setAppliedGroup(final String appliedGroup) {
		this.appliedGroup = appliedGroup;
	}
	public Double getPtCommission() {
		return ptCommission;
	}
	public void setPtCommission(Double ptCommission) {
		this.ptCommission = ptCommission;
	}
}
