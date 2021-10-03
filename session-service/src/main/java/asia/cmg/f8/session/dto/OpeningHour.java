package asia.cmg.f8.session.dto;

import java.util.ArrayList;
import java.util.List;

import asia.cmg.f8.session.dto.cms.ETicketOpenTimeDTO;

public class OpeningHour {

	public List<ETicketOpenTimeDTO> openingHours = new ArrayList<ETicketOpenTimeDTO>();

	public List<ETicketOpenTimeDTO> getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(List<ETicketOpenTimeDTO> openingHours) {
		this.openingHours = openingHours;
	}
}
