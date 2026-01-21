package com.hz.aicodelessappbuilder.service;

import com.hz.aicodelessappbuilder.model.dto.app.AppQueryRequest;
import com.hz.aicodelessappbuilder.model.entity.User;
import com.hz.aicodelessappbuilder.model.vo.AppVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.hz.aicodelessappbuilder.model.entity.App;
import reactor.core.publisher.Flux;

import java.io.Serializable;
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
     * 分页获取应用视图对象列表
     * @param appPage 应用分页对象
     * @return 应用视图对象分页列表
     */
    Page<AppVO> getAppVOPage(Page<App> appPage);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);


    /**
     * 生成代码
     * @param appId 应用id
     * @param message 消息
     * @param loginUser 登录用户
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 部署应用
     * @param appId 应用id
     * @param loginUser 登录用户
     * @return
     */
    String deployApp(Long appId, User loginUser);


    /**
     * 根据id删除应用
     * @param id
     * @return
     */
    boolean removeById(Serializable id);
}
