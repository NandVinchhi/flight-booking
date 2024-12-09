package com.dxbair.services.flightbooking;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI flightBookingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flight Booking REST API")
                        .description("Flight Booking REST API Documentation")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Shameer Kunjumohamed")
                                .url("www.sameerean.com")
                                .email("sameerean@gmail.com"))
                        .license(new License()
                                .name("License of API")
                                .url("API license URL")));
    }
}
