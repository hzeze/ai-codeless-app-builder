package com.hz.aicodelessappbuilder.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 消息类型枚举
 *
 * @author 阿泽
 */
@Getter
public enum MessageTypeEnum {

    USER("用户消息", "user"),
    AI("AI消息", "ai");

    private final String text;

    private final String value;

    MessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static MessageTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (MessageTypeEnum anEnum : MessageTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}