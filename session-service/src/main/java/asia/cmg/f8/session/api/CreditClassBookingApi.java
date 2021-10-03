package asia.cmg.f8.session.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.dto.CMSSessionValidatingResponse;
import asia.cmg.f8.session.service.CreditBookingService;

@RestController
public class CreditClassBookingApi {

	@Autowired
	private CreditBookingService creditBookingService;

	/**
	 * Ticket LEEP-2891
	 * @author Duyet Pham
	 * @param sessionIds
	 * @param account
	 * @return
	 */
	@GetMapping(value = "/cms/v1/wallets/credit/booking/classes/count-booked", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CMSSessionValidatingResponse> countBookingForClasses(@RequestParam(name = "session_ids") List<Long> sessionIds, final Account account) {
		CMSSessionValidatingResponse checkingResult = null;
		try {
			checkingResult = creditBookingService.countBookingAndcheckReservedForServices(account, sessionIds);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return new ResponseEntity<CMSSessionValidatingResponse>(checkingResult, HttpStatus.OK);
	}
}
