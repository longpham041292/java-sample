package asia.cmg.f8.commerce.entity.credit;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user_credit_packages_notifications")
public class UserCreditPackagesNotificationsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate;

	@UpdateTimestamp
	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_credit_package_id", nullable = false)
	private UserCreditPackageEntity userCreditPackage;

	@Column(name = "expired_remaining_day")
	private Integer expiredRemainingDay;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserCreditPackageEntity getUserCreditPackage() {
		return userCreditPackage;
	}

	public void setUserCreditPackage(UserCreditPackageEntity userCreditPackage) {
		this.userCreditPackage = userCreditPackage;
	}

	public Integer getExpiredRemainingDay() {
		return expiredRemainingDay;
	}

	public void setExpiredRemainingDay(Integer expiredRemainingDay) {
		this.expiredRemainingDay = expiredRemainingDay;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

}
