package asia.cmg.f8.report.api;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.FileExportUtils;
import asia.cmg.f8.common.util.PagedResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.report.dto.ClubProfitSharingReportDTO;
import asia.cmg.f8.report.dto.DeductCoinClubDTO;
import asia.cmg.f8.report.dto.DeductCoinTrainerDTO;
import asia.cmg.f8.report.dto.TopUpReportDto;
import asia.cmg.f8.report.dto.TrainerProfitSharingReportDTO;
import asia.cmg.f8.report.dto.TransactionReportDto;
import asia.cmg.f8.report.entity.database.ClubOutcomeEntity;
import asia.cmg.f8.report.entity.database.UserOutcomeEntity;
import asia.cmg.f8.report.entity.database.WalletCashFlow;
import asia.cmg.f8.report.service.FinanceReportService;

@RestController
public class FinanceReportApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceReportApi.class);
	private static final String START_TIME = "from";
	private static final String END_TIME = "to";
	private static final String CLUB_PROFIT_SHARING_REPORT = "club_profit_sharing_report.csv";
	private static final String TRAINER_PROFIT_SHARING_REPORT = "trainer_profit_sharing_report.csv";
	private static final String DEDUCT_COIN_CLUB_REPORT = "deduct_coin_club_report.csv";
	private static final String DEDUCT_COIN_TRAINER_REPORT = "deduct_coin_trainer_report.csv";

	private static final String[] WALLET_CASH_IN_EXPORT_HEADERS = { "orderCode", "transactionDate", "clientName", "userName",
			"paymentMethod", "couponSerial" , "creditPackageName", "creditAmount", "exchangeMoney", "originalPrice", "packageDuration", "expiredDate",
			"promotionCode" };
	private static final String[] CLIENT_TRANSACTION_EXPORT_HEADERS = { "transactionDate", "bookingId", "clientName",
			"clientUsername", "transactionType", "transactionStatus", "partnerName", "partnerUsername","studioName", "studioAddress", "classServiceName",
			"creditAmount", "bookingDate" };
	
	private static final String[] CLUB_TRANSACTION_EXPORT_HEADERS = { "transactionDate", "bookingId", "clientName",
			"clientUsername", "transactionType", "transactionStatus", "studioName", "studioAddress", "classServiceName",
			"creditAmount", "bookingDate" };
	private static final String[] CLUB_PROFIT_SHARING_REPORT_HEADER = { "clubName", "clubUserName", "clubClass",
			"clubType", "businessLicense", "businessTaxCode", "totalAmount", "vatAmount", "amountExcludeVat", "leepFee",
			"clubCoinEarn", "clubGrossIncome", "taxWitholding", "clubNetIncome", "representativeOffice", "clubAddress",
			"emailAddress", "phoneNumber", "beneficiaryName", "bankAccountNumber", "bankName", "bankBranch",
			"transactionConfirmStatus", "paymentStatus", "toDate" };
	private static final String[] TRAINER_PROFIT_SHARING_REPORT_HEADER = { "ptName", "ptUsername", "ptClass",
			"totalAmount", "vatAmount", "amountExcludeVat", "leepFee", "ptCoinEarn", "ptGrossIncome", "taxWitholding",
			"ptNetIncome", "amountReturn", "fullName", "idNumber", "pitTaxCode", "emailAddress", "phoneNumber",
			"location", "bankAccountNumber", "bankName", "bankBranch", "paymentStatus", "toDate" };
	private static final String[] DEDUCT_COIN_CLUB_REPORT_HEADER = { "clubName", "clubUsername", "clubClass",
			"clubType", "businessLicense", "businessTaxCode", "earnCoin", "topUp", "usedCoin", "balanceCoin",
			"deductCoin", "amountReturn", "beneficiaryName", "bankAccountNumber", "bankName", "bankBranch",
			"tracsactionStatus", "email", "phone", "paymentStatus", "paymentDate", "toDate" };
	private static final String[] DEDUCT_COIN_TRAINER_REPORT_HEADER = { "ptName", "ptUsername", "ptClass", "idNumber",
			"pitTaxCode", "earnCoin", "topUp", "usedCoin", "balanceCoin", "deductCoin", "exchange", "beneficiaryName",
			"bankAccountNumber", "bankName", "bankBranch", "email", "phone", "paymentStatus", "paymentDate", "toDate" };

	@Autowired
	private FinanceReportService walletService;

	/**
	 * Wallet Activities for all user in the app (TOP UP). Withdraw will apply in
	 * later
	 * 
	 * @author phong
	 * @param cashFlow startTime endTime pageable response
	 * @return
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/activities", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public ResponseEntity<Object> getWalletActivitiesAllUser(
			@RequestParam(name = "cashFlow", required = false, defaultValue = "ALL") WalletCashFlow cashFlow,
			@RequestParam(name = START_TIME) final Long startTime, @RequestParam(name = END_TIME) final Long endTime,
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@PageableDefault(page = 0, size = 20) Pageable pageable) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			switch (cashFlow) {
			case TOP_UP:
				Page<TopUpReportDto> pagedResult = walletService.getTopUpActivitiesAllUser(start, end, keyword,
						pageable);
				PagedResponse<TopUpReportDto> result = new PagedResponse<TopUpReportDto>();
				result.setCount((int) pagedResult.getTotalElements());
				result.setEntities(pagedResult.getContent());
				apiResponse.setData(result);
				break;
			default:
				apiResponse.setData(null);
				break;
			}

		} catch (Exception e) {
			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * Export Wallet Activities of all user in the app (TOP UP) to csv file
	 * 
	 * @author phong
	 * @param cashFlow startTime endTime pageable response
	 * @return
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/activities/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public StreamingResponseBody exportWalletActivities(
			@RequestParam(name = "cashFlow", required = false, defaultValue = "ALL") WalletCashFlow cashFlow,
			@RequestParam(name = START_TIME) final Long startTime, @RequestParam(name = END_TIME) final Long endTime,
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			final HttpServletResponse response) {
		final String filename = "wallet_activity_report.csv";
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		try {
			switch (cashFlow) {
			case TOP_UP:
				Page<TopUpReportDto> serviceHistory = walletService.getTopUpActivitiesAllUser(start, end, keyword,
						pageable);
				List<TopUpReportDto> listServiceHistory = serviceHistory.getContent();
				return FileExportUtils.exportCSV(listServiceHistory, WALLET_CASH_IN_EXPORT_HEADERS, filename, response);
			default:
				return null;
			}

		} catch (Exception e) {
			LOGGER.error("[exportBookingHistory] failed: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * All Transaction Report Clients (cash in, cash out, top up)
	 * 
	 * @author phong
	 * 
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public ResponseEntity<Object> getTransactionReport(@RequestParam(name = START_TIME) final Long startTime,
																		@RequestParam(name = END_TIME) final Long endTime,
																		@RequestParam(name = "userType")  final UserType userType,
																		@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
																		@PageableDefault(page = 0, size = 20 ) Pageable pageable) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			switch (userType) {
			case EU:
				Page<TransactionReportDto> pagedResult = walletService.getClientWalletActivitiesReport(start, end, keyword, pageable);
				PagedResponse<TransactionReportDto> result = new PagedResponse<TransactionReportDto>();
				result.setCount((int) pagedResult.getTotalElements());
				result.setEntities(pagedResult.getContent());
				apiResponse.setData(result);
				break;

			case PT:
				Page<TransactionReportDto> trainerPagedResult = walletService.getTrainerWalletActivitiesReport(start, end, keyword, pageable);
				PagedResponse<TransactionReportDto> trainerResult = new PagedResponse<TransactionReportDto>();
				trainerResult.setCount((int) trainerPagedResult.getTotalElements());
				trainerResult.setEntities(trainerPagedResult.getContent());
				apiResponse.setData(trainerResult);
				break;
				
			default:
				break;
			}
			
		} catch (Exception e) {
			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * Export transaction report for clients (cash in, cash out, top up)
	 * 
	 * @author phong
	 * 
	 */

	@GetMapping(value = "/admin/v1/wallets/finance/transactions/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public StreamingResponseBody exportTransactionReport(@RequestParam(name = "cashFlow", required = false, defaultValue = "ALL") WalletCashFlow cashFlow,
															@RequestParam(name = START_TIME) final Long startTime,
															@RequestParam(name = END_TIME) final Long endTime,
															@RequestParam(name = "userType")  final UserType userType,
															@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
															final HttpServletResponse response) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), TimeZone.getDefault().toZoneId());
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		try {
			final String filename = "client_transaction_report.csv";
			switch (userType) {
			case EU:
				Page<TransactionReportDto> pagedResult = walletService.getClientWalletActivitiesReport(start, end, keyword, pageable);
				List<TransactionReportDto> result = pagedResult.getContent();
				return FileExportUtils.exportCSV(result, CLIENT_TRANSACTION_EXPORT_HEADERS, filename, response);

			case PT:
				Page<TransactionReportDto> trainerPagedResult = walletService.getTrainerWalletActivitiesReport(start, end, keyword, pageable);
				List<TransactionReportDto> trainerResult = trainerPagedResult.getContent();
				return FileExportUtils.exportCSV(trainerResult, CLIENT_TRANSACTION_EXPORT_HEADERS, filename, response);
			default:
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("[exportBookingHistory] failed: {}", e.getMessage());
			return null;
		}
	}
	
	/**
	 * All Club Report Clients (cash in, cash out, top up)
	 * @author phong
	 * 
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/clubs/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public ResponseEntity<Object> getClubTransactionReport(@RequestParam(name = START_TIME) final Long startTime,
																		@RequestParam(name = END_TIME) final Long endTime,
																		@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
																		@PageableDefault(page = 0, size = 20 ) Pageable pageable) {

		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), TimeZone.getDefault().toZoneId());
		
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<TransactionReportDto> pagedResult = walletService.getClubWalletActivitiesReport(start, end, keyword, pageable);
//			PagedResponse<ClientWalletTransactionReportDto> result = new PagedResponse<ClientWalletTransactionReportDto>();
//			result.setCount((int) pagedResult.getTotalElements());
//			result.setEntities(pagedResult.getContent());
			apiResponse.setData(pagedResult);			
		} catch (Exception e) {
			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping(value = "/admin/v1/wallets/finance/clubs/transaction/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public StreamingResponseBody getClubTransactionReportExport(@RequestParam(name = START_TIME) final Long startTime,
																		@RequestParam(name = END_TIME) final Long endTime,
																		@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
																		final HttpServletResponse response) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), TimeZone.getDefault().toZoneId());
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), TimeZone.getDefault().toZoneId());
		
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		final String filename = "club_transaction_report.csv";
		try {
			
			List<TransactionReportDto> pagedResult = walletService.getClubWalletActivitiesReport(start, end, keyword, pageable);
			return FileExportUtils.exportCSV(pagedResult, CLUB_TRANSACTION_EXPORT_HEADERS, filename, response);		
		} catch (Exception e) {
			LOGGER.error("[exportClubTransaction] failed: {}", e.getMessage());
			return null;
		}
	}
	
	@PutMapping(value = "/admin/v1/wallets/users/outcome/payment-status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public ResponseEntity<Object> updateUserOutComePaymentStatus(@PathVariable(name = "id") Long id) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			UserOutcomeEntity outcome =  walletService.updateUserOutComePaymentStatus(id);
			if(outcome == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Can not find user outcome or payment status is paid"));
			}
		} catch (Exception e) {
			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@PutMapping(value = "/admin/v1/wallets/clubs/outcome/payment-status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public ResponseEntity<Object> updateClubOutComePaymentStatus(@PathVariable(name = "id") Long id) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			ClubOutcomeEntity outcome =  walletService.updateClubOutComePaymentStatus(id);
			if(outcome == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Can not find club outcome or payment status is paid"));
			}
		} catch (Exception e) {
			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * @apiNote get club profit sharing report
	 * @author long
	 * @email longpham@leep.app
	 * @param startTime, endTime, name, page, size
	 * @return ResponseEntity<Object>
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/club-profit-sharing", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public ResponseEntity<Object> getClubProfitSharingReport(
			@RequestParam(value = "startTime", required = true) long startTime,
			@RequestParam(value = "endTime", required = true) long endTime,
			@RequestParam(value = "name", required = false) String name,
			@PageableDefault(page = 0, size = 20) Pageable pageable) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			PagedResponse<ClubProfitSharingReportDTO> clubProfitSharing = walletService
					.getClubProfitSharingReport(startTime, endTime, name, pageable);
			apiResponse.setData(clubProfitSharing);
		} catch (Exception e) {
			LOGGER.error("[getClubProfitSharingReport] failed: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * @apiNote export club profit sharing report
	 * @author long
	 * @email longpham@leep.app
	 * @param startTime, endTime, name, page, size
	 * @return csv file
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/club-profit-sharing/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public StreamingResponseBody exportClubProfitSharingReport(
			@RequestParam(value = "startTime", required = true) long startTime,
			@RequestParam(value = "endTime", required = true) long endTime,
			@RequestParam(value = "name", required = false) String name, final HttpServletResponse response) {
		try {
			Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
			PagedResponse<ClubProfitSharingReportDTO> clubProfitSharing = walletService
					.getClubProfitSharingReport(startTime, endTime, name, pageable);
			List<ClubProfitSharingReportDTO> listTrainerProfitSharingReport = clubProfitSharing.getEntities();
			return FileExportUtils.exportCSV(listTrainerProfitSharingReport, CLUB_PROFIT_SHARING_REPORT_HEADER,
					CLUB_PROFIT_SHARING_REPORT, response);
		} catch (Exception e) {
			LOGGER.error("[exportClubProfitSharingReport] failed: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * @apiNote get trainer profit sharing report
	 * @author long
	 * @email longpham@leep.app
	 * @param startTime, endTime, name, page, size
	 * @return ResponseEntity<Object>
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/trainer-profit-sharing", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public ResponseEntity<Object> getTrainerProfitSharingReport(
			@RequestParam(value = "startTime", required = true) long startTime,
			@RequestParam(value = "endTime", required = true) long endTime,
			@RequestParam(value = "name", required = false) String name,
			@PageableDefault(page = 0, size = 20) Pageable pageable) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			PagedResponse<TrainerProfitSharingReportDTO> trainerProfitSharing = walletService
					.getTrainerProfitSharingReport(startTime, endTime, name, pageable);
			apiResponse.setData(trainerProfitSharing);
		} catch (Exception e) {
			LOGGER.error("[getTrainerProfitSharingReport] failed: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * @apiNote export trainer profit sharing report
	 * @author long
	 * @email longpham@leep.app
	 * @param startTime, endTime, name, page, size
	 * @return csv file
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/trainer-profit-sharing/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public StreamingResponseBody exportTrainerProfitSharingReport(
			@RequestParam(value = "startTime", required = true) long startTime,
			@RequestParam(value = "endTime", required = true) long endTime,
			@RequestParam(value = "name", required = false) String name, final HttpServletResponse response) {
		try {
			Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
			PagedResponse<TrainerProfitSharingReportDTO> trainerProfitSharing = walletService
					.getTrainerProfitSharingReport(startTime, endTime, name, pageable);
			List<TrainerProfitSharingReportDTO> listTrainerProfitSharingReport = trainerProfitSharing.getEntities();
			return FileExportUtils.exportCSV(listTrainerProfitSharingReport, TRAINER_PROFIT_SHARING_REPORT_HEADER,
					TRAINER_PROFIT_SHARING_REPORT, response);
		} catch (Exception e) {
			LOGGER.error("[exportTrainerProfitSharingReport] failed: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * @apiNote get deduct coin club report
	 * @author long
	 * @email longpham@leep.app
	 * @param startTime, endTime, name, page, size
	 * @return ResponseEntity<Object>
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/deduct-coin-club", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public ResponseEntity<Object> getDeductCoinClubReport(
			@RequestParam(value = "startTime", required = true) long startTime,
			@RequestParam(value = "endTime", required = true) long endTime,
			@RequestParam(value = "name", required = false) String name,
			@PageableDefault(page = 0, size = 20) Pageable pageable) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			PagedResponse<DeductCoinClubDTO> deductCoinClub = walletService.getDeductCoinClubReport(startTime, endTime,
					name, pageable);
			apiResponse.setData(deductCoinClub);
		} catch (Exception e) {
			LOGGER.error("[getDeductCoinClubReport] failed: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * @apiNote export deduct coin club report
	 * @author long
	 * @email longpham@leep.app
	 * @param startTime, endTime, name, page, size
	 * @return csv file
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/deduct-coin-club/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public StreamingResponseBody exportDeductCoinClubReport(
			@RequestParam(value = "startTime", required = true) long startTime,
			@RequestParam(value = "endTime", required = true) long endTime,
			@RequestParam(value = "name", required = false) String name, final HttpServletResponse response) {
		try {
			Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
			PagedResponse<DeductCoinClubDTO> deductCoinClub = walletService.getDeductCoinClubReport(startTime, endTime,
					name, pageable);
			List<DeductCoinClubDTO> listDeductCoinClubReport = deductCoinClub.getEntities();
			return FileExportUtils.exportCSV(listDeductCoinClubReport, DEDUCT_COIN_CLUB_REPORT_HEADER,
					DEDUCT_COIN_CLUB_REPORT, response);
		} catch (Exception e) {
			LOGGER.error("[exportDeductCoinClubReport] failed: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * @apiNote get deduct coin trainer report
	 * @author long
	 * @email longpham@leep.app
	 * @param startTime, endTime, name, page, size
	 * @return ResponseEntity<Object>
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/deduct-coin-trainer", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public ResponseEntity<Object> getDeductCoinTrainerReport(
			@RequestParam(value = "startTime", required = true) long startTime,
			@RequestParam(value = "endTime", required = true) long endTime,
			@RequestParam(value = "name", required = false) String name,
			@PageableDefault(page = 0, size = 20) Pageable pageable) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			PagedResponse<DeductCoinTrainerDTO> deductCoinTrainer = walletService.getDeductCoinTrainerReport(startTime,
					endTime, name, pageable);
			apiResponse.setData(deductCoinTrainer);
		} catch (Exception e) {
			LOGGER.error("[getDeductCoinTrainerReport] failed: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * @apiNote export deduct coin trainer report
	 * @author long
	 * @email longpham@leep.app
	 * @param startTime, endTime, name, page, size
	 * @return csv file
	 */
	@GetMapping(value = "/admin/v1/wallets/finance/deduct-coin-trainer/export", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('finance') || hasRole('sales-admin')")
	public StreamingResponseBody exportDeductCoinTrainerReport(
			@RequestParam(value = "startTime", required = true) long startTime,
			@RequestParam(value = "endTime", required = true) long endTime,
			@RequestParam(value = "name", required = false) String name, final HttpServletResponse response) {
		try {
			Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
			PagedResponse<DeductCoinTrainerDTO> deductCoinTrainer = walletService.getDeductCoinTrainerReport(startTime,
					endTime, name, pageable);
			List<DeductCoinTrainerDTO> listDeductCoinTrainerReport = deductCoinTrainer.getEntities();
			return FileExportUtils.exportCSV(listDeductCoinTrainerReport, DEDUCT_COIN_TRAINER_REPORT_HEADER,
					DEDUCT_COIN_TRAINER_REPORT, response);
		} catch (Exception e) {
			LOGGER.error("[exportDeductCoinClubReport] failed: {}", e.getMessage());
			return null;
		}
	}
}
