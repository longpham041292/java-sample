package asia.cmg.f8.report.entity.database;

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
@Table(name = "credit_eticket_bookings")
public class CreditETicketBookingEntity {

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name = "credit_booking_id")
	private CreditBookingEntity creditBooking;
	
	@JsonProperty("service_id")
	@Column(name = "service_id")
	private long serviceId;
	
	@JsonProperty("service_name")
	@Column(name = "service_name", length = 255)
	private String serviceName;
	
	@JsonProperty("service_fee")
	@Column(name = "service_fee", columnDefinition = "double not null default 0")
	private Double serviceFee = 0d;
	
	@JsonProperty("no_show_fee")
	@Column(name = "no_show_fee", columnDefinition = "double not null default 0")
	private Double noShowFee = 0d;
	
	@JsonProperty("is_all_day")
	@Column(name = "is_all_day", columnDefinition = "tinyint not null default 0")
	private boolean isAllDay = false;
	
	@JsonProperty("open_hours")
	@Column(name = "opening_hours", length = 250)
	private String openingHours;
	
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

	public CreditBookingEntity getCreditBooking() {
		return creditBooking;
	}

	public void setCreditBooking(CreditBookingEntity creditBooking) {
		this.creditBooking = creditBooking;
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

	public boolean isAllDay() {
		return isAllDay;
	}

	public void setAllDay(boolean isAllDay) {
		this.isAllDay = isAllDay;
	}

	public String getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(String openingHours) {
		this.openingHours = openingHours;
	}
}
