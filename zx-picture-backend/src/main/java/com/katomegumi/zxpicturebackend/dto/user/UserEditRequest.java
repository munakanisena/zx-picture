package com.katomegumi.zxpicturebackend.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : lr
 * @description : 用户编辑请求
 * @createDate : 2025/5/10 上午11:56
 */
@Data
public class UserEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 登录名及昵称
     */
    private String name;

    /**
     * 手机号 预留
     */
    private String phone;

    /**
     * 用户简介
     */
    private String introduction;

}

