package asia.cmg.f8.report.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.report.entity.database.BookingServiceType;
import asia.cmg.f8.report.entity.database.CreditBookingEntity;


public class BookingHistoryDTO {

	@JsonProperty("date")
	private String date;

	@JsonProperty("time")
	private String time;

	@JsonProperty("partner_name")
	private String partnerName;

	@JsonProperty("service_type")
	private BookingServiceType serviceType;

	@JsonProperty("credit_amount")
	private Integer creditAmount;

	@JsonProperty("service_fee")
	private double serviceFee;

	@JsonProperty("commission")
	private double commission;

	@JsonProperty("status")
	private CreditBookingSessionStatus status;

	public BookingHistoryDTO(String date, String time, String partnerName, BookingServiceType serviceType,
			Integer creditAmount, double serviceFee, double commission, CreditBookingSessionStatus status) {
		super();
		this.date = date;
		this.time = time;
		this.partnerName = partnerName;
		this.serviceType = serviceType;
		this.creditAmount = creditAmount;
		this.serviceFee = serviceFee;
		this.commission = commission;
		this.status = status;
	}

	public BookingHistoryDTO(CreditBookingEntity c) {
		this.date = getDate(c.getStartTime());
		this.time = getTime(c.getStartTime());
		this.partnerName = c.getStudioName();
		this.serviceType = c.getBookingType();
		this.creditAmount = c.getCreditAmount();
		this.serviceFee = getServiceFee(c);
		this.commission = this.creditAmount - this.serviceFee;
		this.status = c.getStatus();
	}

	public BookingHistoryDTO() {
		super();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public BookingServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(BookingServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}

	public double getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(double serviceFee) {
		this.serviceFee = serviceFee;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public CreditBookingSessionStatus getStatus() {
		return status;
	}

	public void setStatus(CreditBookingSessionStatus status) {
		this.status = status;
	}

	private String getDate(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return date.format(formatter);
	}

	private String getTime(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return date.format(formatter);
	}

	private double getServiceFee(CreditBookingEntity creditBookingEntity) {
		double sumServiceFee = 0;
		switch (creditBookingEntity.getBookingType()) {
		case CLASS:
			sumServiceFee = creditBookingEntity.getClasses().stream()
					.mapToDouble(clas -> clas.getServiceFee() * creditBookingEntity.getCreditAmount()).sum();
			break;
		case ETICKET:
			sumServiceFee = creditBookingEntity.getEtickets().stream()
					.mapToDouble(etick -> etick.getServiceFee() * creditBookingEntity.getCreditAmount()).sum();
			break;
		case SESSION:
			sumServiceFee = creditBookingEntity.getSessions().stream()
					.mapToDouble(ses -> ses.getServiceFee() * creditBookingEntity.getCreditAmount()).sum();
			break;
		default:
			break;
		}
		return sumServiceFee;
	}

}
