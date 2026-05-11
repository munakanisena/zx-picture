package com.katomegumi.zxpicturebackend.mapper;

import com.katomegumi.zxpicturebackend.entity.PictureCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lr
 * @description 针对表【picture_category(分类表)】的数据库操作Mapper
 * @createDate 2025-05-27 20:15:27
 * @Entity com.katomegumi.zxpicturebackend.entity.PictureCategory
 */
public interface PictureCategoryMapper extends BaseMapper<PictureCategory> {

    /**
     * 根据 ID 集合查询分类，并以 ID 为键构建 Map
     *
     * @param categoryIds 分类 ID 集合
     * @return Map<分类ID, 分类实体>
     */
    @MapKey("id")
    Map<Long, PictureCategory> selectMapByIds(@Param("categoryIds") Collection<Long> categoryIds);

    /**
     * 将指定分类ID关联的图片的 category_id 设置为 NULL
     * @param categoryIds 要删除的分类ID集合
     */
    void updatePictureCategoryIdToNull(@Param("ids") List<Long> categoryIds);

}




