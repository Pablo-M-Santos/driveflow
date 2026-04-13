package com.pablo.driveflow_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI driveFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DriveFlow API")
                        .version("v1.0.0")
                        .description("API para gestão de aluguel de veículos - Sistema completo de reservas e controle de frota")
                        .contact(new Contact()
                                .name("Pablo Santos")
                                .email("pablo@driveflow.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

