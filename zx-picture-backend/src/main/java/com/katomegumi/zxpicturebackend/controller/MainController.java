package com.katomegumi.zxpicturebackend.controller;

import com.katomegumi.zxpicturebackend.common.resp.BaseResponse;
import com.katomegumi.zxpicturebackend.common.util.ResultUtils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lr
 * @description : 测试
 */
@RestController
@RequestMapping("/")
public class MainController {
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public BaseResponse<String> health(){
        return ResultUtils.success("ok");
    }
}
