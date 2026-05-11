package com.katomegumi.zxpicturebackend.service;

import com.katomegumi.zxpicturebackend.common.resp.PageVO;
import com.katomegumi.zxpicturebackend.dto.picture.PictureQueryRequest;
import com.katomegumi.zxpicturebackend.vo.category.CategoryVO;
import com.katomegumi.zxpicturebackend.vo.picture.PictureHomeVO;

import java.util.List;

/**
 * @author : katolr
 * @description :
 * @createDate : 2025/5/28 下午1:19
 */
public interface HomeService {


    /**
     * 获取首页 图片列表
     *
     * @param pictureQueryRequest 首页搜索
     * @return 搜索图片列表
     */
    PageVO<PictureHomeVO> pageHomePictures(PictureQueryRequest pictureQueryRequest);


    /**
     * 获取首页 分类列表
     *
     * @return 分页列表
     */
    List<CategoryVO> listHomeCategories();


}
