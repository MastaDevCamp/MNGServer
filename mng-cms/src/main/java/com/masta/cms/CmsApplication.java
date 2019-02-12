package com.masta.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class CmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
    }

}