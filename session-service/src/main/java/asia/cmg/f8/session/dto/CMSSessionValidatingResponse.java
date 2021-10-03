package asia.cmg.f8.session.dto;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CMSSessionValidatingResponse {
	@JsonProperty
	private HashMap<Long, long[]> sessions;

	@JsonProperty("available_credit")
	private int availableCredit;

	public HashMap<Long, long[]> getSessions() {
		return sessions;
	}

	public void setSessions(HashMap<Long, long[]> sessions) {
		this.sessions = sessions;
	}

	public int getAvailableCredit() {
		return availableCredit;
	}

	public void setAvailableCredit(int availableCredit) {
		this.availableCredit = availableCredit;
	}
}
