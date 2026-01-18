package com.hz.aicodelessappbuilder.core.parser;

/**
 * 代码解析器策略接口
 */
public interface CodeParser<T> {

    /**
     * 解析代码
     */
    T parseCode(String codeContent);
}

