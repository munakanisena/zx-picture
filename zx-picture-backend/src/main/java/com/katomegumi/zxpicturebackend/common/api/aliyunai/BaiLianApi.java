package com.katomegumi.zxpicturebackend.common.api.aliyunai;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.katomegumi.zxpicturebackend.common.api.aliyunai.model.BaiLianConfig;
import com.katomegumi.zxpicturebackend.common.api.aliyunai.model.CreateOutPaintingTaskRequest;
import com.katomegumi.zxpicturebackend.common.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.katomegumi.zxpicturebackend.common.api.aliyunai.model.GetOutPaintingTaskResponse;
import com.katomegumi.zxpicturebackend.common.exception.BusinessException;
import com.katomegumi.zxpicturebackend.common.exception.ErrorCode;
import com.katomegumi.zxpicturebackend.common.exception.ThrowUtils;
import com.katomegumi.zxpicturebackend.entity.PictureInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 参考文档: <a href="https://help.aliyun.com/zh/model-studio/image-scaling-api?spm=a2c4g.11186623.0.0.2c3b90d9rQntD9">...</a>
 *
 * @author lr
 * @description: 阿里百炼 API
 * @createDate: 2025/6/10 下午7:27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BaiLianApi {

    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024L;
    private static final int MIN_RESOLUTION = 512;
    private static final int MAX_RESOLUTION = 4096;
    private final BaiLianConfig baiLianConfig;
    private final OkHttpClient okHttpClient;

    /**
     * 创建任务
     *
     * @param createOutPaintingTaskRequest 请求参数
     * @return 任务ID
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        log.info("阿里百炼扩图任务开始参数:{}", createOutPaintingTaskRequest);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSONUtil.toJsonStr(createOutPaintingTaskRequest), mediaType);

        Request request = new Request.Builder().url(BaiLianConfig.CREATE_OUT_PAINTING_TASK_URL).header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()).header(Header.AUTHORIZATION.getValue(), baiLianConfig.getBearer()).header("X-DashScope-Async", "enable").post(requestBody).build();

        try (Response response = okHttpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                log.error("阿里百炼扩图任务失败:状态码{},信息{}", response.code(), response.message());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI扩图失败");
            }
            try (ResponseBody body = response.body()) {
                if (ObjectUtil.isNull(body)) {
                    log.debug("阿里百炼扩图任务失败:{}", response.code());
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI扩图失败返回体为空");
                }
                CreateOutPaintingTaskResponse createOutPaintingTaskResponse = JSONUtil.toBean(body.string(), CreateOutPaintingTaskResponse.class);
                log.info("{}", createOutPaintingTaskResponse);
                if (StrUtil.isNotBlank(createOutPaintingTaskResponse.getCode()) || StrUtil.isNotBlank(createOutPaintingTaskResponse.getMessage())) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI扩图失败");
                }
                return createOutPaintingTaskResponse;
            }
        } catch (IOException e) {
            log.error("创建阿里云百炼扩图任务失败:{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI扩图失败");
        }
    }

    /**
     * 查询任务
     *
     * @param taskId 任务 id
     * @return 扩图任务结果
     */
    public GetOutPaintingTaskResponse queryOutPaintingTask(String taskId) {
        if (StrUtil.isBlank(taskId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务 id 不能为空");
        }
        Request request = new Request.Builder().get().url(String.format(BaiLianConfig.GET_OUT_PAINTING_TASK_URL, taskId)).header(Header.AUTHORIZATION.getValue(), baiLianConfig.getBearer()).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("查询阿里云百炼扩图任务失败:{}", response.code());
            }
            ResponseBody body = response.body();
            if (ObjectUtil.isNull(body)) {
                log.debug("阿里百炼扩图任务失败:{}", response.code());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI扩图失败返回体为空");
            }
            GetOutPaintingTaskResponse getOutPaintingTaskResponse = JSONUtil.toBean(body.string(), GetOutPaintingTaskResponse.class);
            if (StrUtil.isNotBlank(getOutPaintingTaskResponse.getOutput().getCode()) || StrUtil.isNotBlank(getOutPaintingTaskResponse.getOutput().getMessage())) {
                log.error("AI扩图失败:{}", getOutPaintingTaskResponse.getOutput().getMessage());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI扩图失败:");
            }
            return getOutPaintingTaskResponse;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务失败");
        }
    }

    /**
     * 校验 PictureInfo 中的原图大小和分辨率
     *
     * @param pictureInfo 图片信息对象
     */
    public void validatePictureInfo(PictureInfo pictureInfo) {
        // 1. 校验原图大小
        Long originSize = pictureInfo.getOriginSize();
        ThrowUtils.throwIf(originSize == null || originSize <= 0, ErrorCode.PARAMS_ERROR, "原图大小信息缺失或无效");
        ThrowUtils.throwIf(originSize > MAX_SIZE_BYTES, ErrorCode.PARAMS_ERROR, "原图大小超过 10 MB");
        // 2. 校验原图分辨率（宽 / 高）
        Integer width = pictureInfo.getOriginWidth();
        Integer height = pictureInfo.getOriginHeight();
        ThrowUtils.throwIf(width == null || height == null || width <= 0 || height <= 0, ErrorCode.PARAMS_ERROR, "原图宽度或高度信息缺失或无效");
        ThrowUtils.throwIf(width < MIN_RESOLUTION || height < MIN_RESOLUTION, ErrorCode.PARAMS_ERROR, "原图分辨率过低");
        ThrowUtils.throwIf(width > MAX_RESOLUTION || height > MAX_RESOLUTION, ErrorCode.PARAMS_ERROR, "原图分辨率过高");
    }
}
