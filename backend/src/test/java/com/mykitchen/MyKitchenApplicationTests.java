package com.mykitchen;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
class MyKitchenApplicationTests {

    @Test
    void contextLoads() {
        // Basic context load test
    }
}
