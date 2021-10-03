package asia.cmg.f8.commerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private ResourceLoader resourceLoader;
 
    @Autowired
    private DataSource dataSource;

    @Autowired
    private CommerceProperties commerceProps;

    @Bean
    public DataSourceInitializer initializeDatasource() {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        if (commerceProps.getDatabase().isInitDb()) {
            final ResourceDatabasePopulator dbPopulator = new ResourceDatabasePopulator();
            dbPopulator.setSeparator("$$");
            dbPopulator.addScript(resourceLoader.getResource("classpath:"
                    + commerceProps.getDatabase().getInitDbSource()));
            initializer.setDatabasePopulator(dbPopulator);
        }
        return initializer;
    }

}
