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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author khoa.bui
 *
 */
@Entity
@Table(name = "Promotion_Usage")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PromotionUsageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "couponCode", nullable = false)
	private String couponCode;
	
	@Column(name = "userUuid", nullable = false)
	private String userUuid;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(final String couponCode) {
		this.couponCode = couponCode;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(final String userUuid) {
		this.userUuid = userUuid;
	}
	
}
