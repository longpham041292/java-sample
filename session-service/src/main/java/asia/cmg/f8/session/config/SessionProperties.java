package asia.cmg.f8.session.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "session")
public class SessionProperties {

    private String defCountry;
    private String popupSchedulePeriod;
    private String defLang;
    private String defCurrency;
    private String dateTimeFormat;
    private String newPTMonthsAhead;
    private String currentPTMonthsAhead;
    private String removeAvailabilityMonthsPast;
    private int numOfThreadResetConnection;
    private String[] f8UserUuids;
    private String defaultGroupId;
    private String euGroupId;
    private String ptGroupId;
    private String localTimeZoneId;
    private String leepAccountUuid;
    private Double vat;
    private Double pit;

	private final Database database = new Database();
    private final Export export = new Export();

    public int getNumOfThreadResetConnection() {
		return numOfThreadResetConnection;
	}

	public void setNumOfThreadResetConnection(int numOfThreadResetConnection) {
		this.numOfThreadResetConnection = numOfThreadResetConnection;
	}
	
    public String getCountry() {
        return defCountry;
    }

    public void setDefCountry(final String defCountry) {
        this.defCountry = defCountry;
    }

    public String getCurrency() {
        return defCurrency;
    }

    public void setDefCurrency(final String defCurrency) {
        this.defCurrency = defCurrency;
    }

    public Database getDatabase() {
        return database;
    }

    public String getLang() {
        return defLang;
    }

    public void setDefLang(final String defLang) {
        this.defLang = defLang;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(final String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public Export getExport() {
        return export;
    }

    public String getRemoveAvailabilityMonthsPast() {
        return removeAvailabilityMonthsPast;
    }

    public void setRemoveAvailabilityMonthsPast(final String removeAvailabilityMonthsPast) {
        this.removeAvailabilityMonthsPast = removeAvailabilityMonthsPast;
    }

    public String getNewPTMonthsAhead() {
        return newPTMonthsAhead;
    }

    public void setNewPTMonthsAhead(final String newPTMonthsAhead) {
        this.newPTMonthsAhead = newPTMonthsAhead;
    }

    public String getCurrentPTMonthsAhead() {
        return currentPTMonthsAhead;
    }

    public void setCurrentPTMonthsAhead(final String currentPTMonthsAhead) {
        this.currentPTMonthsAhead = currentPTMonthsAhead;
    }

    public String getPopupSchedulePeriod() {
        return popupSchedulePeriod;
    }

    public void setPopupSchedulePeriod(final String popupSchedulePeriod) {
        this.popupSchedulePeriod = popupSchedulePeriod;
    }

    public String[] getF8UserUuids() {
		return f8UserUuids;
	}

	public void setF8UserUuids(String[] f8UserUuids) {
		this.f8UserUuids = f8UserUuids;
	}
	
	public String getDefaultGroupId() {
		return defaultGroupId;
	}

	public void setDefaultGroupId(String defaultGroupId) {
		this.defaultGroupId = defaultGroupId;
	}

	public String getEuGroupId() {
		return euGroupId;
	}

	public void setEuGroupId(String euGroupId) {
		this.euGroupId = euGroupId;
	}

	public String getPtGroupId() {
		return ptGroupId;
	}

	public void setPtGroupId(String ptGroupId) {
		this.ptGroupId = ptGroupId;
	}

	public String getLocalTimeZoneId() {
		return localTimeZoneId;
	}

	public void setLocalTimeZoneId(String localTimeZoneId) {
		this.localTimeZoneId = localTimeZoneId;
	}

	public String getLeepAccountUuid() {
		return leepAccountUuid;
	}

	public void setLeepAccountUuid(String leepAccountUuid) {
		this.leepAccountUuid = leepAccountUuid;
	}
	
	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public Double getPit() {
		return pit;
	}

	public void setPit(Double pit) {
		this.pit = pit;
	}

	public static class Database {
        private boolean initDb;
        private String initDbSource;

        public String getInitDbSource() {
            return initDbSource;
        }

        public void setInitDbSource(final String initDbSource) {
            this.initDbSource = initDbSource;
        }

        public boolean isInitDb() {
            return initDb;
        }

        public void setInitDb(final boolean initDb) {
            this.initDb = initDb;
        }

    }

    public static class Export {
        private String members;

        public String getMembers() {
            return members;
        }

        public void setMembers(final String members) {
            this.members = members;
        }

    }
}
