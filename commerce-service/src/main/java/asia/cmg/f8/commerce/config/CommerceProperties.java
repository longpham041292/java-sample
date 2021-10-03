package asia.cmg.f8.commerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "commerce")
public class CommerceProperties {

    private String defCountry;
    private String defLang;
    private String defCurrency;
    private String dateTimeFormat;
    private int hoursGracePeriod;
    private String leepAccountUuid;
    private Integer vat;
    private Double pit;
    private String secretKey;

    public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Integer getVat() {
		return vat;
	}

	public void setVat(Integer vat) {
		this.vat = vat;
	}

	public Double getPit() {
		return pit;
	}

	public void setPit(Double pit) {
		this.pit = pit;
	}

	private final Database database = new Database();
    
    private final Export export = new Export();

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

    public int getHoursGracePeriod() {
        return hoursGracePeriod;
    }

    public void setHoursGracePeriod(final int hoursGracePeriod) {
        this.hoursGracePeriod = hoursGracePeriod;
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
    
    public Export getExport() {
        return export;
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

	public String getLeepAccountUuid() {
		return leepAccountUuid;
	}

	public void setLeepAccountUuid(String leepAccountUuid) {
		this.leepAccountUuid = leepAccountUuid;
	}
    
    
}
