package edu.prokhorovmax.bigdata.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Configuration;


import java.util.HashMap;

/**
 * Конфигурация hibernate
 */
@Configuration
public class HibernateConfig {

    @Value("${spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation:true}")
    public boolean hibernateLobNonContextualCreation;

    @Value("${spring.jpa.hibernate.ddl-auto:none}")
    public String hibernateHbm2ddlAuto;

    @Value("${spring.jpa.properties.hibernate.show_sql:false}")
    public boolean hibernateShowSql;

    @Value("${spring.jpa.properties.hibernate.format_sql:true}")
    public boolean hibernateFormatSql;

    @Value("${spring.jpa.properties.hibernate.use_sql_comments:false}")
    public boolean hibernateUseSqlComment;

    public HashMap<String, Object> getJpaProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
        properties.put("hibernate.show_sql", hibernateShowSql);
        properties.put("hibernate.format_sql", hibernateFormatSql);
        properties.put("hibernate.use_sql_comments", hibernateUseSqlComment);
        properties.put("hibernate.jdbc.lob.non_contextual_creation", hibernateLobNonContextualCreation);
        properties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        return properties;
    }
}
