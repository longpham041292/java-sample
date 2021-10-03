package asia.cmg.f8.commerce.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.dto.CreditCODRequest;
import asia.cmg.f8.commerce.dto.CreditCODResponse;
import asia.cmg.f8.commerce.dto.CreditCouponEmailDTO;
import asia.cmg.f8.commerce.dto.CreditCouponsDTO;
import asia.cmg.f8.commerce.dto.UpdateCODRequest;
import asia.cmg.f8.commerce.entity.credit.CreditCouponEntity;
import asia.cmg.f8.commerce.entity.credit.CreditCouponStatus;
import asia.cmg.f8.commerce.repository.CreditCouponsRepository;
import asia.cmg.f8.common.email.EmailSender;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.AESEncryptUtils;
import asia.cmg.f8.common.util.PagedResponse;

/**
 * 
 * @author Long Pham
 *
 */
@Service
public class CreditCouponService {

	private static final Logger LOG = LoggerFactory.getLogger(CreditCouponService.class);
	private static final String COD_NOTIFICATION = "cod-notification";
	private static final String DTOS = "dtos";
	private static final String LOGO = "logo";
	private static final String PACKAGE_NAME = "packageName";

	@Autowired
	private CommerceProperties commerceProps;

	@Autowired
	private CreditCouponsRepository creditCouponsRepository;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Value("${email.recipient}")
	private String recipient;

	@Value("${email.sender}")
	private String sender;

	@Value("${email.subject}")
	private String subject;

	/**
	 * Create list credit coupons
	 * 
	 * @param codRequest
	 * @param acount
	 * @return list coupon's serial number and code
	 * @throws Exception
	 */
	@Transactional
	public List<CreditCODResponse> createListCreditCoupon(CreditCODRequest codRequest, Account acount)
			throws Exception {
		List<CreditCODResponse> response = new ArrayList<CreditCODResponse>();
		List<CreditCouponEntity> result = new ArrayList<CreditCouponEntity>();
		int seriesNumberLengh = 11;
		int latestSeriesNumber = getLatestSeriesNumber();
		for (int i = 0; i < codRequest.getQuantity(); i++) {
			CreditCouponEntity creditCouponsEntity = new CreditCouponEntity();
			creditCouponsEntity.setName(codRequest.getCodeName());
			creditCouponsEntity.setCredit(codRequest.getCredit());
			creditCouponsEntity.setBonusCredit(codRequest.getBonusCredit());
			creditCouponsEntity.setAmount(codRequest.getAmount());
			creditCouponsEntity.setCouponExpiredDay(codRequest.getDuration());
			creditCouponsEntity.setCouponExpiredDate(codRequest.getDuration() == 0 ? null
					: LocalDateTime.now().plusDays(codRequest.getDuration() + 1).withHour(0).withMinute(0).withSecond(0)
							.withNano(0));
			creditCouponsEntity.setStatus(CreditCouponStatus.ACTIVED);
			creditCouponsEntity.setCode(generateCodCode());
			creditCouponsEntity.setEncryptedCode(
					AESEncryptUtils.encrypt(creditCouponsEntity.getCode().toString(), commerceProps.getSecretKey()));
			latestSeriesNumber += 1;
			creditCouponsEntity.setSerial(String.format("%0" + seriesNumberLengh + "d", latestSeriesNumber));
			result.add(creditCouponsEntity);
		}
		creditCouponsRepository.save(result);
		for (CreditCouponEntity entity : result) {
			response.add(new CreditCODResponse(entity.getId(), entity.getSerial(), entity.getEncryptedCode()));
		}
		this.sendEmailToFinance(result, codRequest.getQuantity());
		return response;
	}

	/**
	 * get list Credit Coupons
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public PagedResponse<CreditCouponsDTO> getListCreditsCoupons(Long startTime, Long endTime, String name,
			Integer status, Pageable pageable) {
		final LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
				TimeZone.getDefault().toZoneId());
		final LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
				TimeZone.getDefault().toZoneId());
		List<CreditCouponStatus> listStatus = new ArrayList<CreditCouponStatus>();
		if (status == null) {
			listStatus.add(CreditCouponStatus.ACTIVED);
			listStatus.add(CreditCouponStatus.USED);
			listStatus.add(CreditCouponStatus.EXPIRED);
		} else {
			listStatus.add(CreditCouponStatus.values()[status]);
		}
		Page<CreditCouponsDTO> entities = null;
		if (StringUtils.isEmpty(name)) {
			entities = creditCouponsRepository.getCreditCoupons(from, to, listStatus, pageable);
		} else {
			entities = creditCouponsRepository.getCreditCouponsByName(from, to, name, listStatus, pageable);
		}
		PagedResponse<CreditCouponsDTO> result = new PagedResponse<CreditCouponsDTO>();
		result.setEntities(entities.getContent());
		result.setCount((int) entities.getTotalElements());
		return result;
	}

	private int getLatestSeriesNumber() {
		CreditCouponEntity latestRecord = creditCouponsRepository.findFirstByOrderByIdDesc();
		return latestRecord == null ? 0 : Integer.valueOf(latestRecord.getSerial());
	}

	private String generateCodCode() {
		return UUID.randomUUID().toString();
	}

	private void sendEmailToFinance(List<CreditCouponEntity> result, int quantity) {
		List<CreditCouponEmailDTO> listDto = new ArrayList<>();
		result.forEach(c -> {
			CreditCouponEmailDTO dto = new CreditCouponEmailDTO(c);
			listDto.add(dto);
		});
		Locale locale = Locale.ENGLISH;
		Context context = new Context(locale);
		context.setVariable(DTOS, listDto);
		context.setVariable(LOGO, "https://leep.imgix.net/2020/10/leep-logo.png?fm=png&ixlib=php-1.2.1");
		context.setVariable(PACKAGE_NAME, result.get(0).getName());
		String emailTempalte = templateEngine.process(COD_NOTIFICATION, context);

		EmailSender email = new EmailSender();
		Boolean sendMail = email.sendEmail(sender, recipient, subject, emailTempalte);
		if (!sendMail) {
			LOG.error("Fail to send email");
		}
	}

	public List<CreditCouponsDTO> updateListCreditCoupon(List<UpdateCODRequest> codRequest, Account acount) {
		List<CreditCouponsDTO> response = new ArrayList<CreditCouponsDTO>();
		List<CreditCouponEntity> result = new ArrayList<CreditCouponEntity>();
		for (UpdateCODRequest request : codRequest) {
			if (request.getId() == null) {
				LOG.error("Missing credit coupon id");
				continue;
			}
			Optional<CreditCouponEntity> entity = creditCouponsRepository.findById(request.getId());
			if (!entity.isPresent()) {
				LOG.error("Credit coupon with id: {} not found", request.getId());
				continue;
			}
			CreditCouponEntity creditCoupon = entity.get();
			if (creditCoupon.getStatus() == CreditCouponStatus.USED) {
				LOG.error("Credit coupon with id: {} has been used", creditCoupon.getId());
				continue;
			} else if (creditCoupon.getStatus() == CreditCouponStatus.EXPIRED) {
				LOG.error("Credit coupon with id: {} has been expired", creditCoupon.getId());
				continue;
			}
			if (!StringUtils.isEmpty(request.getQrCodeUrl())) {
				creditCoupon.setQrCodeUrl(request.getQrCodeUrl());
			}
			result.add(creditCoupon);
			response.add(new CreditCouponsDTO(creditCoupon));
		}
		creditCouponsRepository.save(result);
		return response;
	}
}
