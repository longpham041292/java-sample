package asia.cmg.f8.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.format.MoneyFormatterBuilder;

public final class CurrencyUtils {

    private CurrencyUtils() {
    }

    private static final String SPACE = " ";

    public static String formatCurrency(final String currency, final String language, final double amount) {
        final CurrencyUnit currencyUnit = CurrencyUnit.of(currency.toUpperCase(Locale.ENGLISH));
        final MoneyFormatterBuilder mfb = new MoneyFormatterBuilder().appendCurrencyCode()
                .appendLiteral(SPACE).appendAmountLocalized();
        return mfb.toFormatter(new Locale(language)).print(
                BigMoney.ofScale(currencyUnit, new BigDecimal(String.valueOf(amount)),
                        currencyUnit.getDecimalPlaces(), RoundingMode.DOWN));
    }
    
    public static String formatCurrency(final String currency, final String language, final BigDecimal amount) {
        final CurrencyUnit currencyUnit = CurrencyUnit.of(currency.toUpperCase(Locale.ENGLISH));
        final MoneyFormatterBuilder mfb = new MoneyFormatterBuilder().appendCurrencyCode()
                .appendLiteral(SPACE).appendAmountLocalized();
        return mfb.toFormatter(new Locale(language)).print(
                BigMoney.ofScale(currencyUnit, amount,
                        currencyUnit.getDecimalPlaces(), RoundingMode.DOWN));
    }
}
