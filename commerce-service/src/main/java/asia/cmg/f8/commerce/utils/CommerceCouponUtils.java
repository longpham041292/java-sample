package asia.cmg.f8.commerce.utils;

import java.math.BigDecimal;

import asia.cmg.f8.commerce.dto.Product;
import asia.cmg.f8.commerce.entity.ProductEntity;
import asia.cmg.f8.commerce.entity.PromotionEntity;
import asia.cmg.f8.common.util.CurrencyUtils;

public final class CommerceCouponUtils {

	private CommerceCouponUtils() {
    }
	
	public static Double calculateFinalUnitPrice(final ProductEntity entity, final Double unitPrice,
			final Double couponDiscount, final Integer freeSession) {
		
		final Double finalTotalPrice = calculateFinalTotalPrice(entity, unitPrice, couponDiscount);
		final Double ret = finalTotalPrice/(entity.getNumOfSessions() + freeSession);

		if (ret <= 0) {
			return 0D;
		}
		return ret;
	}
	
	public static Double calculatePromotionPrice(final Double originalPrice, final Double discountPercent) {
		Double calculationPercent = 100 - discountPercent;
		Double result = 0d;
		
		return result = (originalPrice * calculationPercent) / 100;
	}

	public static Double calculatePromotionPrice(final ProductEntity entity, final Double unitPrice,
			final Double couponDiscount) {
		final Double bdUnitPrice = new Double(unitPrice.toString());
		final Double subTotal = bdUnitPrice *(new Double(entity.getNumOfSessions()));
		final Double promotionPrice = new Double(entity.getPromotionPrice());

		final Double totalDiscount = subTotal - promotionPrice;
		final Double calculateDiscount = (totalDiscount * 100)/subTotal;
		final Double discountValChose = (couponDiscount >= calculateDiscount) ? couponDiscount : calculateDiscount;
		final Double ret = (subTotal* discountValChose)/100;
		if (ret <= 0) {
			return 0D;
		}
		return ret;
	}

	public static Double calculateFinalTotalPrice(final ProductEntity entity, final Double unitPrice,
			final Double couponDiscount) {

		final Double subTotal = new Double(unitPrice.toString()) * (new Double(entity.getNumOfSessions()));
		final Double displayPromotionPrice = calculatePromotionPrice(entity, unitPrice, couponDiscount);
		final double ret = subTotal - displayPromotionPrice;

		if (ret <= 0) {
			return 0D;
		}
		return ret;
	}

	public static Double calculateDiscount(final ProductEntity entity, final Double unitPrice,
			final Double couponDiscount, final Integer freeSession) {
		final Double bdUnitPrice = new Double(unitPrice.toString());
		final Double finalUnitPrice = calculateFinalUnitPrice(entity, unitPrice, couponDiscount, freeSession);

		final Double totalDiscount = bdUnitPrice - finalUnitPrice;
		if (totalDiscount.doubleValue() < 0.0) {
			return 0.0;
		}
		final Double ret = (totalDiscount * 100)/bdUnitPrice;
		if (ret <= 0) {
			return 0D;
		}
		return ret;
	}

	public static Product buildForUser(final ProductEntity entity, final Double unitPrice, final String language,
			final PromotionEntity promotion) {
		final Integer freeSession = (null != promotion) ? promotion.getFreeSession() : 0;
		Double discount = (null != promotion) ? promotion.getDiscount() : 0;
		
		//TODO: remove after campaign August 2019
		if (promotion != null) {
			discount = calculatorSpecialDiscount(discount, promotion.getCouponCode(), entity.getNumOfSessions());
		}
		//end

		final String displayUnitPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language, unitPrice);

		final Double promotionPrice = CommerceUtils.roundCurrency(calculatePromotionPrice(entity, unitPrice, discount), entity.getCurrency());

		final String finalDisplayPromotionPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				promotionPrice);

		final String displayFinalUnitPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				Math.floor(calculateFinalUnitPrice(entity, unitPrice, discount, freeSession)));

		final String displayFinalTotalPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				CommerceUtils.roundCurrency(calculateFinalTotalPrice(entity, unitPrice, discount), entity.getCurrency()));

		final BigDecimal subTotal = new BigDecimal(unitPrice.toString())
				.multiply(new BigDecimal(entity.getNumOfSessions()));

		final String displaySubTotal = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				Double.valueOf(subTotal.toString()));
		
		final Double finalDiscount = Double.valueOf(Math.round(calculateDiscount(entity, unitPrice, discount, freeSession) * 100)/100);

		return Product.builder().id(entity.getUuid()).finalTotalNumOfSession(entity.getNumOfSessions() + freeSession)
				.numOfSessions(entity.getNumOfSessions()).expireLimit(entity.getExpireLimit())
				.promotionPrice(entity.getPromotionPrice()).finalDisplayPromotionPrice(finalDisplayPromotionPrice).unitPrice(unitPrice)
				.displayUnitPrice(displayUnitPrice).displaySubtotal(displaySubTotal)
				.discount(finalDiscount)
				.finalTotalPrice(displayFinalTotalPrice).finalUnitPrice(displayFinalUnitPrice)
				.trainingStyle(entity.getTrainingStyle().name())
				.build();
	}

	public static Product buildForAdmin(final ProductEntity entity, final Double unitPrice, final String language,
			final PromotionEntity promotion) {
		final Integer freeSession = (null != promotion) ? promotion.getFreeSession() : 0;
		final Double discount = (null != promotion) ? promotion.getDiscount() : 0;
		final String finalDisplayPromotionPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				calculatePromotionPrice(entity, unitPrice, discount));
		final String displayFinalUnitPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				calculateFinalUnitPrice(entity, unitPrice, discount, freeSession));
		final String displayPromotionPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				entity.getPromotionPrice());
		final String displayFinalTotalPrice = CurrencyUtils.formatCurrency(entity.getCurrency(), language,
				calculateFinalTotalPrice(entity, unitPrice, discount));
		return Product.builder().id(entity.getUuid()).numOfSessions(entity.getNumOfSessions())
				.finalTotalNumOfSession(entity.getNumOfSessions() + freeSession)
				.finalDisplayPromotionPrice(finalDisplayPromotionPrice)
				.finalTotalPrice(displayFinalTotalPrice).finalUnitPrice(displayFinalUnitPrice)
				.expireLimit(entity.getExpireLimit()).promotionPrice(entity.getPromotionPrice())
				.displayPromotionPrice(displayPromotionPrice).levelCode(entity.getLevel().getCode())
				.active(entity.getActive()).discount(calculateDiscount(entity, unitPrice, discount, freeSession))
				.trainingStyle(entity.getTrainingStyle().name())
				.build();
	}
	
	//TODO this is hard code for August 2019 campaign, should be removed later
	public static Double calculatorSpecialDiscount(final Double discount, final String couponCode, final Integer numberOfSession) {
		if ("pkga".equalsIgnoreCase(couponCode)) {
			if (numberOfSession == 1) {
				return 54.0;
			}
			if (numberOfSession == 2) {
				return 65.0;
			}
			if (numberOfSession == 3) {
				return 69.0;
			}
		}
		if ("pkgb".equalsIgnoreCase(couponCode)) {
			if (numberOfSession == 1) {
				return 44.0;
			}
			if (numberOfSession == 2) {
				return 61.0;
			}
			if (numberOfSession == 3) {
				return 66.0;
			}
		}
		if ("pkgc".equalsIgnoreCase(couponCode)) {
			if (numberOfSession == 1) {
				return 0.0;
			}
			if (numberOfSession == 2) {
				return 38.0;
			}
			if (numberOfSession == 3) {
				return 51.0;
			}
		}
		return discount;
	}
}
