package edu.prokhorovmax.bigdata.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
@EnableConfigurationProperties(DataSourceProperties.class)
public class CoreConfig {

    public static final String PERSISTENCE_UNIT_NAME = "core";
    public static final String PACKAGE_TO_SCAN = "edu.prokhorovmax.bigdata.model";

    @Bean(name = "coreEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean coreEntityManagerFactory(HibernateConfig config) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(coreDataSource());
        em.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        em.setPackagesToScan(PACKAGE_TO_SCAN);

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaPropertyMap(config.getJpaProperties());
        return em;
    }

    @Bean(name = "coreTransactionManager")
    @Primary
    public PlatformTransactionManager coreTransactionManager(HibernateConfig config) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(coreEntityManagerFactory(config).getObject());
        return transactionManager;
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource coreDataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }
}
