package asia.cmg.f8.session.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "user_subscriptions")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserSubscriptionEntity extends AbstractEntity {
	
	@Column(name = "order_uuid", length = 36, nullable = false)
	private String orderUuid;
	
	@Column(name = "subscription_id", nullable = false)
	private Long subscriptionId;
	
	@Column(name = "start_time", nullable = false)
	private LocalDateTime startTime;
	
	@Column(name = "end_time", nullable = false)
	private LocalDateTime endTime;
	
	@Column(name = "eu_uuid", length = 36, nullable = false)
	private String euUuid;
	
	@Column(name = "pt_uuid", length = 36, nullable = false)
	private String ptUuid;
	
	@Column(name = "number_of_month", length = 100, nullable = true)
	private Integer numberOfMonth;
	
	@Column(name = "limit_day")
	private Integer limitDay;
	
	public String getOrderUuid() {
		return orderUuid;
	}

	public void setOrderUuid(String orderUuid) {
		this.orderUuid = orderUuid;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}


	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	
	public String getEuUuid() {
		return euUuid;
	}

	public void setEuUuid(String euUuid) {
		this.euUuid = euUuid;
	}

	
	public String getPtUuid() {
		return ptUuid;
	}

	public void setPtUuid(String ptUuid) {
		this.ptUuid = ptUuid;
	}

	
	public Integer getNumberOfMonth() {
		return numberOfMonth;
	}

	public void setNumberOfMonth(Integer numberOfMonth) {
		this.numberOfMonth = numberOfMonth;
	}

	public Integer getLimitDay() {
		return limitDay;
	}

	public void setLimitDay(Integer limitDay) {
		this.limitDay = limitDay;
	}

	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

}
