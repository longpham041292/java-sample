package asia.cmg.f8.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.report.entity.database.BookingServiceType;


public class CreditBookingResponse<T> {

	@JsonProperty("service_type")
	private BookingServiceType serviceType;
	
	@JsonProperty("booking_data")
	private T bookingData;
	
	@JsonProperty("pay_extended_credit")
	private Integer payExtendedCredit;
	
	@JsonProperty("pay_extended_money")
	private Double payExtendedMoney;

	public BookingServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(BookingServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public T getBookingData() {
		return bookingData;
	}

	public void setBookingData(T bookingData) {
		this.bookingData = bookingData;
	}

	public Integer getPayExtendedCredit() {
		return payExtendedCredit;
	}

	public void setPayExtendedCredit(Integer payExtendedCredit) {
		this.payExtendedCredit = payExtendedCredit;
	}

	public Double getPayExtendedMoney() {
		return payExtendedMoney;
	}

	public void setPayExtendedMoney(Double payExtendedMoney) {
		this.payExtendedMoney = payExtendedMoney;
	}
}
