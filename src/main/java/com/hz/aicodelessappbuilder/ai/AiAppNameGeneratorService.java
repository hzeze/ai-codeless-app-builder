package com.hz.aicodelessappbuilder.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * AI应用名称生成服务
 * 使用AI根据用户需求生成简洁、准确的应用名称
 *
 */
public interface AiAppNameGeneratorService {

    /**
     * 根据用户需求生成应用名称
     *
     * @param userPrompt 用户输入的需求描述
     * @return 生成的应用名称
     */
    @SystemMessage(fromResource = "prompt/app-name-generator-system-prompt.txt")
    String generateAppName(String userPrompt);
}