package com.katomegumi.zxpicturebackend.dto.user;


import com.katomegumi.zxpicturebackend.common.req.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author lr
 */ //继承pageRequest实现分页查询 管理员查询请求
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
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
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 会员编号
     */
    private Long vipNumber;

    /**
     * 用户角色（USER-普通用户, ADMIN-管理员）
     */
    private String role;

    /**
     * 是否为会员;0-否 1-是
     */
    private Integer isVip;

    /**
     * 是否禁用（0-正常, 1-禁用）
     */
    private Integer isDisabled;

}
