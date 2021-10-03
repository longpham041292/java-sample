package asia.cmg.f8.commerce.api.admin;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.dto.CreditWalletDTO;
import asia.cmg.f8.commerce.dto.UserCreditFlowStatisticDto;
import asia.cmg.f8.commerce.dto.WalletActivityDto;
import asia.cmg.f8.commerce.dto.WalletCashFlow;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageEntity;
import asia.cmg.f8.commerce.service.WalletService;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.util.PagedResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;


@RestController
public class AdminWalletApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminWalletApi.class);
    private static final String START_TIME = "from";
    private static final String END_TIME = "to";
    
	@Autowired
	private WalletService walletService;

	/**
	 * Get wallet information by uuid
	 * @author phong
	 * @param uuid
	 * @return
	 */
	@GetMapping(value = "/admin/v1/wallets/credits/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> getCreditWalletByUuid(@PathVariable(name = "uuid") final String uuid) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {

			CreditWalletDTO walletDTO = walletService.getWalletDTOByOwnerUuid(uuid);
			if (walletDTO == null) {
				CreditWalletEntity walletEntity = new CreditWalletEntity(uuid, Boolean.TRUE);
				walletEntity = walletService.createWallet(walletEntity);
				walletDTO = walletService.getWalletDTOByOwnerUuid(uuid);
			}

			UserCreditPackageEntity nextExpiredPackage = walletService.getNextExpiredPackage(uuid);
			if (nextExpiredPackage != null) {
				CreditWalletDTO.ExpiredCreditPackage expiredPackageDTO = walletDTO.new ExpiredCreditPackage();
				expiredPackageDTO.setExpiredDate(
						nextExpiredPackage.getExpiredDate().atZone(ZoneId.systemDefault()).toEpochSecond());
				expiredPackageDTO
						.setExpiredCredit(nextExpiredPackage.getTotalCredit() - nextExpiredPackage.getUsedCredit());
				walletDTO.setNextExpiredPackage(expiredPackageDTO);
			}
			apiResponse.setData(walletDTO);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}


	/**
	 * get wallet activity by  user uuid
	 * @author phong
	 * @param uuid
	 * @param cashFlow
	 * @param pageable
	 * @return
	 */
	@GetMapping(value = "/admin/v1/wallets/users/{uuid}/activities", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> getActivitiesWithCashFlowFilterByUuid(@PathVariable(name = "uuid") final String uuid,
			@RequestParam(name = "cashFlow", required = false, defaultValue = "ALL") WalletCashFlow cashFlow,
			@PageableDefault(page = 0, size = 20) Pageable pageable) {

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			Page<WalletActivityDto> pagedResult = walletService.getWalletActivities(uuid, cashFlow, pageable);
			PagedResponse<WalletActivityDto> result = new PagedResponse<WalletActivityDto>();
			result.setCount((int) pagedResult.getTotalElements());
			result.setEntities(pagedResult.getContent());

			apiResponse.setData(result);
		} catch (Exception e) {
			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	/**
	 * Get earning statistic for earning chart
	 * @author phong
	 * @param ptUuid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value = "/admin/v1/wallets/statistic/trainers/{ptUuid}/earning", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> getTrainerEarningByRangeTime(@PathVariable("ptUuid") String ptUuid,
															@RequestParam(name = START_TIME) final Long startTime,
															@RequestParam(name = END_TIME) final Long endTime) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		
		final List<UserCreditFlowStatisticDto> earningData = walletService.getAllCreditSesionsBookingOfTrainer(start, end, ptUuid);
		
		return new ResponseEntity<>(earningData, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/v1/wallets/statistic/users/{uuid}/spending", method = RequestMethod.GET)
	@RequiredAdminRole
	public ResponseEntity<?> getClientSpendingByRangeTime(@PathVariable("uuid") String ptUuid,
															@RequestParam(name = START_TIME) final Long startTime,
															@RequestParam(name = END_TIME) final Long endTime) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
		
		final List<UserCreditFlowStatisticDto> spendingData = walletService.getClientSpendingByRangeTime(start, end, ptUuid);
		
		return new ResponseEntity<>(spendingData, HttpStatus.OK);
	}
}
