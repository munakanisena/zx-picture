package com.katomegumi.zxpicturebackend.security.permission;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.katomegumi.zxpicturebackend.security.model.SpaceUserAuthConfig;
import com.katomegumi.zxpicturebackend.security.model.SpaceUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author lr
 * @description 获取对应的权限
 */
@Component
@RequiredArgsConstructor
public class SpaceUserAuthManager {

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     *
     * @param role 用户角色
     * @return 权限列表
     */
    public List<String> getSpaceUserPermissionsByRole(String role) {
        if (role == null) {
            return Collections.emptyList();
        }
        SpaceUserRole spaceUserRole = SPACE_USER_AUTH_CONFIG.getRoles().stream().filter(role1 -> role1.getKey().equals(role)).findFirst().orElse(null);
        if (spaceUserRole == null) {
            return Collections.emptyList();
        }
        return spaceUserRole.getPermissions();
    }
}
