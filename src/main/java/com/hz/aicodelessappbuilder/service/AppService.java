package com.hz.aicodelessappbuilder.service;

import com.hz.aicodelessappbuilder.model.dto.app.AppQueryRequest;
import com.hz.aicodelessappbuilder.model.entity.User;
import com.hz.aicodelessappbuilder.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.hz.aicodelessappbuilder.model.entity.App;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author 阿泽
 */
public interface AppService extends IService<App> {

    /**
     * 获取应用视图对象
     *
     * @param app 应用实体
     * @return 应用视图对象
     */
    AppVO getAppVO(App app);

    /**
     * 获取应用视图对象列表
     *
     * @param appList 应用实体列表
     * @return 应用视图对象列表
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 校验应用数据
     *
     * @param app 应用实体
     * @param add 是否为添加操作
     */
    void validApp(App app, boolean add);

    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    String deployApp(Long appId, User loginUser);
}
