package asia.cmg.f8.session.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;

@Configuration
public class HibernateJpaOrmConfiguration extends HibernateJpaAutoConfiguration {
    public HibernateJpaOrmConfiguration(DataSource dataSource, JpaProperties jpaProperties,
                                        ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider) {
        super(dataSource, jpaProperties, jtaTransactionManagerProvider);
    }

    @Bean
    @Override
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder) {
        final LocalContainerEntityManagerFactoryBean managerFactoryBean = super.entityManagerFactory(factoryBuilder);
        managerFactoryBean.setMappingResources("orm/orm-SessionFinancial.xml", "orm/orm-OrderFinancialRecord.xml");
        return managerFactoryBean;
    }
}
