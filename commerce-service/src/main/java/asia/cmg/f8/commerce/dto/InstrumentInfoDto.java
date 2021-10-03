package asia.cmg.f8.commerce.dto;

import javax.persistence.Column;

import asia.cmg.f8.commerce.dto.onepay.PspType;

public class InstrumentInfoDto {

	@Column(name = "id")
	private Long id;

	@Column(name = "brand_name")
	private String brandName;

	@Column(name = "number")
	private String number;

	@Column(name = "psp_id")
	private PspType pspId;

	public InstrumentInfoDto(final Long id, final String brandName, final String number, final PspType pspId) {
		this.id = id;
		this.brandName = brandName;
		this.number = number;
		this.pspId = pspId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public PspType getPspId() {
		return pspId;
	}

	public void setPspId(PspType pspId) {
		this.pspId = pspId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
