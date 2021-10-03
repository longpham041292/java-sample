package asia.cmg.f8.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private String defaultLang;
    private String webPortal;
    private String commonLib;
    private String termCondUrl;
    private Integer notifySessionStartPeriodInMins;
    private String defaultGroupId;
    private String euGroupId;
    private String ptGroupId;
    private String[] f8UserUuids;

    private final Mail mail = new Mail();
    
    private final Database database = new Database();
    
    private final OneSignal oneSignal = new OneSignal();
    
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

    public Database getDatabase() {
		return database;
	}

	public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(final String defaultLang) {
        this.defaultLang = defaultLang;
    }

    public String getWebPortal() {
        return webPortal;
    }

    public void setWebPortal(final String webPortal) {
        this.webPortal = webPortal;
    }

    public static class Mail {

        private String from;

        public String getFrom() {
            return from;
        }

        public void setFrom(final String from) {
            this.from = from;
        }
    }
    
    public static class OneSignal {
    	private String appId;
    	private String apiKey;
    	private String allSegment;
    	
		public String getAppId() {
			return appId;
		}
		
		public void setAppId(String appId) {
			this.appId = appId;
		}
		
		public String getApiKey() {
			return apiKey;
		}
		
		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
		}

		public String getAllSegment() {
			return allSegment;
		}

		public void setAllSegment(String allSegment) {
			this.allSegment = allSegment;
		}
    }

    public Mail getMail() {
        return mail;
    }

    public String getCommonLib() {
        return commonLib;
    }

    public void setCommonLib(final String commonLib) {
        this.commonLib = commonLib;
    }

    public String getTermCondUrl() {
        return termCondUrl;
    }

    public void setTermCondUrl(final String termCondUrl) {
        this.termCondUrl = termCondUrl;
    }

    public Integer getNotifySessionStartPeriodInMins() {
        return notifySessionStartPeriodInMins;
    }

    public void setNotifySessionStartPeriodInMins(final Integer notifySessionStartPeriodInMins) {
        this.notifySessionStartPeriodInMins = notifySessionStartPeriodInMins;
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

	public String[] getF8UserUuids() {
		return f8UserUuids;
	}

	public void setF8UserUuids(String[] f8UserUuids) {
		this.f8UserUuids = f8UserUuids;
	}

	public OneSignal getOneSignal() {
		return oneSignal;
	}
}
