package com.investment.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.investment.accounts.configuration")
public class AccountsMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountsMsApplication.class, args);
    }
}