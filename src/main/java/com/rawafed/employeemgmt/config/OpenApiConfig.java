package com.rawafed.employeemgmt.config;

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
    public OpenAPI myOpenAPI() {

        var local = new Server();
        local.setUrl("http://localhost:8080");
        local.setDescription("Local development of Rawafed employee management app");

        var server = new Server();
        server.setUrl("http://employee-management.swagger.io/v1");
        server.setDescription("Server URL of Employee Management");


        Contact contact = new Contact();
        contact.setEmail("mabulmagd55@email.com");
        contact.setName("Muhammad Abulmagd");
        contact.setUrl("https://github.com/mmajd");

        License mitLicense = new License().name("Licence type")
                .url("https://mit-license.org/");

        Info info = new Info()
                .title("Employee Management API")
                .version("1.0")
                .contact(contact)
                .description("Open API code generation and swagger ui of Employee Management")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(local, server));
    }
}
