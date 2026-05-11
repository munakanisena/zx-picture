<template>
  <n-grid
    style="width: 90%; margin: 0 auto; padding: 24px"
    x-gap="24"
    y-gap="12"
    cols="1 l:24"
    responsive="screen"
    item-responsive
  >
    <!--图片展示-->
    <n-gi span="1 l:16">
      <BPictureCard
        :src="pictureDetail?.compressUrl"
        :picture-id="pictureDetail?.id"
        cover-height="600px"
        object-fit="contain"
        :preview-disabled="false"
      >
        <template #actions>
          <div style="display: flex; width: 100%; padding: 4px 0; border-top: 1px solid #f0f0f0">
            <n-button @click="clickLike(pictureDetail)" text style="flex: 1; height: 44px; font-size: 14px">
              <n-icon size="20" style="margin-right: 4px">
                <HeartOutline v-if="!pictureDetail?.isLike" />
                <HeartSharp v-else style="color: #e74c3c" />
              </n-icon>
              {{ pictureDetail?.likeQuantity || '' }}
            </n-button>
            <n-button @click="clickCollect(pictureDetail)" text style="flex: 1; height: 44px; font-size: 14px">
              <n-icon size="20" style="margin-right: 4px">
                <BookmarkOutline v-if="!pictureDetail?.isCollect" />
                <Bookmark v-else style="color: #f39c12" />
              </n-icon>
              {{ pictureDetail?.collectQuantity || '' }}
            </n-button>
            <n-button @click="clickDownload" text style="flex: 1; height: 44px">
              <n-icon size="20" style="margin-right: 2px">
                <CloudDownloadOutline />
              </n-icon>
              <span style="font-size: 12px">下载</span>
            </n-button>
            <n-button @click="clickShare(pictureDetail)" text style="flex: 1; height: 44px">
              <n-icon size="20" style="margin-right: 2px">
                <ShareSocialOutline />
              </n-icon>
              <span style="font-size: 12px">分享</span>
            </n-button>
          </div>
        </template>
      </BPictureCard>
    </n-gi>
    <!--图片详情组件-->
    <n-gi span="1 l:8">
      <n-flex vertical style="height: 100%">
        <BAuthorInfo :pictureDetail="pictureDetail" />
        <BPictureInfo v-if="!!pictureDetail" :pictureDetail="pictureDetail" :show-more="true" style="flex: 1; min-height: 0" content-style="height: 100%" />
      </n-flex>
    </n-gi>
  </n-grid>
  <BPictureShare ref="pictureShareRef" :link="shareLink" />
</template>
<script setup lang="ts">
import { onMounted, onUnmounted, ref, useTemplateRef } from 'vue'
import {
  getPictureDetailByIdUsingGet,
  likeOrCollectionUsingPost,
  pictureDownloadUsingPost,
} from '@/services/api/pictureController.ts'
import BPictureInfo from '@/modules/picture/components/BPictureInfo.vue'
import BAuthorInfo from '@/modules/picture/components/BAuthorInfo.vue'
import BPictureCard from '@/shared/components/BPictureCard.vue'
import {
  Bookmark,
  BookmarkOutline,
  CloudDownloadOutline,
  HeartOutline,
  HeartSharp,
  ShareSocialOutline,
} from '@vicons/ionicons5'
import { useLoginUserStore } from '@/app/store/useLoginUserStore.ts'
import { PIC_INTERACTION_STATUS_ENUM, PIC_INTERACTION_TYPE_ENUM } from '@/shared/constants/picture.ts'
import BPictureShare from '@/modules/picture/components/BPictureShare.vue'
import { downloadImage } from '@/shared/utils/util.ts'

const shareLink = ref<string>()
const pictureDetail = ref<API.PictureDetailVO>()
const { pictureId } = defineProps<{ pictureId: string }>()
const message = useMessage()
const loading = ref<boolean>(false)
const pictureShareRef = useTemplateRef('pictureShareRef')
const loginStore = useLoginUserStore()

const fetchPictureDetail = async () => {
  const { data } = await getPictureDetailByIdUsingGet({ pictureId: pictureId })
  pictureDetail.value = data
}

onMounted(() => {
  fetchPictureDetail()
})

//状态维护 避免多次点击
const actioLike = ref(true)
const actioLCollect = ref(true)
let likeTimer: ReturnType<typeof setTimeout> | null = null
let collectTimer: ReturnType<typeof setTimeout> | null = null

onUnmounted(() => {
  if (likeTimer) clearTimeout(likeTimer)
  if (collectTimer) clearTimeout(collectTimer)
})

//点赞
const clickLike = async (pictureHomeVO: API.PictureHomeVO) => {
  const isLogin = loginStore.checkLogin()
  if (!isLogin) {
    return
  }

  if (!actioLike.value) {
    message.warning('点太快啦！')
    return
  }

  if (pictureHomeVO.isLike) {
    pictureHomeVO.likeQuantity = (pictureHomeVO.likeQuantity || 0) - 1
  } else {
    pictureHomeVO.likeQuantity = (pictureHomeVO.likeQuantity || 0) + 1
  }
  await likeOrCollectionUsingPost({
    id: pictureHomeVO.id as number,
    interactionType: PIC_INTERACTION_TYPE_ENUM.LIKE,
    interactionStatus: pictureHomeVO.isLike
      ? PIC_INTERACTION_STATUS_ENUM.NOT_INTERACTED
      : PIC_INTERACTION_STATUS_ENUM.INTERACTED,
  })
  pictureHomeVO.isLike = !pictureHomeVO.isLike
  message.success(`${pictureHomeVO.isLike ? '点赞成功！' : '取消点赞！'}`)

  actioLike.value = false
  if (likeTimer) clearTimeout(likeTimer)
  likeTimer = setTimeout(() => {
    actioLike.value = true
  }, 1000)
}

//收藏
const clickCollect = async (pictureHomeVO: API.PictureHomeVO) => {
  const isLogin = loginStore.checkLogin()
  if (!isLogin) {
    return
  }

  if (!actioLCollect.value) {
    message.warning('点太快啦！')
    return
  }

  if (pictureHomeVO.isCollect) {
    pictureHomeVO.collectQuantity = (pictureHomeVO.collectQuantity || 0) - 1
  } else {
    pictureHomeVO.collectQuantity = (pictureHomeVO.collectQuantity || 0) + 1
  }

  await likeOrCollectionUsingPost({
    id: pictureHomeVO.id as number,
    interactionType: PIC_INTERACTION_TYPE_ENUM.COLLECT,
    interactionStatus: pictureHomeVO.isCollect
      ? PIC_INTERACTION_STATUS_ENUM.NOT_INTERACTED
      : PIC_INTERACTION_STATUS_ENUM.INTERACTED,
  })
  pictureHomeVO.isCollect = !pictureHomeVO.isCollect
  message.success(`${pictureHomeVO.isCollect ? '收藏成功！' : '取消收藏！'}`)

  actioLCollect.value = false
  if (collectTimer) clearTimeout(collectTimer)
  collectTimer = setTimeout(() => {
    actioLCollect.value = true
  }, 1000)
}

//下载原图片
const clickDownload = async () => {
  const isLogin = loginStore.checkLogin()
  if (!isLogin) {
    return
  }
  loading.value = true
  const { data } = await pictureDownloadUsingPost({ id: pictureDetail.value?.id })
  await downloadImage(data, pictureDetail.value?.picName ?? 'download')
  loading.value = false
}

//分享
const clickShare = (pictureHomeVO: API.PictureHomeVO) => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/detail/${pictureHomeVO.id}`
  pictureShareRef.value?.openModal()
}
</script>
<style scoped></style>
