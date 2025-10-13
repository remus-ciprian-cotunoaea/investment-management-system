package com.investment.positions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class PositionsMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PositionsMsApplication.class, args);
    }

}
