package asia.cmg.f8.session.entity.credit;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import asia.cmg.f8.session.entity.SessionAction;

@Entity
@Table(name = "credit_booking_transactions")
public class CreditBookingTransactionEntity {

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name = "credit_booking_id")
	@JsonIgnore
	private CreditBookingEntity creditBooking;
	
	@Column(name = "old_status")
	@Enumerated(EnumType.ORDINAL)
	private CreditBookingSessionStatus oldStatus;
	
	@Column(name = "new_status")
	@Enumerated(EnumType.ORDINAL)
    private CreditBookingSessionStatus newStatus;
    
	@Column(name = "action")
    private SessionAction action;

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

	public CreditBookingSessionStatus getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(CreditBookingSessionStatus oldStatus) {
		this.oldStatus = oldStatus;
	}

	public CreditBookingSessionStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(CreditBookingSessionStatus newStatus) {
		this.newStatus = newStatus;
	}

	public SessionAction getAction() {
		return action;
	}

	public void setAction(SessionAction action) {
		this.action = action;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
}
