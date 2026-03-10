package com.hz.aicodelessappbuilder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.hz.aicodelessappbuilder.exception.BusinessException;
import com.hz.aicodelessappbuilder.exception.ErrorCode;
import com.hz.aicodelessappbuilder.manager.CosManager;
import com.hz.aicodelessappbuilder.model.dto.user.UserQueryRequest;
import com.hz.aicodelessappbuilder.model.enums.UserRoleEnum;
import com.hz.aicodelessappbuilder.model.vo.LoginUserVO;
import com.hz.aicodelessappbuilder.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.hz.aicodelessappbuilder.model.entity.User;
import com.hz.aicodelessappbuilder.mapper.UserMapper;
import com.hz.aicodelessappbuilder.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static com.hz.aicodelessappbuilder.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/hzeze">阿泽</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private CosManager cosManager;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.参数校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码格式错误");
        }
        // 校验用户名只能包含字母、数字和下划线
        if (!userAccount.matches("^[a-zA-Z0-9_]+$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名只能包含字母、数字和下划线");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }

        //2.查询用户是否已存在
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在");
        }

        //3.加密
        String encryptPassword = getEncryptPassword(userPassword);

        //4.插入数据库记录
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("用户" + userAccount);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败");
        }
        return user.getId();
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "heze";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.参数校验
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //加密
        String encryptPassword = getEncryptPassword(userPassword);
        //2.查询用户是否存在
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("userAccount", userAccount);
        wrapper.eq("userPassword", encryptPassword);
        User user = this.mapper.selectOneByQuery(wrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        //3.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        //4.脱敏
        return getLoginUserVO(user);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        //判断是否已经登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        return (User) userObj;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("userRole", userRole)
                .like("userAccount", userAccount)
                .like("userName", userName)
                .like("userProfile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public String uploadBase64AvatarToCos(String base64Data, Long userId) {
        try {
            // 解析 base64 数据
            String[] parts = base64Data.split(",");
            if (parts.length != 2) {
                log.error("base64 数据格式错误");
                return null;
            }
            String base64String = parts[1];
            String mimeType = parts[0].substring(parts[0].indexOf(":") + 1, parts[0].indexOf(";"));
            
            // 确定文件扩展名
            String extension = "jpg";
            if (mimeType.contains("png")) {
                extension = "png";
            } else if (mimeType.contains("gif")) {
                extension = "gif";
            } else if (mimeType.contains("webp")) {
                extension = "webp";
            }
            
            // 解码 base64
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            
            // 创建临时文件
            String tempFileName = UUID.randomUUID().toString().substring(0, 8) + "_avatar." + extension;
            Path tempDir = Files.createTempDirectory("avatar_upload_");
            Path tempFile = tempDir.resolve(tempFileName);
            Files.write(tempFile, imageBytes);
            
            try {
                // 生成 COS 对象键
                String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                String cosKey = String.format("/avatars/%s/%s_%s.%s", datePath, userId, UUID.randomUUID().toString().substring(0, 8), extension);
                
                // 上传到 COS
                String cosUrl = cosManager.uploadFile(cosKey, tempFile.toFile());
                log.info("头像上传COS成功: userId={}, cosUrl={}", userId, cosUrl);
                return cosUrl;
            } finally {
                // 清理临时文件
                FileUtil.del(tempDir.toFile());
            }
        } catch (Exception e) {
            log.error("上传头像到COS失败: userId={}, error={}", userId, e.getMessage(), e);
            return null;
        }
    }

}
