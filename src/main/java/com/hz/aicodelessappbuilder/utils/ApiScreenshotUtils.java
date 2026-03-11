package com.hz.aicodelessappbuilder.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.hz.aicodelessappbuilder.exception.BusinessException;
import com.hz.aicodelessappbuilder.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;

/**
 * 截图工具类 - 使用第三方 API 实现 <a href="https://xxapi.cn/">...</a>
 */
@Slf4j
@Component
public class ApiScreenshotUtils {

    private final RestTemplate restTemplate;

    @Value("${screenshot.api-url}")
    private String apiUrl;

    @Value("${screenshot.api-key}")
    private String apiKey;

    @Value("${screenshot.width}")
    private Integer width;

    public ApiScreenshotUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 生成网页截图
     *
     * @param webUrl 网页URL
     * @return 压缩后的截图文件路径，失败返回null
     */
    public String saveWebPageScreenshot(String webUrl) {
        if (StrUtil.isBlank(webUrl)) {
            log.error("网页URL不能为空");
            return null;
        }

        try {
            String requestUrl = String.format("%s?url=%s&return=json&width=%d",
                    apiUrl, webUrl, width);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            log.info("请求截图API: {}", requestUrl);

            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(requestUrl),
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = response.getBody();
                log.info("API响应: {}", responseBody);

                String imageUrl = extractImageUrl(responseBody);
                
                if (imageUrl != null) {
                    return downloadAndCompressImage(imageUrl);
                } else {
                    log.error("无法从响应中提取图片URL");
                    return null;
                }
            } else {
                log.error("截图API请求失败，状态码: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("网页截图失败: {}", webUrl, e);
            return null;
        }
    }

    /**
     * 从API响应中提取图片URL
     */
    private String extractImageUrl(String responseBody) {
        try {
            if (responseBody.contains("\"data\":")) {
                int dataStart = responseBody.indexOf("\"data\":");
                if (dataStart != -1) {
                    int valueStart = responseBody.indexOf("\"", dataStart + 7);
                    if (valueStart != -1) {
                        int valueEnd = responseBody.indexOf("\"", valueStart + 1);
                        if (valueEnd != -1) {
                            return responseBody.substring(valueStart + 1, valueEnd);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("提取图片URL失败", e);
        }
        return null;
    }

    /**
     * 下载图片并压缩
     */
    private String downloadAndCompressImage(String imageUrl) {
        try {
            String rootPath = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "screenshots"
                    + File.separator + RandomUtil.randomNumbers(5);
            FileUtil.mkdir(rootPath);

            String originalImagePath = rootPath + File.separator + "original.png";
            String compressedImagePath = rootPath + File.separator + "compressed.jpg";

            ResponseEntity<byte[]> response = restTemplate.getForEntity(URI.create(imageUrl), byte[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                FileUtil.writeBytes(response.getBody(), originalImagePath);
                log.info("图片下载成功: {}", originalImagePath);

                compressImage(originalImagePath, compressedImagePath);
                FileUtil.del(originalImagePath);
                log.info("压缩图片保存成功: {}", compressedImagePath);
                return compressedImagePath;
            } else {
                log.error("图片下载失败，状态码: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("下载并压缩图片失败: {}", imageUrl, e);
            return null;
        }
    }

    /**
     * 压缩图片
     */
    private void compressImage(String originalImagePath, String compressedImagePath) {
        try {
            cn.hutool.core.img.ImgUtil.compress(
                    FileUtil.file(originalImagePath),
                    FileUtil.file(compressedImagePath),
                    0.3f
            );
            log.info("图片压缩成功: {} -> {}", originalImagePath, compressedImagePath);
        } catch (Exception e) {
            log.error("压缩图片失败: {} -> {}", originalImagePath, compressedImagePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩图片失败");
        }
    }
}
