package edu.prokhorovmax.bigdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = "edu.prokhorovmax.bigdata.repository",
        entityManagerFactoryRef = "coreEntityManager",
        transactionManagerRef = "coreTransactionManager"
)
public class BigData {

    public static void main(String[] args) {
        SpringApplication.run(BigData.class, args);
    }

}
