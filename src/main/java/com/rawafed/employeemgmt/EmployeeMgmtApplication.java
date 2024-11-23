package com.rawafed.employeemgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class EmployeeMgmtApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeMgmtApplication.class, args);

    }
}
