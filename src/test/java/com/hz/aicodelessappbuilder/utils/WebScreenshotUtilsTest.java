package com.hz.aicodelessappbuilder.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class WebScreenshotUtilsTest {

    @Resource
    private ApiScreenshotUtils apiScreenshotUtils;

    @Test
    void saveWebPageScreenshot() {
        String testUrl = "https://www.deepseek.com/";
        String webPageScreenshot = apiScreenshotUtils.saveWebPageScreenshot(testUrl);
        Assertions.assertNotNull(webPageScreenshot);
    }
}

