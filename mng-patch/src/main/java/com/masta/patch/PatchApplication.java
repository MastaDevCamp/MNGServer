package com.masta.patch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatchApplication.class, args);
    }

}