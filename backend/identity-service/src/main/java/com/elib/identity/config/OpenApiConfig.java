package com.elib.identity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI identityServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("E-Library Identity Service API")
                .description("Identity and authentication service")
                .version("1.0.0")
                .contact(new Contact()
                    .name("E-Lib Team")
                    .email("support@elibrary.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8083")
                    .description("Identity Service"),
                new Server()
                    .url("http://localhost:8081")
                    .description("Via Gateway")
            ));
    }
}
