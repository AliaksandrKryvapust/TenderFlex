package com.exadel.tenderflex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class TenderFlexApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenderFlexApplication.class, args);
    }

}
