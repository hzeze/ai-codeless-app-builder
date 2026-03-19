package com.hz.aicodelessappbuilder.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * AI代码生成类型智能路由服务
 * 使用结构化输出直接返回枚举类型
 *
 */
public interface AiCodeGenTypeRoutingService {

    /**
     * 根据用户需求智能选择代码生成类型
     *
     * @param userPrompt 用户输入的需求描述
     * @return 推荐的代码生成类型（HTML、MULTI_FILE、VUE_PROJECT）或错误提示
     */
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    String routeCodeGenType(String userPrompt);
}

