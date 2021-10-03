package asia.cmg.f8.session.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.common.spec.misc.Country;
import asia.cmg.f8.common.spec.report.TimeRange;
import asia.cmg.f8.common.util.CurrencyUtils;
import asia.cmg.f8.common.util.NumberUtils;
import asia.cmg.f8.session.api.SortFieldMapping;
import asia.cmg.f8.session.dto.ClientSessionInfo;
import asia.cmg.f8.session.dto.PTSessionInfo;
import asia.cmg.f8.session.entity.SessionPackageStatus;

@SuppressWarnings("PMD.TooManyMethods")
public final class ReportUtils {

	private static final String DATE_PATTERN_DD_MM_YYYY = "dd/MM/yyyy";
	public static final String JOIN_DATE = "join_date";
	public static final String EXPIRED_DATE = "expired_date";
	public static final String SESSION_BURNED = "session_burned";
	public static final String SO_NUM_OF_BURNED = "so.num_of_burned";
	public static final String SO_EXPIRED_DATE = "so.expired_date";
	public static final String EMAIL = "email";
	public static final String PRICE_FORMAT = "#,###,###,##0";

	public static final Logger LOG = LoggerFactory.getLogger(ReportUtils.class);

	private ReportUtils() {
		//
	}

	public static final String FREE_SESSION = "FREE_SESSION";
	public static final String ORDER = "ORDER";
	public static final String TRANSFERRED = SessionPackageStatus.TRANSFERRED.name();
	public static final String NAME = "name";
	public static final String FULL_NAME = "full_name";
	public static final String CITY = "city";
	public static final String COUNTRY = "country";
	public static final String ACTIVATED = "activated";
	public static final String PACKAGE_ACTIVE_STATUS = "Active";
	public static final String PACKAGE_INVALID_STATUS = "InValid";
	public static final String PACKAGE_INACTIVE_STATUS = "InActive";
	public static final String PACKAGE_TRANSFERRED_STATUS = "Transferred";
	public static final String PACKAGE_UNKNOWN_STATUS = "Unknown";
	public static final String NOT_START = "Not started";
	public static final String DATE_PATTERN = "MM/dd/yyyy";
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	public static final DateTimeFormatter DATE_DDMMYYYY_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN_DD_MM_YYYY);
	private static final DateTimeFormatter DATE_MONTH_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");
	private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

	private static final FastDateFormat DATETIME_FORMATTER = FastDateFormat.getInstance("dd/MM/yyyy HH:MM");

    private static final FastDateFormat TIMESTAMP_DATE_FORMATTER = FastDateFormat.getInstance("dd/MM/yyyy");
    private static final FastDateFormat TIME_FORMATTER = FastDateFormat.getInstance("HH:MM");

	public static String resolveSortValue(final String sortField) {
		switch (sortField) {
		case "name":
			return FULL_NAME;
		case CITY:
			return CITY;
		case COUNTRY:
			return COUNTRY;
		case ACTIVATED:
			return ACTIVATED;
		case JOIN_DATE:
			return JOIN_DATE;
		case SESSION_BURNED:
			return SO_NUM_OF_BURNED;
		case EXPIRED_DATE:
			return SO_EXPIRED_DATE;
		case EMAIL:
			return EMAIL;
		default:
			return FULL_NAME;
		}

	}

	public static String calculateStatusSessionPackage(final SessionPackageStatus currentStatus, final Long expiredDate,
			final int sessionBurned, final int numberOfSession) {
		
		long currentMillis = LocalDate.now().atStartOfDay().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();

		switch (currentStatus) {
		case VALID:
			return Objects.isNull(expiredDate)
					|| (sessionBurned < numberOfSession && expiredDate * 1000 >= currentMillis)
							? ReportUtils.PACKAGE_ACTIVE_STATUS : ReportUtils.PACKAGE_INVALID_STATUS;
		case TRANSFERRED:
			return Objects.isNull(expiredDate)
					|| (sessionBurned < numberOfSession && expiredDate * 1000 >= currentMillis)
							? ReportUtils.PACKAGE_TRANSFERRED_STATUS : ReportUtils.PACKAGE_INVALID_STATUS;
		case INVALID:
			return ReportUtils.PACKAGE_INVALID_STATUS;
		case INACTIVE:
			return ReportUtils.PACKAGE_INACTIVE_STATUS;
		default:
			return ReportUtils.getClientSessionPackageStatus(currentStatus);
		}
	}

	public static ClientSessionInfo parseClientSessionInfo(final Object[] content, final String language,
			final String currency) {
		final ClientSessionInfo user = new ClientSessionInfo();
		user.setUuid((String) content[0]);
		user.setAvatar((String) content[1]);
		user.setName((String) content[2]);
		user.setSessionBurned((Integer) content[3]);
		user.setSessionNumber((Integer) content[4]);
		user.setPrice(formatCurrency((double) content[5]));
		user.setCommission((double) content[6]);

		final Long timeStamp = content[7] == null ? null : ((Timestamp) content[7]).getTime() / 1000;
		user.setExpireDate(timeStamp);

		final SessionPackageStatus sessionPackageStatus = SessionPackageStatus.valueOf((String) content[8]);
		final String status = ReportUtils.calculateStatusSessionPackage(sessionPackageStatus, user.getExpireDate(),
				user.getSessionBurned(), user.getSessionNumber());
		user.setStatus(status);

		user.setPackageSessionUuid((String) content[9]);
		user.setUserName((String) content[10]);
		user.setOrderCode((String) content[11]);
		user.setSessionConfirmed(((BigInteger) content[12]).intValue());
		user.setOrderUuid((String) content[13]);
		user.setContractNumber((String) content[14]);
		return user;
	}

	public static PTSessionInfo parsePTSessionInfo(final Object[] content, final String language,
			final String currency) {
		final PTSessionInfo userPt = new PTSessionInfo();
		userPt.setUuid((String) content[0]);
		userPt.setAvatar((String) content[1]);
		userPt.setName((String) content[2]);
		userPt.setSessionBurned((Integer) content[3]);
		userPt.setSessionNumber((Integer) content[4]);
		userPt.setPrice(CurrencyUtils.formatCurrency(currency, language, (double) content[5]));

		userPt.setCommission((double) content[6]);

		final Long timeStamp = content[7] == null ? null : ((Timestamp) content[7]).getTime() / 1000;
		userPt.setExpireDate(timeStamp);

		final SessionPackageStatus sessionPackageStatus = SessionPackageStatus.valueOf((String) content[8]);
		final String status = ReportUtils.calculateStatusSessionPackage(sessionPackageStatus, userPt.getExpireDate(),
				userPt.getSessionBurned(), userPt.getSessionNumber());
		userPt.setStatus(status);

		userPt.setPackageSessionUuid((String) content[9]);
		userPt.setOrderCode((String) content[10]);
		userPt.setOrderUuid((String) content[12]);
		userPt.setContractNumber((String) content[13]);
		userPt.setTotalPrice((double) content[14]);
		return userPt;
	}

	public static String getClientSessionPackageStatus(final SessionPackageStatus sessionPackageStatus) {
		switch (sessionPackageStatus) {
		case INVALID:
			return ReportUtils.PACKAGE_INVALID_STATUS;
		case VALID:
			return ReportUtils.PACKAGE_ACTIVE_STATUS;
		case TRANSFERRED:
			return ReportUtils.PACKAGE_TRANSFERRED_STATUS;
		default:
			return ReportUtils.PACKAGE_UNKNOWN_STATUS;
		}
	}

	public static long parseDateToSecond(final String date) {
		return LocalDateTime.of(LocalDate.parse(date, DATE_FORMATTER), LocalTime.MIDNIGHT)
				.atZone(ZoneId.systemDefault()).toEpochSecond();
	}

	public static long parseDateToSecondEndDay(final String date) {
		return LocalDateTime.of(LocalDate.parse(date, DATE_FORMATTER), LocalTime.MAX).atZone(ZoneId.systemDefault())
				.toEpochSecond();
	}

	public static LocalDate parseToLocalDate(final String date) {
		return LocalDate.parse(date, DATE_FORMATTER);
	}

	public static String formatDateFromMilis(final Timestamp milis) {
		final FastDateFormat format = FastDateFormat.getInstance(DATE_PATTERN_DD_MM_YYYY);
		return milis != null ? format.format(milis) : "";
	}

	public static String formatDateTime(final Timestamp timestamp) {
		if (timestamp == null) {
			return "";
		}
		return DATETIME_FORMATTER.format(timestamp);
	}

	public static String formatDate(final Date date) {
		return date != null ? date.toLocalDate().format(DATE_FORMATTER) : "";
	}

	public static String formatLocalDate(final LocalDate date) {
		return date.format(DATE_FORMATTER);
	}

	public static String formatToMonthYear(final LocalDate date) {
		return date.format(MONTH_YEAR_FORMATTER);
	}

	public static String formatToDayMonth(final LocalDate date) {
		return date.format(DATE_MONTH_FORMATTER);
	}

	public static Map<String, BigDecimal> initPurchaseHistory(final TimeRange timeRange) {
		final Map<String, BigDecimal> purchaseHistory = new LinkedHashMap<>();

		switch (timeRange) {
		case WEEK:
			for (int i = 7; i > 0; i--) {
				final LocalDate date = LocalDate.now().minusDays(i);
				purchaseHistory.put(ReportUtils.formatToDayMonth(date), BigDecimal.ZERO);
			}
			break;
		case MONTH:
			for (int i = 30; i > 0; i--) {
				final LocalDate date = LocalDate.now().minusDays(i);
				purchaseHistory.put(ReportUtils.formatToDayMonth(date), BigDecimal.ZERO);
			}
			break;
		case YEAR:
			for (int i = 11; i >= 0; i--) {
				final LocalDate date = LocalDate.now().minusMonths(i);
				purchaseHistory.put(ReportUtils.formatToMonthYear(date), BigDecimal.ZERO);
			}
			break;

		default:
			throw new IllegalArgumentException("TimeRange is not correct");
		}
		return purchaseHistory;
	}

	public static String buildKeyPurchaseHistory(final TimeRange timeRange, final Timestamp timeStamp) {
		String key = "";
		switch (timeRange) {
		case WEEK:
			key = ReportUtils.formatToDayMonth(timeStamp.toLocalDateTime().toLocalDate());
			break;
		case MONTH:
			key = ReportUtils.formatToDayMonth(timeStamp.toLocalDateTime().toLocalDate());
			break;
		case YEAR:
			key = ReportUtils.formatToMonthYear(timeStamp.toLocalDateTime().toLocalDate());
			break;
		default:
			throw new IllegalArgumentException("TimeRange is not correct");
		}
		return key;
	}

	/**
	 * Convert milliseconds to seconds with round.
	 *
	 * @param mili
	 *            milliseconds
	 * @return second
	 */
	public static long miliToSecond(final long mili) {
		return mili / 1000;
	}

	public static Integer calculateTrend(final int previous, final int current) {
		Integer trend = null;
		if (previous > 0 && current > 0) {
			trend = (int) Math.round((double) (current - previous) / previous * 100);
		}
		return trend;
	}

	public static Integer calculateTrend(final double previous, final double current) {
		Integer trend = null;
		if (previous > 0 && current > 0) {
			trend = (int) Math.round((current - previous) / previous * 100);
		}
		return trend;
	}

	public static boolean isBeforeYesterday(final LocalDateTime time) {
		LOG.info("--- >>> time {}", time);
		final LocalDateTime startYesterday = LocalDate.now().minusDays(1).atTime(LocalTime.MIN);
		LOG.info("--- >>> startYesterday {}", startYesterday);
		LOG.info("--- >>> startYesterday.isAfter(time) {}", startYesterday.isAfter(time));
		return startYesterday.isAfter(time);
	}

	public static boolean isBeforeYesterday(final LocalDate time) {
		return LocalDate.now().minusDays(1).isAfter(time);
	}

	public static LocalDateTime getStartOfRunTime(final LocalDateTime latestRun) {
		return LocalDateTime.of(latestRun.toLocalDate(), LocalTime.MIN).plusDays(1);
	}

	public static long getCurrentSecond(final LocalDateTime time) {
		return time.atZone(ZoneId.systemDefault()).toEpochSecond();
	}

	public static double formatDoubleNullToZero(final Double value) {
		return value == null ? 0.0 : NumberUtils.roundUpDouble(value);
	}

	public static final SortFieldMapping SORT_FIELD_MAPPING_FOR_USER_LISTING = new SortFieldMapping() {

		@Override
		public String getFullNameSortField() {
			return ReportUtils.FULL_NAME;
		}

		@Override
		public String getActivatedSortField() {
			return "activated";
		}

		@Override
		public String getJoinDateSortField() {
			return "join_date";
		}

		@Override
		public String getCountrySortField() {
			return "country";
		}

		@Override
		public String getCitySortField() {
			return "city";
		}

		@Override
		public String getUsernameSortField() {
			return "username";
		}
	};
	
	public static final SortFieldMapping SORT_FIELD_MAPPING_FOR_PT_LISTING = new SortFieldMapping() {

		@Override
		public String getFullNameSortField() {
			return ReportUtils.FULL_NAME;
		}

		@Override
		public String getActivatedSortField() {
			return "activated";
		}

		@Override
		public String getF8RevenueSortField() {
			return "sr.total_commission";
		}

		@Override
		public String getPtRevenueSortField() {
			return "sr.pt_total_fee";
		}

		@Override
		public String getLevelSortField() {
			return "level";
		}

		@Override
		public String getDocumentStatusSortField() {
			return "doc_status";
		}

		@Override
		public String getJoinDateSortField() {
			return "join_date";
		}

		@Override
		public String getCountrySortField() {
			return "country";
		}

		@Override
		public String getCitySortField() {
			return "city";
		}

		@Override
		public String getUsernameSortField() {
			return "username";
		}
	};

	public static String getLocalizeCountry(final String country) {
		return StringUtils.isEmpty(country) ? "" : Country.valueOf(country.toUpperCase(Locale.US)).getName();
	}

	public static long nullToZero(final BigInteger input) {
		return input != null ? input.longValue() : 0;
	}

	public static String formatCurrency(Double price) {
        if (price == null) {
            return "";
        }
		final DecimalFormat formatPrice = new DecimalFormat(PRICE_FORMAT);
		return formatPrice.format(price);
	}

    public static String formatDate(final Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return TIMESTAMP_DATE_FORMATTER.format(timestamp);
    }

    public static String formatTime(final Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return TIME_FORMATTER.format(timestamp);
    }
}
