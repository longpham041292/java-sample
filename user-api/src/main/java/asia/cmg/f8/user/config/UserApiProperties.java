package asia.cmg.f8.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "userapi")
public class UserApiProperties {

    private String dateTimeFormat;
    private int hoursGracePeriod;

    private final Database database = new Database();
    
    private final Export export = new Export();


    public Database getDatabase() {
        return database;
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
}
