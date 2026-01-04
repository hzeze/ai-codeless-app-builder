package com.hz.aicodelessappbuilder.controller;

import com.hz.aicodelessappbuilder.common.BaseResponse;
import com.hz.aicodelessappbuilder.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class Hello {

    @GetMapping("/")
    public BaseResponse<String> hello() {
        return ResultUtils.success("hello");
    }
}
