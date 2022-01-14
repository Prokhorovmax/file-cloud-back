package edu.prokhorovmax.bigdata.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Кофигурация Swagger
 */
@Configuration
@EnableSwagger2
@ConditionalOnExpression(value = "${swagger.enabled:true}")
public class SwaggerConfig {

    @Bean
    public Docket apiSwaggerDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("edu.prokhorovmax.bigdata.rest"))
                .paths(PathSelectors.any())
                .build();
    }

}
