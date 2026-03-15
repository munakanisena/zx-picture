package com.katomegumi.zxpicturebackend.manager.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.katomegumi.zxpicturebackend.core.common.exception.BusinessException;
import com.katomegumi.zxpicturebackend.core.common.exception.ErrorCode;
import com.katomegumi.zxpicturebackend.core.constant.CacheConstant;
import com.katomegumi.zxpicturebackend.core.util.RedisUtils;
import com.katomegumi.zxpicturebackend.model.dao.entity.UserInfo;
import com.katomegumi.zxpicturebackend.model.dao.mapper.UserInfoMapper;
import com.katomegumi.zxpicturebackend.model.vo.user.UserDetailVO;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author : Megumi
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
     * @param userInfo 用户实体
     */
    public void setUserCache(UserInfo userInfo){
        redisUtils.set(CacheConstant.USER.USER_LOGIN_STATE+userInfo.getId(),
                JSONUtil.toJsonStr(userInfo),
                RandomUtil.randomInt(10)+30,
                TimeUnit.MINUTES);
    }

    /**
     * 基于用户id添加缓存
     * @param userId 用户id
     */
    public void setUserCache(Long userId){
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (userInfo==null){
            redisUtils.set(CacheConstant.USER.USER_LOGIN_STATE+userId,"empty_user",3,TimeUnit.MINUTES);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        setUserCache(userInfo);
    }

    /**
     * 基于用户id获取用户缓存;没有自行加载
     * @param userId 用户id
     * @return UserDetailVO
     */
    public UserInfo getUserDetailVOCache(Long userId){
        String jsonStr = redisUtils.get(CacheConstant.USER.USER_LOGIN_STATE + userId);
        if (jsonStr==null){
            setUserCache(userId);
        }
        jsonStr = redisUtils.get(CacheConstant.USER.USER_LOGIN_STATE + userId);
        if ("empty_user".equals(jsonStr)){
         throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        return JSONUtil.toBean(jsonStr, UserInfo.class);
    }

    /**
     * 删除缓存
     * @param userId 用户id
     * @return true成功 false失败
     */
    public Boolean deleteUserCache(Long userId){
        return redisUtils.delete(CacheConstant.USER.USER_LOGIN_STATE+userId);
    }
}

