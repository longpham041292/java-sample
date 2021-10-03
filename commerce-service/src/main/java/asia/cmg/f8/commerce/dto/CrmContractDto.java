package asia.cmg.f8.commerce.dto;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrmContractDto {
	private @Nullable String contractNumber;
	private String euCode;
	private @Nullable String expireDate;
	private int numOfSessions;
	private double ptServiceFree;
	private String ptCode;

	@JsonProperty("contract_number")
	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	@JsonProperty("eu_code")
	public String getEuCode() {
		return euCode;
	}

	public void setEuCode(String euCode) {
		this.euCode = euCode;
	}

	@JsonProperty("expire_date")
	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	@JsonProperty("num_of_sessions")
	public int getNumOfSessions() {
		return numOfSessions;
	}

	public void setNumOfSessions(int numOfSessions) {
		this.numOfSessions = numOfSessions;
	}

	@JsonProperty("pt_service_fee")
	public double getPtServiceFree() {
		return ptServiceFree;
	}

	public void setPtServiceFree(double ptServiceFree) {
		this.ptServiceFree = ptServiceFree;
	}

	@JsonProperty("pt_code")
	public String getPtCode() {
		return ptCode;
	}

	public void setPtCode(String ptCode) {
		this.ptCode = ptCode;
	}

}
