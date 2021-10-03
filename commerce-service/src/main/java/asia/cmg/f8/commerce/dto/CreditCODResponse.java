package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditCODResponse {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("series_number")
	private String seriesNumber;

	@JsonProperty("cod_code")
	private String codCode;

	public CreditCODResponse() {
		super();
	}

	public CreditCODResponse(Long id, String seriesNumber, String codCode) {
		super();
		this.id = id;
		this.seriesNumber = seriesNumber;
		this.codCode = codCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getCodCode() {
		return codCode;
	}

	public void setCodCode(String codCode) {
		this.codCode = codCode;
	}

}
