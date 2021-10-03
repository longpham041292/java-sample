package asia.cmg.f8.notification.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DatabaseConfiguration {

	@Autowired
    private ResourceLoader resourceLoader;
	
	@Autowired
    private DataSource dataSource;
	
	@Autowired
	private NotificationProperties notifProps;
	
	@Bean
    public DataSourceInitializer initializeDatasource() {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        if (notifProps.getDatabase().isInitDb()) {
            final ResourceDatabasePopulator dbPopulator = new ResourceDatabasePopulator();
            dbPopulator.setSeparator("$$");
            dbPopulator.addScript(resourceLoader.getResource("classpath:"
                    + notifProps.getDatabase().getInitDbSource()));
            initializer.setDatabasePopulator(dbPopulator);
        }
        return initializer;
    }
}
