package asia.cmg.f8.commerce.api.admin;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.service.AutoWithdrawService;

@RestController
public class AutoWithdrawCoinAPI {
	private static final Logger LOGGER = LoggerFactory.getLogger(AutoWithdrawCoinAPI.class);
	
	@Autowired
	private AutoWithdrawService autoDeductService;
	/**
	 * Every 00:00 Monday, Auto withdrawal earning user credit of every PT
	 * @author phong
	 * @return
	 */
	@PostMapping(value = "/internal/v1/credit/booking/users/auto-withdrawal", produces = APPLICATION_JSON_UTF8_VALUE)
	public void autoWithdrawalUserCreditByWeeklyJob() {

		LOGGER.info("Start auto withdrawal wallet from job schedule");
		try {
			final LocalDateTime currentDate = LocalDateTime.now().withHour(0).withMinute(0).withMinute(0).withSecond(0).withNano(0);
			autoDeductService.handleAutoWithdrawalUserCreditByWeeklyJob(currentDate);
		} catch (Exception e) {
			LOGGER.error("[autoWithdrawalUserCreditJob] error detail: {}", e.getMessage());
		}
	}
	
	/**
	 * Auto deduct earning credit for all club
	 * @author phong
	 * @return
	 */
	@PostMapping(value = "/internal/v1/credit/booking/clubs/auto-withdrawal", produces = APPLICATION_JSON_UTF8_VALUE)
	public void autoWithdrawalClubCreditByWeeklyJob() {

		LOGGER.info("Start auto withdrawal wallet club from job schedule");
		try {
			final LocalDateTime currentDate = LocalDateTime.now().withHour(0).withMinute(0).withMinute(0).withSecond(0).withNano(0);
			autoDeductService.handleAutoWithdrawalClubCreditByWeeklyJob(currentDate);
		} catch (Exception e) {
			LOGGER.error("[autoWithdrawalClubCreditJob] error detail: {}", e.getMessage());
		}
	}
	
	@PostMapping(value = "/admin/v1/wallets/finance/users/auto-withdrawal", produces = APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('finance')")
	public void withdrawalUserCreditByWeeklyJob(@RequestParam(name = "end_time") final Long endTime) {

		LOGGER.info("Start auto withdrawal wallet from job schedule");
		try {
			final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
					TimeZone.getDefault().toZoneId());
			autoDeductService.handleAutoWithdrawalUserCreditByWeeklyJob(end);
		} catch (Exception e) {
			LOGGER.error("[autoWithdrawalUserCreditJob] error detail: {}", e.getMessage());
		}
	}
	
	/**
	 * Auto deduct earning credit for all club
	 * @author phong
	 * @return
	 */
	@PostMapping(value = "/admin/v1/wallets/finance/clubs/auto-withdrawal", produces = APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('finance')")
	public void withdrawalClubCreditByWeeklyJob( @RequestParam(name = "end_time") final Long endTime) {

		LOGGER.info("Start auto withdrawal wallet club from job schedule");
		try {
			final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
					TimeZone.getDefault().toZoneId());
			autoDeductService.handleAutoWithdrawalClubCreditByWeeklyJob(end);
		} catch (Exception e) {
			LOGGER.error("[autoWithdrawalClubCreditJob] error detail: {}", e.getMessage());
		}
	}
}
