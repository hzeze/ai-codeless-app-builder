package com.hz.aicodelessappbuilder;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.hz.aicodelessappbuilder.mapper")
public class AiCodelessAppBuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCodelessAppBuilderApplication.class, args);
    }

}
