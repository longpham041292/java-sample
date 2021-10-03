package asia.cmg.f8.commerce.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import asia.cmg.f8.commerce.dto.Product;
import asia.cmg.f8.commerce.dto.ProductType;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.OrderEntryEntity;
import asia.cmg.f8.commerce.entity.OrderSubscriptionEntryEntity;
import asia.cmg.f8.commerce.entity.OrderType;
import asia.cmg.f8.commerce.entity.ProductEntity;
import asia.cmg.f8.commerce.entity.ProductTypeEntity;
import asia.cmg.f8.common.util.CurrencyUtils;
import asia.cmg.f8.common.util.NumberUtils;

public final class CommerceUtils {

	public static final String CONTRACT = "contracting";
	public static final String RESULT = "result";
	public static final String PAYMENT_STATUS_HOLDER = "#STATUS#";
	public static final String PAYMENT_RESPONSE_CODE_HOLDER = "#RESPONSE_CODE#";
	public static final String PAYMENT_RESPONSE_INTERNATIONAL_CODE_MESSAGE_PREFIX = "onepay.response.international.code_";
	public static final String PAYMENT_RESPONSE_DOMESTIC_CODE_MESSAGE_PREFIX = "onepay.response.domestic.code_";
	public static final String DATE_PATTERN = "MM/dd/yyyy";
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

	private CommerceUtils() {
	}

	public static ProductType build(final ProductTypeEntity entity, final String language) {
		final String displayUnitPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				entity.getUnitPrice());
		return ProductType.builder().id(entity.getUuid()).commision(entity.getCommision())
				.displayCommission(NumberUtils.getFormatNumber(entity.getCommision(), language))
				.unitPrice(entity.getUnitPrice()).displayUnitPrice(displayUnitPrice)
				.description(entity.getDescription()).levelCode(entity.getLevel().getCode()).build();
	}

	public static Product buildForAdmin(final ProductEntity entity, final Double unitPrice, final String language) {
		final String displayPromotionPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				entity.getPromotionPrice());
		return Product.builder().id(entity.getUuid()).numOfSessions(entity.getNumOfSessions())
				.expireLimit(entity.getExpireLimit()).promotionPrice(entity.getPromotionPrice())
				.displayPromotionPrice(displayPromotionPrice).levelCode(entity.getLevel().getCode())
				.active(entity.getActive()).discount(calculateDiscount(entity, unitPrice))
				.finalUnitPrice(CurrencyUtils.formatCurrency(entity.getCurrency(), language,
						CommerceCouponUtils.calculateFinalUnitPrice(entity, unitPrice, 0D, 0)))
				.finalTotalNumOfSession(entity.getNumOfSessions())
				.finalTotalPrice(CurrencyUtils.formatCurrency(entity.getCurrency(), language,
						CommerceCouponUtils.calculateFinalTotalPrice(entity, unitPrice, 0D)))
				.finalDisplayPromotionPrice(CurrencyUtils.formatCurrency(entity.getCurrency(), language,
						CommerceCouponUtils.calculatePromotionPrice(entity, unitPrice, 0D)))
				.trainingStyle(entity.getTrainingStyle().name())
				.build();
	}

	public static Double calculateDiscount(final ProductEntity entity, final Double unitPrice) {
		final BigDecimal bdUnitPrice = new BigDecimal(unitPrice.toString());
		final BigDecimal subTotal = bdUnitPrice.multiply(new BigDecimal(entity.getNumOfSessions()));
		final BigDecimal promotionPrice = new BigDecimal(entity.getPromotionPrice()).setScale(2, RoundingMode.HALF_UP);

		final BigDecimal totalDiscount = subTotal.subtract(promotionPrice);
		if (totalDiscount.doubleValue() < 0.0) {
			return 0.0;
		}
		return totalDiscount.multiply(new BigDecimal(100)).divide(subTotal, RoundingMode.FLOOR)
				.setScale(0, RoundingMode.FLOOR).doubleValue();
	}

	public static Product buildForUser(final ProductEntity entity, final Double unitPrice, final String language) {
		final String displayUnitPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language, unitPrice);
		final String displayPromotionPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				entity.getPromotionPrice());
		final BigDecimal subTotal = new BigDecimal(unitPrice.toString())
				.multiply(new BigDecimal(entity.getNumOfSessions()));

		final String displaySubTotal = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				Double.valueOf(subTotal.toString()));

		return Product.builder().id(entity.getUuid()).numOfSessions(entity.getNumOfSessions())
				.expireLimit(entity.getExpireLimit()).promotionPrice(entity.getPromotionPrice())
				.displayPromotionPrice(displayPromotionPrice).unitPrice(unitPrice).displayUnitPrice(displayUnitPrice)
				.displaySubtotal(displaySubTotal).discount(calculateDiscount(entity, unitPrice))
				.finalTotalNumOfSession(entity.getNumOfSessions())
				.finalUnitPrice(CurrencyUtils.formatCurrency(entity.getCurrency(), language,
						CommerceCouponUtils.calculateFinalUnitPrice(entity, unitPrice, 0D, 0)))
				.finalTotalPrice(CurrencyUtils.formatCurrency(entity.getCurrency(), language,
						CommerceCouponUtils.calculateFinalTotalPrice(entity, unitPrice, 0D)))
				.finalDisplayPromotionPrice(CurrencyUtils.formatCurrency(entity.getCurrency(), language,
						CommerceCouponUtils.calculatePromotionPrice(entity, unitPrice, 0D)))
				.trainingStyle(entity.getTrainingStyle().name())
				.build();
	}

	public static Double roundCurrency(final Double value) {
		final BigDecimal bdUnitPrice = new BigDecimal(value.toString());
		return Double.valueOf(bdUnitPrice.setScale(2, RoundingMode.HALF_UP).toString());
	}
	
	public static Double roundCurrency(final Double value, final String currency) {
		final BigDecimal bdValue = new BigDecimal(value.toString());
		int newScale = 2;
		if("vnd".equals(currency.toLowerCase())){
			newScale = 0;
		}
		return Double.valueOf(bdValue.setScale(newScale, RoundingMode.HALF_EVEN).toString());
	}

	public static Double round(final Double value, final RoundingMode roundMode) {
		final BigDecimal bdValue = new BigDecimal(value.toString());
		return Double.valueOf(bdValue.setScale(2, roundMode).toString());
	}
	
	public static Double calculateDiscountPrice(final Double price, final Double discountPecent, final String currency) {
		BigDecimal discountPrice = BigDecimal.ZERO;
		BigDecimal bdPrice = new BigDecimal(price);
		BigDecimal bdDiscountPercent = new BigDecimal(discountPecent);
		
		discountPrice = bdPrice.multiply(bdDiscountPercent).divide(new BigDecimal(100));
		
		return CommerceUtils.roundCurrency(discountPrice.doubleValue(), currency);
	}

	public static void calculateOrder(final OrderEntity order) {
		BigDecimal subTotal = BigDecimal.ZERO;
		BigDecimal totalPrice = BigDecimal.ZERO;

		if (order.getType() == null || order.getType().equals(OrderType.PRODUCT.toString())) {
			for (final OrderEntryEntity entry : order.getOrderProductEntries()) {
				subTotal = subTotal.add(
						new BigDecimal(entry.getUnitPrice().toString()).multiply(new BigDecimal(entry.getQuantity())));
				totalPrice = totalPrice.add(new BigDecimal(entry.getTotalPrice()));
			}
		}else 
		{
			for (final OrderSubscriptionEntryEntity entry : order.getOrderSubscriptionEntries()) {
				subTotal = subTotal.add(
						new BigDecimal(entry.getUnitPrice().toString()).multiply(new BigDecimal(entry.getQuantity())));
				totalPrice = totalPrice.add(new BigDecimal(entry.getPrice()));
			}
		}

		order.setSubTotal(Double.valueOf(subTotal.toString()));
		order.setTotalPrice(CommerceUtils.roundCurrency(Double.valueOf(totalPrice.toString()), order.getCurrency()));
		order.setDiscount(CommerceUtils.roundCurrency(subTotal.subtract(totalPrice).doubleValue(), order.getCurrency()));
	}

	public static Double calculateSubTotal(final Double unitPrice, final Integer numberOfSession) {
		final BigDecimal bdUnitPrice = new BigDecimal(unitPrice.toString()).setScale(2, RoundingMode.HALF_UP);
		final BigDecimal subTotal = bdUnitPrice.multiply(new BigDecimal(numberOfSession));
		return Double.valueOf(subTotal.toString());
	}

	public static String getUniqueCode() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static LocalDate parseDate(final String date) {
		return LocalDate.parse(date, DATE_FORMATTER);
	}

	public static String setdateFormat(final Long date) {
		if (date == null) {
			return "";
		}
//    	 final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//	     final Date dateTime = new Date(date + (1000 * 60 * 60 * 18));
//       return formatter.format(dateTime);    	
		final Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(date);
		final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		formatter.setCalendar(calendar);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));

		return formatter.format(calendar.getTime());
		
    }

	/**
	 *
	 * @param unitPrice
	 * @param quantity
	 * @param price
	 * @return Discount
	 */
	public static Double calculateDiscount(Double unitPrice, int quantity, Double price){
    	return (1 - (price/(unitPrice * quantity))) * 100;
	}
	
	public static String convertListToStringWithSemicolon(List<String> params) {
		String result = "";
		if(params!= null && !params.isEmpty()) {
			result = String.join(";", params);
		}
		return result;
	}
}
