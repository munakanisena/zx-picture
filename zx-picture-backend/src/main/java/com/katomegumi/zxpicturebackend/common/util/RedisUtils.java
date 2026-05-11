package com.katomegumi.zxpicturebackend.common.util;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author : lr
 * @description : 缓存操作类
 * @createDate : 2026/3/15 下午3:19
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 删除单个缓存
     *
     * @param key 键
     * @return Boolean【true 删除成功，false 删除失败】
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除多个缓存
     *
     * @param keys 键集合
     * @return Long【删除键的个数】
     */
    public Long deletes(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 判断缓存是否存在
     *
     * @param key 键
     * @return Boolean【true 存在，false 不存在】
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置缓存过期时间
     *
     * @param key 键
     * @param et  失效时间，单位毫秒
     * @return Boolean【true 设置成功，false 设置失败】
     */
    public Boolean setTime(String key, long et) {
        return redisTemplate.expire(key, et, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置缓存过期时间
     *
     * @param key  键
     * @param et   失效时间
     * @param unit 时间单位
     * @return Boolean【true 设置成功，false 设置失败】
     */
    public Boolean setTime(String key, long et, TimeUnit unit) {
        return redisTemplate.expire(key, et, unit);
    }

    /**
     * 获取缓存过期时间
     *
     * @param key 键
     * @return Long【过期时间，单位毫秒】
     */
    public Long getTime(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取缓存过期时间
     *
     * @param key  键
     * @param unit 时间单位
     * @return Long【过期时间，单位 unit】
     */
    public Long getTime(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 删除缓存过期时间
     *
     * @param key 键
     * @return Boolean【true 删除成功，false 删除失败】
     */
    public Boolean delTime(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 获取所有键
     *
     * @return Set<String>【慎用】
     */
    public Set<String> getKeys() {
        return redisTemplate.keys("*");
    }

    /**
     * 获取所有键
     *
     * @param prefix 键前缀
     * @return Set<String>【键集合】
     */
    public Set<String> getKeys(String prefix) {
        return redisTemplate.keys(prefix + "*");
    }

    /**
     * 获取随机一个键
     *
     * @return String【随机键】
     */
    public String getRandomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * 修改键的名称
     *
     * @param oldKey 旧键
     * @param newKey 新键
     */
    public void renameKey(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 修改键的名称
     *
     * @param oldKey 旧键
     * @param newKey 新键
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }
    /**
     * 获取键的类型
     *
     * @param key 键
     * @return DataType【类型对象】
     */
    public DataType getType(String key) {
        return redisTemplate.type(key);
    }

    /**
     * 获取 String 操作对象
     *
     * @return ValueOperations<String, String>【键操作对象】
     */
    public ValueOperations<String, String> opsString() {
        return opsString(null);
    }

    /**
     * 获取 String 操作对象
     *
     * @param redisTemplate 指定 redisTemplate
     * @return ValueOperations<String, String>【键操作对象】
     */
    public ValueOperations<String, String> opsString(RedisTemplate<String, String> redisTemplate) {
        if (redisTemplate == null) {
            return this.redisTemplate.opsForValue();
        } else {
            return redisTemplate.opsForValue();
        }
    }

    /**
     * 获取 Hash 操作对象
     *
     * @return HashOperations<String, Object, Object>【键操作对象】
     */
    public HashOperations<String, String, Object> opsHash() {
        return opsHash(null);
    }

    /**
     * 获取 Hash 操作对象
     *
     * @param redisTemplate 指定 redisTemplate
     * @return HashOperations<String, Object, Object>【键操作对象】
     */
    public HashOperations<String, String, Object> opsHash(RedisTemplate<String, String> redisTemplate) {
        if (redisTemplate == null) {
            return this.redisTemplate.opsForHash();
        } else {
            return redisTemplate.opsForHash();
        }
    }

    /**
     * 获取 boundHash 操作对象
     *
     * @param key 绑定的值
     * @return BoundHashOperations<String, String, String>【绑定对象】
     */
    public BoundHashOperations<String, String, String> boundHashOperations(String key) {
        return boundHashOperations(null, key);
    }

    /**
     * 获取 boundHash 操作对象
     *
     * @param redisTemplate 指定 redisTemplate
     * @param key           绑定的值
     * @return BoundHashOperations<String, String, String>【绑定对象】
     */
    public BoundHashOperations<String, String, String> boundHashOperations(RedisTemplate<String, String> redisTemplate, String key) {
        if (redisTemplate == null) {
            return this.redisTemplate.boundHashOps(key);
        } else {
            return redisTemplate.boundHashOps(key);
        }
    }

    /**
     * 获取 List 操作对象
     *
     * @return ListOperations<String, String>【键操作对象】
     */
    public ListOperations<String, String> opsList() {
        return opsList(null);
    }

    /**
     * 获取 List 操作对象
     *
     * @param redisTemplate 指定 redisTemplate
     * @return ListOperations<String, String>【键操作对象】
     */
    public ListOperations<String, String> opsList(RedisTemplate<String, String> redisTemplate) {
        if (redisTemplate == null) {
            return this.redisTemplate.opsForList();
        } else {
            return redisTemplate.opsForList();
        }
    }

    /**
     * 获取 Set 操作对象
     *
     * @return SetOperations<String, String>【键操作对象】
     */
    public SetOperations<String, String> opsSet() {
        return opsSet(null);
    }

    /**
     * 获取 Set 操作对象
     *
     * @param redisTemplate 指定 redisTemplate
     * @return SetOperations<String, String>【键操作对象】
     */
    public SetOperations<String, String> opsSet(RedisTemplate<String, String> redisTemplate) {
        if (redisTemplate == null) {
            return this.redisTemplate.opsForSet();
        } else {
            return redisTemplate.opsForSet();
        }
    }

    /**
     * 获取 boundSet 操作对象
     *
     * @param key 绑定的值
     * @return BoundSetOperations<String, String>【绑定对象】
     */
    public BoundSetOperations<String, String> boundSetOperations(String key) {
        return boundSetOperations(null, key);
    }

    /**
     * 获取 boundSet 操作对象
     *
     * @param redisTemplate 指定 redisTemplate
     * @param key           绑定的值
     * @return BoundSetOperations<String, String>【绑定对象】
     */
    public BoundSetOperations<String, String> boundSetOperations(RedisTemplate<String, String> redisTemplate, String key) {
        if (redisTemplate == null) {
            return this.redisTemplate.boundSetOps(key);
        } else {
            return redisTemplate.boundSetOps(key);
        }
    }

    /**
     * 获取 ZSet 操作对象
     *
     * @return ZSetOperations<String, String>【键操作对象】
     */
    public ZSetOperations<String, String> opsZSet() {
        return opsZSet(null);
    }

    /**
     * 获取 ZSet 操作对象
     *
     * @param redisTemplate 指定 redisTemplate
     * @return ZSetOperations<String, String>【键操作对象】
     */
    public ZSetOperations<String, String> opsZSet(RedisTemplate<String, String> redisTemplate) {
        if (redisTemplate == null) {
            return this.redisTemplate.opsForZSet();
        } else {
            return redisTemplate.opsForZSet();
        }
    }

    // region ========== String 相关操作

    /**
     * String：获取值
     *
     * @param key 键
     * @return String【值】
     */
    public String get(String key) {
        return opsString().get(key);
    }

    /**
     * String：获取值集合
     *
     * @param keys 键集合
     * @return List<String>【值集合】
     */
    public List<String> gets(Collection<String> keys) {
        return opsString().multiGet(keys);
    }

    /**
     * String：设置键值
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value) {
        opsString().set(key, value);
    }

    /**
     * String：设置键值
     *
     * @param key   键
     * @param value 值
     * @param et    失效时间，单位毫秒
     */
    public void set(String key, String value, long et) {
        set(key, value, et, TimeUnit.MILLISECONDS);
    }

    /**
     * String：设置键值
     *
     * @param key   键
     * @param value 值
     * @param et    失效时间
     * @param unit  时间单位
     */
    public void set(String key, String value, long et, TimeUnit unit) {
        opsString().set(key, value, et, unit);
    }

    /**
     * String：设置键值集合
     *
     * @param map 键值集合
     */
    public void sets(Map<String, String> map) {
        opsString().multiSet(map);
    }

    /**
     * String：设置键值，不存在才设置
     *
     * @param key   键
     * @param value 值
     * @return Boolean【true 不存在设置成功，false 存在设置失败】
     */
    public Boolean setIfHas(String key, String value) {
        return opsString().setIfAbsent(key, value);
    }

    /**
     * String：设置键值，不存在才设置
     *
     * @param key   键
     * @param value 值
     * @param et    失效时间，单位毫秒
     * @return Boolean【true 不存在设置成功，false 存在设置失败】
     */
    public Boolean setIfHas(String key, String value, long et) {
        return setIfHas(key, value, et, TimeUnit.MILLISECONDS);
    }

    /**
     * String：设置键值，不存在才设置
     *
     * @param key   键
     * @param value 值
     * @param et    失效时间
     * @param unit  时间单位
     * @return Boolean【true 不存在设置成功，false 存在设置失败】
     */
    public Boolean setIfHas(String key, String value, long et, TimeUnit unit) {
        return opsString().setIfAbsent(key, value, et, unit);
    }

    /**
     * String：设置自增减值
     *
     * @param key   键
     * @param delta 增减值，负数表示自减
     * @return Long【自增减后的值】
     */
    public Long incrBy(String key, long delta) {
        return opsString().increment(key, delta);
    }

    // endregion ========== String 相关操作

    // region ========== Hash 相关操作

    /**
     * Hash：获取值集合
     *
     * @param key 键
     * @return Map<Object, Object>【值集合】
     */
    public Map<String, Object> hGet(String key) {
        return opsHash().entries(key);
    }

    /**
     * Hash：获取内容
     *
     * @param key   键
     * @param field 字段
     * @return Object【内容】
     */
    public Object hGet(String key, String field) {
        return opsHash().get(key, field);
    }

    /**
     * Hash：获取值的数量
     *
     * @param key 键
     * @return Long【值的数量】
     */
    public Long hSize(String key) {
        return opsHash().size(key);
    }

    /**
     * Hash：设置键值
     *
     * @param key   键
     * @param field 字段
     * @param value 内容
     */
    public void hSet(String key, String field, Object value) {
        opsHash().put(key, field, value);
    }

    /**
     * Hash：设置键值集合
     *
     * @param key 键
     * @param map 值集合
     */
    public void hSets(String key, Map<String, Object> map) {
        opsHash().putAll(key, map);
    }

    /**
     * Hash：设置键值，不存在才设置
     *
     * @param key   键
     * @param field 字段
     * @param value 内容
     * @return Boolean【true 不存在设置成功，false 存在设置失败】
     */
    public Boolean setIfHas(String key, String field, Object value) {
        return opsHash().putIfAbsent(key, field, value);
    }

    /**
     * Hash：判断字段是否存在
     *
     * @param key   键
     * @param field 字段
     * @return Boolean【true 存在，false 不存在】
     */
    public Boolean hHas(String key, String field) {
        return opsHash().hasKey(key, field);
    }

    /**
     * Hash：删除字段
     *
     * @param key    键
     * @param fields 字段，一个或多个
     * @return Long【删除的数量】
     */
    public Long hDelete(String key, Object... fields) {
        return opsHash().delete(key, fields);
    }

    /**
     * Hash：设置字段的自增减值
     *
     * @param key   键
     * @param field 字段
     * @param delta 增减值，负数表示自减
     * @return Long【自增减后的值】
     */
    public Long hIncrBy(String key, String field, long delta) {
        return opsHash().increment(key, field, delta);
    }

    // endregion ========== Hash 相关操作
}

