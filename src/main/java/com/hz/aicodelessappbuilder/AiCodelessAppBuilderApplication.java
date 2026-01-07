package com.hz.aicodelessappbuilder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hz.aicodelessappbuilder.mapper")
public class AiCodelessAppBuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCodelessAppBuilderApplication.class, args);
    }

}
