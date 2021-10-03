package asia.cmg.f8.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public final class NumberUtils {
	
	private NumberUtils(){
	}
	
	public static String getFormatNumber(final Double number, final String language){
		final NumberFormat formatter = NumberFormat.getInstance(new Locale(language));
		return formatter.format(number);
	}
	
	public static double roundUpDouble(final double value) {
        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
