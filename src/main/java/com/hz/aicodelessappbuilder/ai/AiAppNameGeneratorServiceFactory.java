package com.hz.aicodelessappbuilder.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI应用名称生成服务工厂
 *
 */
@Slf4j
@Configuration
public class AiAppNameGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    /**
     * 创建AI应用名称生成服务实例
     */
    @Bean
    public AiAppNameGeneratorService aiAppNameGeneratorService() {
        return AiServices.builder(AiAppNameGeneratorService.class)
                .chatModel(chatModel)
                .build();
    }
}