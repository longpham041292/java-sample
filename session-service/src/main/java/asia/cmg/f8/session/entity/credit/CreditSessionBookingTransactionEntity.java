package asia.cmg.f8.session.entity.credit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import asia.cmg.f8.session.entity.AbstractEntity;
import asia.cmg.f8.session.entity.SessionAction;

@Entity
@Table(name = "credit_session_booking_transactions")
public class CreditSessionBookingTransactionEntity extends AbstractEntity {

	@ManyToOne
	@JoinColumn(name = "credit_session_booking_id")
	@JsonIgnore
	private CreditSessionBookingEntity creditSessionBooking;
	
	@Column(name = "old_status")
	@Enumerated(EnumType.ORDINAL)
	private CreditBookingSessionStatus oldStatus;
	
	@Column(name = "new_status")
	@Enumerated(EnumType.ORDINAL)
    private CreditBookingSessionStatus newStatus;
    
	@Column(name = "old_pt")
    private String oldPtUuid;
    
	@Column(name = "new_pt")
    private String newPtUuid;
    
	@Column(name = "action")
    private SessionAction action;

	public CreditSessionBookingEntity getCreditSessionBooking() {
		return creditSessionBooking;
	}

	public void setCreditSessionBooking(CreditSessionBookingEntity creditSessionBooking) {
		this.creditSessionBooking = creditSessionBooking;
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

	public String getOldPtUuid() {
		return oldPtUuid;
	}

	public void setOldPtUuid(String oldPtUuid) {
		this.oldPtUuid = oldPtUuid;
	}

	public String getNewPtUuid() {
		return newPtUuid;
	}

	public void setNewPtUuid(String newPtUuid) {
		this.newPtUuid = newPtUuid;
	}

	public SessionAction getAction() {
		return action;
	}

	public void setAction(SessionAction action) {
		this.action = action;
	}
}
