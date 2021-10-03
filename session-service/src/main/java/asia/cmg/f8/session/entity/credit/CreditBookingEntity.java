package asia.cmg.f8.session.entity.credit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.BookingServiceType;

@Entity
@Table(name = "credit_bookings")
public class CreditBookingEntity {

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@JsonProperty("client_uuid")
	@Column(name = "client_uuid", length = 36, nullable = false)
    private String clientUuid = "";

	@JsonProperty("studio_uuid")
	@Column(name = "studio_uuid", length = 32)
	private String studioUuid;

	@JsonProperty("studio_name")
	@Column(name = "studio_name", length = 255)
	private String studioName;

	@JsonProperty("studio_address")
	@Column(name = "studio_address", length = 500)
	private String studioAddress;

	@JsonProperty("studio_picture")
	@Column(name = "studio_picture", length = 500)
	private String studioPicture;

	@JsonProperty("booked_by")
	@Column(name = "booked_by", length = 36)
    private String bookedBy;

	@JsonProperty("credit_amount")
	@Column(name = "credit_amount", columnDefinition = "int not null default 0")
	private Integer creditAmount = 0;

	@JsonProperty("booking_type")
	@Column(name = "booking_type")
	@Enumerated(EnumType.ORDINAL)
	private BookingServiceType bookingType;

	@Column(name = "status", columnDefinition = "int not null default 0")
    @Enumerated(EnumType.ORDINAL)
    private CreditBookingSessionStatus status = CreditBookingSessionStatus.BOOKED;

	@Column(name = "booking_day")
	private LocalDate bookingDay;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

	@UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "creditBooking", targetEntity = CreditETicketBookingEntity.class)
    List<CreditETicketBookingEntity> etickets = new ArrayList<CreditETicketBookingEntity>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "creditBooking", targetEntity = CreditClassBookingEntity.class)
    List<CreditClassBookingEntity> classes = new ArrayList<CreditClassBookingEntity>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "creditBooking", targetEntity = CreditSessionBookingEntity.class)
    List<CreditSessionBookingEntity> sessions = new ArrayList<CreditSessionBookingEntity>();
    
    @Column(name = "confirmation_code", length = 50)
    private String confirmationCode;					// Upon check-in successfully
    
    @JsonProperty("studio_cover")
    @Column(name = "studio_cover", length = 500)
    private String studioCover;

    @Transient
    private BasicUserEntity client;

	@Transient
	private CreditBookingTransactionEntity checkinTransaction;

	@JsonProperty("notification_reminded")
	@Column(name = "notification_reminded", columnDefinition = "boolean not null default 0")
	private Boolean notificationReminded = Boolean.FALSE;

	public Boolean getNotificationReminded() {
		return notificationReminded;
	}

	public void setNotificationReminded(Boolean notificationReminded) {
		this.notificationReminded = notificationReminded;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientUuid() {
		return clientUuid;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}

	public String getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(String bookedBy) {
		this.bookedBy = bookedBy;
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}

	public CreditBookingSessionStatus getStatus() {
		return status;
	}

	public void setStatus(CreditBookingSessionStatus status) {
		this.status = status;
	}

	public String getStudioUuid() {
		return studioUuid;
	}

	public void setStudioUuid(String studioUuid) {
		this.studioUuid = studioUuid;
	}

	public String getStudioName() {
		return studioName;
	}

	public void setStudioName(String studioName) {
		this.studioName = studioName;
	}

	public List<CreditETicketBookingEntity> getEtickets() {
		return etickets;
	}

	public void setEtickets(List<CreditETicketBookingEntity> etickets) {
		for (CreditETicketBookingEntity creditBookingETicketEntity : etickets) {
			this.etickets.add(creditBookingETicketEntity);
			creditBookingETicketEntity.setCreditBooking(this);
		}
	}

	public void addETicket(CreditETicketBookingEntity eTicket) {
		this.etickets.add(eTicket);
		eTicket.setCreditBooking(this);
	}

	public void addSession(CreditSessionBookingEntity session) {
		this.sessions.add(session);
		session.setCreditBooking(this);
	}

	public void addClass(CreditClassBookingEntity classEntity) {
		this.classes.add(classEntity);
		classEntity.setCreditBooking(this);
	}

	public BookingServiceType getBookingType() {
		return bookingType;
	}

	public void setBookingType(BookingServiceType bookingType) {
		this.bookingType = bookingType;
	}

	public List<CreditClassBookingEntity> getClasses() {
		return classes;
	}

	public void setClasses(List<CreditClassBookingEntity> classes) {
		for (CreditClassBookingEntity creditBookingClassEntity : classes) {
			this.classes.add(creditBookingClassEntity);
			creditBookingClassEntity.setCreditBooking(this);
		}
	}

	public List<CreditSessionBookingEntity> getSessions() {
		return sessions;
	}

	public void setSessions(List<CreditSessionBookingEntity> sessions) {
		for (CreditSessionBookingEntity creditSessionBookingEntity : sessions) {
			this.sessions.add(creditSessionBookingEntity);
			creditSessionBookingEntity.setCreditBooking(this);
		}
	}

	public LocalDate getBookingDay() {
		return bookingDay;
	}

	public void setBookingDay(LocalDate bookingDay) {
		this.bookingDay = bookingDay;
	}

	public String getStudioAddress() {
		return studioAddress;
	}

	public void setStudioAddress(String studioAddress) {
		this.studioAddress = studioAddress;
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

	public String getStudioPicture() {
		return studioPicture;
	}

	public void setStudioPicture(String studioPicture) {
		this.studioPicture = studioPicture;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public BasicUserEntity getClient() {
		return client;
	}
	
	public void setClient(BasicUserEntity client) {
		this.client = client;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public String getStudioCover() {
		return studioCover;
	}

	public void setStudioCover(String studioCover) {
		this.studioCover = studioCover;
	}

	public CreditBookingTransactionEntity getCheckinTransaction() {
		return checkinTransaction;
	}

	public void setCheckinTransaction(CreditBookingTransactionEntity checkinTransaction) {
		this.checkinTransaction = checkinTransaction;
	}
}
