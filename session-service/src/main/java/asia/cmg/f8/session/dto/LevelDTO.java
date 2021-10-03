package asia.cmg.f8.session.dto;

public class LevelDTO {
	private String code;
	private Integer ptBookingCredit;
	private Double ptCommission;
	private Double ptBurnedCommission;
	
	public LevelDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public LevelDTO(String code, int ptBookingCredit, double ptCommission, double ptBurnedCommission) {
		this.code = code;
		this.ptBookingCredit = ptBookingCredit;
		this.ptCommission = ptCommission;
		this.ptBurnedCommission = ptBurnedCommission;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getPtBookingCredit() {
		return ptBookingCredit;
	}
	public void setPtBookingCredit(Integer ptBookingCredit) {
		this.ptBookingCredit = ptBookingCredit;
	}
	public Double getPtCommission() {
		return ptCommission;
	}
	public void setPtCommission(Double ptCommission) {
		this.ptCommission = ptCommission;
	}
	public Double getPtBurnedCommission() {
		return ptBurnedCommission;
	}
	public void setPtBurnedCommission(Double ptBurnedCommission) {
		this.ptBurnedCommission = ptBurnedCommission;
	}
}
