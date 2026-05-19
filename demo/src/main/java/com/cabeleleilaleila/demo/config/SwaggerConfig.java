package com.cabeleleilaleila.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cabeleleila Leila API")
                        .description("API para gerenciamento de salão de beleza")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Cabeleleila Leila")
                                .email("contato@cabeleleilaleila.com.br")));
    }

}
