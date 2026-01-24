package com.hz.aicodelessappbuilder.service;

import com.hz.aicodelessappbuilder.model.dto.chathistory.ChatHistoryQueryRequest;
import com.hz.aicodelessappbuilder.model.entity.ChatHistory;
import com.hz.aicodelessappbuilder.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层
 *
 * @author <a href="https://github.com/hzeze">阿泽</a>
 */
public interface ChatHistoryService extends IService<ChatHistory> {


    /**
     * 获取查询条件
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 添加对话消息
     *
     * @param appId        应用id
     * @param message      消息内容
     * @param messageType  消息类型
     * @param userId       用户id
     * @return 添加是否成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 分页获取应用对话历史（支持向前加载更多）
     *
     * @param appId           应用id
     * @param pageSize        每页大小
     * @param lastCreateTime  最后一条对话创建时间
     * @param loginUser       登录用户
     * @return 分页对话历史
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    /**
     * 删除应用时，关联删除该应用的所有对话历史
     *
     * @param appId 应用id
     * @return 删除是否成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 将对话历史加载到内存中
     *
     * @param appId
     * @param chatMemory
     * @param maxCount
     * @return
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);
}
