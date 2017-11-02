package com.datiti.fix.services;

import com.google.common.base.Predicate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@SpringBootApplication
@Import(SpringDataRestConfiguration.class)
@EntityScan(basePackages = {
        "com.datiti.fix.services.model"
})
@EnableMongoRepositories(basePackages = {
        "com.datiti.fix.services.repository.mongo"
})
public class MongoServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoServicesApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(eventsPaths())
                .build();
    }

    @Bean
    ApiInfo apiInfo() {
        final ApiInfoBuilder builder = new ApiInfoBuilder();
        builder.title("FIX services API with MongoDB")
                .version("0.0.1")
                .description("This API allows to: load unread FIX messages, load all received FIX messages, answer to Single Order FIX message")
                .license("GNU GPLv3");
        return builder.build();
    }

    private Predicate<String> eventsPaths() {
        return regex("/mongo_events.*");
    }
}
