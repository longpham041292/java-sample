package asia.cmg.f8.session.entity.credit;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "credit_class_bookings")
public class CreditClassBookingEntity {

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name = "credit_booking_id")
	private CreditBookingEntity creditBooking;
	
	@JsonProperty("course_id")
	@Column(name = "course_id", columnDefinition = "bigint not null default 0")
	private Long courseId = 0L;
	
	@JsonProperty("service_id")
	@Column(name = "service_id", columnDefinition = "bigint not null default 0")
	private Long serviceId = 0L;			// Class Id
	
	@JsonProperty("service_name")
	@Column(name = "service_name", length = 255)
	private String serviceName;		// Class name
	
	@JsonProperty("service_fee")
	@Column(name = "service_fee", columnDefinition = "double not null default 0")
	private Double serviceFee = 0d;
	
	@JsonProperty("no_show_fee")
	@Column(name = "no_show_fee", columnDefinition = "double not null default 0")
	private Double noShowFee = 0d;
	
//	@JsonProperty("start_time")
//	@Column(name = "start_time")
//	private LocalDateTime startTime;
//	
//	@JsonProperty("end_time")
//	@Column(name = "end_time")
//	private LocalDateTime endTime;
	
	@CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CreditBookingEntity getCreditBooking() {
		return creditBooking;
	}

	public void setCreditBooking(CreditBookingEntity creditBooking) {
		this.creditBooking = creditBooking;
	}

	public long getServiceId() {
		return serviceId;
	}

	public void setServiceId(long serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Double getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(Double serviceFee) {
		this.serviceFee = serviceFee;
	}

	public Double getNoShowFee() {
		return noShowFee;
	}

	public void setNoShowFee(Double noShowFee) {
		this.noShowFee = noShowFee;
	}

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	

//	public LocalDateTime getStartTime() {
//		return startTime;
//	}
//
//	public void setStartTime(LocalDateTime startTime) {
//		this.startTime = startTime;
//	}
//
//	public LocalDateTime getEndTime() {
//		return endTime;
//	}
//
//	public void setEndTime(LocalDateTime endTime) {
//		this.endTime = endTime;
//	}
}
