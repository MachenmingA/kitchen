package com.mykitchen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mykitchen.mapper")
public class MyKitchenApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyKitchenApplication.class, args);
    }
}
