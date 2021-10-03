package asia.cmg.f8.session.dto.cms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CMSClassBookingDTO {

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
		private long id;
		
		private String name;
		
		@JsonProperty("start_at")
		private Long startTime;
		
		@JsonProperty("end_at")
		private Long endTime;
		
		@JsonProperty("instructor_name")
		private String instructorName;
		
		@JsonProperty("credit_amount")
		private Integer creditAmount;
		
		@JsonProperty("total_slots")
		private Integer totalSlots;
		
		private CMSStudioDTO studio;
		
		private CMSClassCourseDTO course;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getStartTime() {
			return startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public long getEndTime() {
			return endTime;
		}

		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}

		public String getInstructorName() {
			return instructorName;
		}

		public void setInstructorName(String instructorName) {
			this.instructorName = instructorName;
		}

		public int getCreditAmount() {
			return creditAmount;
		}

		public void setCreditAmount(int creditAmount) {
			this.creditAmount = creditAmount;
		}

		public int getTotalSlots() {
			return totalSlots;
		}

		public void setTotalSlots(int totalSlots) {
			this.totalSlots = totalSlots;
		}

		public CMSStudioDTO getStudio() {
			return studio;
		}

		public void setStudio(CMSStudioDTO studio) {
			this.studio = studio;
		}

		public CMSClassCourseDTO getCourse() {
			return course;
		}

		public void setCourse(CMSClassCourseDTO course) {
			this.course = course;
		}
	}
}
