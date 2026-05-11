package com.katomegumi.zxpicturebackend.manager.cache;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.katomegumi.zxpicturebackend.common.constant.UserConstant;
import com.katomegumi.zxpicturebackend.common.exception.BusinessException;
import com.katomegumi.zxpicturebackend.common.exception.ErrorCode;
import com.katomegumi.zxpicturebackend.common.constant.CacheConstant;
import com.katomegumi.zxpicturebackend.common.util.RedisUtils;
import com.katomegumi.zxpicturebackend.entity.UserInfo;
import com.katomegumi.zxpicturebackend.mapper.UserInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author : lr
 * @description : 用户缓存管理
 * @createDate : 2026/3/15 下午3:59
 */
@Component
@RequiredArgsConstructor
public class UserCacheManager {

    private final RedisUtils redisUtils;

    private final UserInfoMapper userInfoMapper;

    /**
     * 基于实体 添加缓存  UserDetailVO 类型
     *
     * @param userInfo 用户实体
     */
    public void setUserCache(UserInfo userInfo) {
        redisUtils.set(CacheConstant.USER.USER_LOGIN_STATE + userInfo.getId(), JSONUtil.toJsonStr(userInfo), RandomUtil.randomInt(10) + 30, TimeUnit.MINUTES);
    }

    /**
     * 基于用户id添加缓存
     *
     * @param userId 用户id
     */
    public void setUserCache(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (userInfo == null) {
            //没有设置默认值 缓存穿透
            redisUtils.set(CacheConstant.USER.USER_LOGIN_STATE + userId, UserConstant.EMPTY_USER, 3, TimeUnit.MINUTES);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        setUserCache(userInfo);
    }

    /**
     * 基于用户id获取用户缓存;没有自行加载
     *
     * @param userId 用户id
     * @return UserInfo 用户信息
     */
    public UserInfo getUserInfoCache(Long userId) {
        String jsonStr = redisUtils.get(CacheConstant.USER.USER_LOGIN_STATE + userId);
        if (jsonStr == null) {
            setUserCache(userId);
        }
        jsonStr = redisUtils.get(CacheConstant.USER.USER_LOGIN_STATE + userId);
        if (UserConstant.EMPTY_USER.equals(jsonStr)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        return JSONUtil.toBean(jsonStr, UserInfo.class);
    }

    /**
     * 删除缓存
     *
     * @param userId 用户id
     * @return true成功 false失败
     */
    public Boolean deleteUserCache(Long userId) {
        return redisUtils.delete(CacheConstant.USER.USER_LOGIN_STATE + userId);
    }
}

