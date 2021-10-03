package asia.cmg.f8.session.dto.cms;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CMSETicketDTO {
	
	private Status status;
	
	private Data data;
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public class Status {
		private int code;
		private String error;
		private String detail;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getError() {
			return error;
		}
		public void setError(String error) {
			this.error = error;
		}
		public String getDetail() {
			return detail;
		}
		public void setDetail(String detail) {
			this.detail = detail;
		}
	};
	
	public class Data {
		@JsonProperty("id")
		private Integer id;
		@JsonProperty("name")
		private String name;
		@JsonProperty("all_day")
		private Boolean allDay;
		@JsonProperty("open_times")
		private List<ETicketOpenTimeDTO> openTimes = new ArrayList<ETicketOpenTimeDTO>();
		@JsonProperty("credit_amount")
		private Integer creditAmount;
		@JsonProperty("service_fee_percent")
		private Double serviceFee;
		@JsonProperty("payable_no_show_fee_percent")
		private Double payableNoShowFee;
		@JsonProperty("studio")
		private EticketStudioDto studio;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean isAllDay() {
			return allDay;
		}

		public void setAllDay(Boolean allDay) {
			this.allDay = allDay;
		}

		public List<ETicketOpenTimeDTO> getOpenTimes() {
			return openTimes;
		}

		public void setOpenTimes(List<ETicketOpenTimeDTO> openTimes) {
			this.openTimes = openTimes;
		}

		public Integer getCreditAmount() {
			return creditAmount;
		}

		public void setCreditAmount(Integer creditAmount) {
			this.creditAmount = creditAmount;
		}


		public Double getServiceFee() {
			return serviceFee == null ? 0 : serviceFee;
		}

		public void setServiceFee(Double serviceFee) {
			this.serviceFee = serviceFee;
		}

		public Double getPayableNoShowFee() {
			return payableNoShowFee == null ? 0 : payableNoShowFee;
		}

		public void setPayableNoShowFee(Double payableNoShowFee) {
			this.payableNoShowFee = payableNoShowFee;
		}

		public EticketStudioDto getStudio() {
			return studio;
		}

		public void setStudio(EticketStudioDto studio) {
			this.studio = studio;
		}
	}
}
