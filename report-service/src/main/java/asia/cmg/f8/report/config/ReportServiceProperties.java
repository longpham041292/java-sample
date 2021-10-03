package asia.cmg.f8.report.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "report")
public class ReportServiceProperties {

	private String leepAccountUuid;
    private Integer vat;
    private Double pit;
    private String secretKey;
    
	public String getLeepAccountUuid() {
		return leepAccountUuid;
	}

	public void setLeepAccountUuid(String leepAccountUuid) {
		this.leepAccountUuid = leepAccountUuid;
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

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	private final Database database = new Database();
	
	public Database getDatabase() {
		return database;
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
}
