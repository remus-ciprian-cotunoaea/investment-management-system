package com.investment.orders;

import com.investment.orders.configuration.OrdersTopicsProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(OrdersTopicsProps.class)
@SpringBootApplication
public class OrdersMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdersMsApplication.class, args);
    }

}
