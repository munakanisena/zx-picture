<template>
  <div style="padding: 24px">
    <n-flex vertical>
      <n-h1 style="margin: 0">收藏图片列表</n-h1>
      <!--搜索栏-->
      <n-card :bordered="false">
        <n-grid :x-gap="12" :cols="4" item-responsive responsive="screen">
          <n-gi span="4 m:2 l:1">
            <n-form-item label="图片名称" label-placement="left">
              <n-input
                v-model:value="searchParams.picName"
                placeholder="请输入图片名称"
                clearable
              />
            </n-form-item>
          </n-gi>
          <n-gi span="4 m:2 l:1">
            <n-form-item label="标签(可以输入多个)" label-placement="left">
              <n-select
                v-model:value="searchParams.tags"
                tag
                filterable
                multiple
                placeholder="请输入图片标签"
              />
            </n-form-item>
          </n-gi>
          <n-gi span="4 m:2 l:1">
            <n-form-item label="图片格式" label-placement="left">
              <n-select
                v-model:value="searchParams.originFormat"
                :options="PIC_FORMAT_OPTION"
                placeholder="请选择图片格式"
                clearable
              />
            </n-form-item>
          </n-gi>
          <n-gi span="4 m:2 l:1">
            <n-space>
              <n-button type="primary" @click="fetchCollectPictureList">
                <template #icon>
                  <n-icon :component="SearchOutline" />
                </template>
                搜索
              </n-button>
              <n-button @click="handleResetSearchParma">
                <template #icon>
                  <n-icon :component="RefreshOutline" />
                </template>
                刷新重置数据
              </n-button>
            </n-space>
          </n-gi>
        </n-grid>
      </n-card>
      <!--图片展示列表-->
      <n-grid
        v-if="collectPictureList?.length > 0"
        :x-gap="12"
        :y-gap="12"
        cols="1 s:2 m:3 l:4 xl:5"
        responsive="screen"
      >
        <n-gi span="1" v-for="pictureHomeVO in collectPictureList" :key="pictureHomeVO.id">
          <BPictureCard
            style="box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1)"
            :src="pictureHomeVO.compressUrl"
            :picture-id="pictureHomeVO.id"
            cover-height="300px"
          >
            <template #actions>
              <div style="display: flex; width: 100%; padding: 8px 12px; border-top: 1px solid #f0f0f0; align-items: center; height: 48px; box-sizing: border-box">
                <n-ellipsis style="flex: 1; min-width: 0; font-weight: 600; font-size: 14px; margin-right: 4px">
                  {{ pictureHomeVO.picName }}
                </n-ellipsis>
                <div style="display: flex; align-items: center; gap: 0; flex-shrink: 0">
                  <n-button text style="padding: 0 4px" @click="cancelCollect(pictureHomeVO)" title="取消收藏">
                    <n-icon size="18" color="#f39c12">
                      <Bookmark />
                    </n-icon>
                  </n-button>
                  <n-button text style="padding: 0 4px" @click="shareAction(pictureHomeVO)" title="分享">
                    <n-icon size="18">
                      <ShareSocialOutline />
                    </n-icon>
                  </n-button>
                </div>
              </div>
            </template>
          </BPictureCard>
        </n-gi>
      </n-grid>
      <n-card :bordered="false" v-else>
        <n-empty size="large" description="你什么也找不到" style="width: 100%; height: 100%">
          <template #extra>
            <n-button
              size="small"
              @click="
                $router.push({
                  name: 'home',
                })
              "
            >
              去收藏图片
            </n-button>
          </template>
        </n-empty>
      </n-card>
      <!--分页-->
    </n-flex>
    <div style="margin-top: 20px"></div>
    <n-flex justify="end">
      <n-pagination
        v-show="collectPictureList?.length > 0"
        :item-count="pagination.itemCount"
        size="large"
        v-model:page="pagination.page"
        :prefix="
          (paginationInfo: PaginationInfo) => {
            return '共' + paginationInfo.itemCount + '条'
          }
        "
        :on-update:page="handlePageChange"
      ></n-pagination>
    </n-flex>
    <!--分享弹窗-->
    <BPictureShare ref="pictureShareRef" :link="shareLink" />
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref, useTemplateRef } from 'vue'
import {
  PIC_FORMAT_OPTION,
  PIC_INTERACTION_STATUS_ENUM,
  PIC_INTERACTION_TYPE_ENUM,
} from '@/shared/constants/picture.ts'
import { Bookmark, RefreshOutline, SearchOutline, ShareSocialOutline } from '@vicons/ionicons5'
import type { PaginationInfo } from 'naive-ui'
import {
  getCollectPictureListUsingPost,
  likeOrCollectionUsingPost,
} from '@/services/api/pictureController.ts'
import BPictureShare from '@/modules/picture/components/BPictureShare.vue'
import BPictureCard from '@/shared/components/BPictureCard.vue'

const shareLink = ref<string>()
const message = useMessage()
const pictureShareRef = useTemplateRef('pictureShareRef')
const collectPictureList = ref<API.PictureHomeVO[]>()
const initialSearchParams: API.PictureQueryRequest = {
  current: 1,
  pageSize: 10,
  picName: null,
  tags: null,
  originFormat: null,
}

const searchParams = reactive<API.PictureQueryRequest>({ ...initialSearchParams })

const pagination = reactive({
  page: 1,
  itemCount: 0,
})

const fetchCollectPictureList = async () => {
  searchParams.current = pagination.page
  const { data } = await getCollectPictureListUsingPost(searchParams)
  collectPictureList.value = data?.records || []
  pagination.itemCount = data?.total || 0
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchCollectPictureList()
}

const cancelCollect = async (pictureHomeVO: API.PictureHomeVO) => {
  await likeOrCollectionUsingPost({
    id: pictureHomeVO.id as number,
    interactionType: PIC_INTERACTION_TYPE_ENUM.COLLECT,
    interactionStatus: PIC_INTERACTION_STATUS_ENUM.NOT_INTERACTED,
  })
  message.success('取消收藏成功')
  fetchCollectPictureList()
}

const shareAction = (pictureHomeVO: API.PictureHomeVO) => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/detail/${pictureHomeVO.id}`
  pictureShareRef.value?.openModal()
}

const handleResetSearchParma = () => {
  Object.assign(searchParams, initialSearchParams)
  pagination.page = 1
  fetchCollectPictureList()
}

onMounted(() => {
  fetchCollectPictureList()
})
</script>
<style scoped></style>
