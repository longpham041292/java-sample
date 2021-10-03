package asia.cmg.f8.commerce.api;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.client.ProfileClient;
import asia.cmg.f8.commerce.client.SessionClient;
import asia.cmg.f8.commerce.dto.CmsCreateWalletRequest;
import asia.cmg.f8.commerce.dto.CreditWalletDTO;
import asia.cmg.f8.commerce.dto.RecentPartnerDTO;
import asia.cmg.f8.commerce.dto.StudioDto;
import asia.cmg.f8.commerce.dto.SuggestedTrainersDTO;
import asia.cmg.f8.commerce.dto.WalletActivityDto;
import asia.cmg.f8.commerce.dto.WalletCashFlow;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageEntity;
import asia.cmg.f8.commerce.service.StudioService;
import asia.cmg.f8.commerce.service.WalletService;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredCmsRole;
import asia.cmg.f8.common.util.PagedResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import one.util.streamex.StreamEx;

@RestController
public class WalletApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(WalletApi.class);
	private static final String UUID = "uuid";
    private static final String START_TIME = "from";
    private static final String END_TIME = "to";
    private static final String[] BOOKING_HISTORY_EXPORT_HEADERS = {"date", "time", "partnerName", "serviceType", "serviceFee", "trainerCommission", "status"};
    
    private static final String[] CASH_IN_HISTORY_EXPORT_HEADERS = {"transactionDate","clientName" ,"userName", "paymentMethod", "creditPackageName", "creditAmount", "exchangeMoney", "packageDuration", "expiredDate", "promotionCode"};
    
    private static final String[] WALLET_ACTIVITY_EXPORT_HEADERS = {"creditWalletId","ownerUuid" ,"partnerName", "creditAmount", "transactionType", "transactionStatus", "descriptionParams", "createdDate"};
    

	@Autowired
	private WalletService walletService;

	@Autowired
	private StudioService studioService;

	@Autowired
	private SessionClient sessionClient;

	@Autowired
	private ProfileClient profileClient;

	@PostMapping(value = "/mobile/v1/wallets/studios/{uuid}/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> studioCheckin(@PathVariable(name = UUID, required = true) final String studio_uuid,
			final Account account) {

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			// Check the studio if existed
			StudioDto studio = studioService.getStudioByUuid(studio_uuid);
			if (studio == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Checkin Failed! Studio not existed!"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}

			// Check the balance of credit
			CreditWalletEntity walletEntity = walletService.getWalletByOwnerUuid(account.uuid());
			if (walletEntity == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Checkin Failed! Wallet not existed!"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}

			// Check if user enough credit for checkin
			if (walletEntity.getAvailableCredit() < studio.getCheckinCredit()) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Checkin Failed! Wallet not existed!"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}

			walletEntity = walletService.studioCheckin(studio, account);

			if (walletEntity == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Trainsaction Failed!"));
			}

			// Checkin Success
			apiResponse.setData(walletEntity);

		} catch (Exception e) {
			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/mobile/v1/wallets/activities", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getActivities(Pageable pageable, final Account account) {

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			apiResponse.setData(walletService.getWalletActivities(account.uuid(), pageable));

		} catch (Exception e) {
			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/mobile/v2/wallets/activities", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getActivitiesWithCashFlowFilter(
			@RequestParam(name = "cashFlow", required = false, defaultValue = "ALL") WalletCashFlow cashFlow,
			@PageableDefault(page = 0, size = 20) Pageable pageable, final Account account) {

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			Page<WalletActivityDto> pagedResult = walletService.getWalletActivities(account.uuid(), cashFlow, pageable);
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

	@GetMapping(value = "/mobile/v1/wallets/credits", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getOwnerWallet(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {

			CreditWalletDTO walletDTO = walletService.getWalletDTOByOwnerUuid(account.uuid());
			if (walletDTO == null) {
				CreditWalletEntity walletEntity = new CreditWalletEntity(account.uuid(), Boolean.TRUE);
				walletEntity = walletService.createWallet(walletEntity);
				walletDTO = walletService.getWalletDTOByOwnerUuid(account.uuid());
			}

			UserCreditPackageEntity nextExpiredPackage = walletService.getNextExpiredPackage(account.uuid());
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

	@RequiredCmsRole
	@PostMapping(value = "/public/cms/v1/credit/wallets", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createCMSStudioWallet(@RequestBody CmsCreateWalletRequest request) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			CreditWalletEntity wallet = walletService.createWallet(request);
			apiResponse.setData(wallet);

		} catch (Exception e) {
			String error = String.format("Create wallet of owner %s failed", request.getStudioUuid());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(error));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@RequiredCmsRole
	@GetMapping(value = "/public/cms/v1/credit/wallets/{id}/owner/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getCMSStudioWallet(@PathVariable(name = "id") final long id,
			@PathVariable(name = "uuid") final String ownerUuid) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			CreditWalletEntity wallet = walletService.getCMSStudioWallet(id, ownerUuid);
			apiResponse.setData(wallet);

		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * Ticket: LEEP-2770
	 */
	@GetMapping(value = "/mobile/v1/wallets/credits/suggested-trainers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getSuggestedTrainers(@RequestParam("latitude") double latitude,
													   @RequestParam("longitude") double longitude,
													   @RequestParam(name = "page", defaultValue = "0") int page,
													   @RequestParam(name = "per_page", defaultValue = "10") int perPage,
													   Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		List<SuggestedTrainersDTO> result = new ArrayList<SuggestedTrainersDTO>();
		try {
			int lack = 0;
			if (account.isEu()) {
				List<RecentPartnerDTO> recentBookedTraines = this.getRecentBookedPartners(account.uuid(), page, perPage);
				result.addAll(recentBookedTraines
									.stream()
									.map(trainer -> {return new SuggestedTrainersDTO(trainer.getUuid(),
																					trainer.getUsername(),
																					trainer.getFullName(),
																					trainer.getAvartar(),
																					trainer.getLevel(),
																					trainer.getPtBookingCredit());})
									.collect(Collectors.toList()));

				lack = perPage - result.size();
				if (lack > 0) {
					result.addAll(this.getNearestTrainers(account.uuid(), latitude, longitude, page, lack));
				}
			} else {
				result.addAll(this.getNearestTrainers(account.uuid(), latitude, longitude, page, perPage));
			}
			lack = perPage - result.size();
			List<String> ptUuid = result.stream().map(dto -> dto.getUuid()).collect(Collectors.toList());
			if (lack > 0) {
				result.addAll(this.getPtiMatch(account.uuid(), ptUuid, lack));
			}
			result = StreamEx.of(result).distinct(SuggestedTrainersDTO::getUuid).toList();
			apiResponse.setData(result);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	private List<RecentPartnerDTO> getRecentBookedPartners(String euUuid, int page, int size) {
		List<RecentPartnerDTO> result = new ArrayList<RecentPartnerDTO>();
		result = sessionClient.getRecentBookedTrainers(euUuid, page, size);
		return result;
	}

	private List<SuggestedTrainersDTO> getNearestTrainers(String euUuid, double latitude, double longitude, int page,
			int size) {
		return profileClient.getNearestTrainersByLocation(euUuid, latitude, longitude, page, size);
	}
	
	/*
	 * Get list Pt iMatch but exclude from list ptUuid
	 * @author Long
	 */
	private List<SuggestedTrainersDTO> getPtiMatch(String euUuid, List<String> listPtUuid, int size) {
		return profileClient.findPtiMatch(euUuid, listPtUuid, size);
	}
}
