package asia.cmg.f8.report.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.report.entity.database.BasicUserEntity;
import asia.cmg.f8.report.entity.database.CreditCouponEntity;
import asia.cmg.f8.report.entity.database.CreditPackageEntity;
import asia.cmg.f8.report.entity.database.OrderCouponEntryEntity;
import asia.cmg.f8.report.entity.database.OrderCreditEntryEntity;
import asia.cmg.f8.report.entity.database.OrderEntity;

/**
 * DTO mapping CreditWalletTransactionEntity and export file
 * @author phong
 *
 */
public class TopUpReportDto {
	
	@JsonProperty("order_code")
	private String orderCode;
	
	@JsonProperty("transaction_date")
	private String transactionDate;

	@JsonProperty("client_name")
	private String clientName;
	
	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("paymentMethod")
	private String paymentMethod;
	
	@JsonProperty("credit_package_name")
	private String creditPackageName;
	
	@JsonProperty("credit_amount")
	private Integer creditAmount;
	
	@JsonProperty("exchange_money")
	private Double exchangeMoney;
	
	@JsonProperty("original_price")
	private Double originalPrice;

	@JsonProperty("package_duration")
	private Integer packageDuration;
	
	@JsonProperty("expired_date")
	private String expiredDate;
	
	@JsonProperty("promotion_code")
	private String promotionCode;
	
	@JsonProperty("coupon_serial")
	private String couponSerial;
	
	
	public TopUpReportDto(OrderEntity order ,  BasicUserEntity user, CreditPackageEntity unitCreditPackage) {
		this.orderCode = order.getCode();
		
		final Double MONEY_PER_COIN = unitCreditPackage.getPrice(); 
		final String DATE_PATTERN = "dd/MM/yyyy hh:mm:ss a";
		final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
		this.transactionDate = order.getCreatedDate() == null ? ""
				: DATE_FORMATTER.format(order.getCreatedDate());
		this.clientName = user.getFullName();
		this.userName = user.getUserName();
		List<OrderCreditEntryEntity> creditEntry = order.getOrderCreditPackageEntries();
		if (order.getType().equalsIgnoreCase("CREDIT") && !creditEntry.isEmpty()) {
			OrderCreditEntryEntity creditInstance = creditEntry.get(0);
			this.paymentMethod = order.getPaymentTransactions().get(0).getPaymentProvider();
			this.creditPackageName = order.getOrderCreditPackageEntries().get(0).getCreditPackage().getTitleEN();
			this.creditAmount =  creditInstance.getQuantity() * order.getOrderCreditPackageEntries().get(0).getCreditPackage().getCredit();
			this.packageDuration = order.getOrderCreditPackageEntries().get(0).getCreditPackage().getNumberOfExpiredDay();
		}
		List<OrderCouponEntryEntity> couponEntry = order.getOrderCouponEntryEntity();
		if (order.getType().equalsIgnoreCase("COUPON") && !couponEntry.isEmpty() ) {
			this.paymentMethod = "CASH";
			CreditCouponEntity coupon = couponEntry.get(0).getCreditCoupon(); 
			this.creditPackageName = coupon.getName();
			this.creditAmount = coupon.getCredit() + coupon.getBonusCredit();
			this.packageDuration = coupon.getCreditExpiredDay();
			OrderCouponEntryEntity couponEntity =  couponEntry.get(0);
			if(couponEntity != null) {
				this.couponSerial =  couponEntity.getCreditCoupon().getSerial();
			}
		}
		this.exchangeMoney = this.creditAmount * MONEY_PER_COIN;
		this.originalPrice = order.getTotalPrice();
		
		LocalDateTime create = order.getCreatedDate();
		if(this.packageDuration != null) {
			create = create.plusDays(this.packageDuration);
		}
		this.expiredDate = create == null ? "" : DATE_FORMATTER.format(create);
		this.promotionCode = order.getCouponCode();
	}


	public String getTransactionDate() {
		return transactionDate;
	}


	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}


	public String getClientName() {
		return clientName;
	}


	public void setClientName(String clientName) {
		this.clientName = clientName;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPaymentMethod() {
		return paymentMethod;
	}


	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}


	public String getCreditPackageName() {
		return creditPackageName;
	}


	public void setCreditPackageName(String creditPackageName) {
		this.creditPackageName = creditPackageName;
	}


	public Integer getCreditAmount() {
		return creditAmount;
	}


	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}


	public Double getExchangeMoney() {
		return exchangeMoney;
	}


	public void setExchangeMoney(Double exchangeMoney) {
		this.exchangeMoney = exchangeMoney;
	}


	public Integer getPackageDuration() {
		return packageDuration;
	}


	public void setPackageDuration(Integer packageDuration) {
		this.packageDuration = packageDuration;
	}


	public String getExpiredDate() {
		return expiredDate;
	}


	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}


	public String getPromotionCode() {
		return promotionCode;
	}


	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}


	public String getOrderCode() {
		return orderCode;
	}


	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}


	public String getCouponSerial() {
		return couponSerial;
	}


	public void setCouponSerial(String couponSerial) {
		this.couponSerial = couponSerial;
	}


	public Double getOriginalPrice() {
		return originalPrice;
	}


	public void setOriginalPrice(Double originalPrice) {
		this.originalPrice = originalPrice;
	}
	
	
}
