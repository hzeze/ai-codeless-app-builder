package com.hz.aicodelessappbuilder.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.hz.aicodelessappbuilder.constant.AppConstant;
import com.hz.aicodelessappbuilder.exception.BusinessException;
import com.hz.aicodelessappbuilder.exception.ErrorCode;
import com.hz.aicodelessappbuilder.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 代码文件保存模板类
 *
 * @param <T>
 */
public abstract class CodeFileSaverTemplate<T> {

    // 文件保存根目录
    protected static final String FILE_SAVE_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;


    /**
     * 保存代码文件
     * @param result
     * @param appId
     * @return
     */
    public final File saveCode(T result, Long appId) {
        //1.验证输入
        validateInput(result);
        //2.构建统一保持路径
        String baseDirPath = buildSavePath(appId);
        //3.保存文件
        saveFiles(result, baseDirPath);
        //4.返回结果
        return new File(baseDirPath);
    }

    /**
     * 构建保存路径
     *
     * @return
     */
    protected final String buildSavePath(Long appId) {
        if (appId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "appId为空");
        }
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     */
    protected final void writeToFile(String dirPath, String filename, String content) {
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

    /**
     * 验证输入
     *
     * @param result
     */
    protected void validateInput(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存文件时，输入结果为空");
        }
    }

    protected abstract CodeGenTypeEnum getCodeType();

    protected abstract void saveFiles(T result, String baseDirPath);
}
