package asia.cmg.f8.report.dto;

import java.util.ArrayList;
import java.util.List;


public class OpeningHour {

	public List<ETicketOpenTimeDTO> openingHours = new ArrayList<ETicketOpenTimeDTO>();

	public List<ETicketOpenTimeDTO> getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(List<ETicketOpenTimeDTO> openingHours) {
		this.openingHours = openingHours;
	}
}
