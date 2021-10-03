package asia.cmg.f8.report.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private DataSource dataSource;
    
    @Bean
    public DataSourceInitializer initializeDatasource() {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        return initializer;
    }

}
