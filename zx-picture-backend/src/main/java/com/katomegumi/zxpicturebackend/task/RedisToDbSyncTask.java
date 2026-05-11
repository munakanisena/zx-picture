package com.katomegumi.zxpicturebackend.task;

import cn.hutool.core.collection.CollUtil;
import com.katomegumi.zxpicturebackend.common.constant.CacheConstant;
import com.katomegumi.zxpicturebackend.common.util.RedisUtils;
import com.katomegumi.zxpicturebackend.entity.PictureInfo;
import com.katomegumi.zxpicturebackend.mapper.PictureInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : lr
 * @description : redis 同步到数据库任务
 * @createDate : 2025/6/3 下午8:29
 */
@Component
@Slf4j
public class RedisToDbSyncTask {

    @Resource
    private PictureInfoMapper pictureInfoMapper;

    @Resource
    private RedisUtils redisUtils;

    @Transactional
    @Scheduled(cron = "0 */30 * * * ?")
    public void syncLikesAndCollectionToDb() {

        log.info("开始执行点赞/收藏增量同步任务");
        String likeTempKey = CacheConstant.PICTURE.PICTURE_INTERACTION_LIKE_KEY_PREFIX + ":sync:" + System.currentTimeMillis();
        String collectTempKey = CacheConstant.PICTURE.PICTURE_INTERACTION_COLLECTION_KEY_PREFIX + ":sync:" + System.currentTimeMillis();
        // 重命名原 key 到临时 key（原子操作）
        //先判断存不存在
        Boolean likeRenamed = false;
        Boolean collectRenamed = false;
        if (redisUtils.hasKey(CacheConstant.PICTURE.PICTURE_INTERACTION_LIKE_KEY_PREFIX)) {
            likeRenamed = redisUtils.renameIfAbsent(CacheConstant.PICTURE.PICTURE_INTERACTION_LIKE_KEY_PREFIX, likeTempKey);
        }
        if (redisUtils.hasKey(CacheConstant.PICTURE.PICTURE_INTERACTION_COLLECTION_KEY_PREFIX)) {
            collectRenamed = redisUtils.renameIfAbsent(CacheConstant.PICTURE.PICTURE_INTERACTION_COLLECTION_KEY_PREFIX, collectTempKey);
        }
        if (Boolean.FALSE.equals(likeRenamed) && Boolean.FALSE.equals(collectRenamed)) {
            log.info("当前没有增量数据");
            return;
        }
        // 如果原 key 不存在，则临时 key 为空
        Map<String, Object> likeMap = likeRenamed ? redisUtils.opsHash().entries(likeTempKey) : Map.of();
        Map<String, Object> collectMap = collectRenamed ? redisUtils.opsHash().entries(collectTempKey) : Map.of();
        log.info("读取到点赞增量数据 {} 条，收藏增量数据 {} 条", likeMap.size(), collectMap.size());

        // 合并所有需要更新的图片ID（取并集）
        Set<Long> allPictureInfoIds = new HashSet<>();
        likeMap.keySet().forEach(key -> allPictureInfoIds.add(Long.parseLong(key)));
        collectMap.keySet().forEach(key -> allPictureInfoIds.add(Long.parseLong(key)));
        // 构建增量更新对象
        List<PictureInfo> pictureInfoList = allPictureInfoIds.stream().map(id -> {
            PictureInfo pictureInfo = new PictureInfo();
            pictureInfo.setId(id);
            // 安全获取点赞增量（处理null）
            Object likeDelta = likeMap.get(id.toString());
            pictureInfo.setLikeDelta(likeDelta != null ? Integer.parseInt(likeDelta.toString()) : 0);
            // 安全获取收藏增量（处理null）
            Object collectDelta = collectMap.get(id.toString());
            pictureInfo.setCollectDelta(collectDelta != null ? Integer.parseInt(collectDelta.toString()) : 0);
            return pictureInfo;
        }).collect(Collectors.toList());
        // 批量增量更新数据库
        if (CollUtil.isNotEmpty(pictureInfoList)) {
            pictureInfoMapper.batchIncrementLikesAndCollects(pictureInfoList);
            // 清空Redis计数（成功更新后）
            if (likeRenamed) {
                redisUtils.delete(likeTempKey);
            }
            if (collectRenamed) {
                redisUtils.delete(collectTempKey);
            }
            log.info("已清空Redis中的增量缓存");
        }

    }
}

