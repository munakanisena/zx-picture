<template>
  <masonry-wall
    :items="pictureList"
    :ssr-columns="1"
    :column-width="300"
    :gap="16"
    :key-mapper="(item) => item.id"
    :scroll-container="props.scrollContainer"
  >
    <template #default="{ item }">
      <BPictureCard
        style="box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1)"
        hoverable
        :src="item.compressUrl"
        :picture-id="item.id"
      >
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center; width: 100%">
            <n-ellipsis style="font-weight: 600; font-size: 15px; max-width: 175px">
              {{ item.picName }}
            </n-ellipsis>
            <n-flex vertical align="center" :size="6">
              <n-avatar round :size="40" :src="item.userAvatar" />
              <span style="font-size: 14px; color: #555">{{ item.userName }}</span>
            </n-flex>
          </div>
        </template>
        <template #actions>
          <div style="display: flex; width: 100%; padding: 4px 0; border-top: 1px solid #f0f0f0">
            <n-button @click="clickLike(item)" text style="flex: 1; height: 44px; font-size: 14px">
              <n-icon size="20" style="margin-right: 4px">
                <HeartOutline v-if="!item.isLike" />
                <HeartSharp v-else style="color: #e74c3c" />
              </n-icon>
              {{ item.likeQuantity || '' }}
            </n-button>
            <n-button @click="clickCollect(item)" text style="flex: 1; height: 44px; font-size: 14px">
              <n-icon size="20" style="margin-right: 4px">
                <BookmarkOutline v-if="!item.isCollect" />
                <Bookmark v-else style="color: #f39c12" />
              </n-icon>
              {{ item.collectQuantity || '' }}
            </n-button>
            <n-button @click="shareAction(item)" text style="flex: 1; height: 44px">
              <n-icon size="20" style="margin-right: 2px">
                <ShareSocialOutline />
              </n-icon>
              <span style="font-size: 12px">分享</span>
            </n-button>
            <n-button
              @click="
                router.push({
                  name: 'picture-search-by-picture',
                  query: { id: item.id, url: item.compressUrl },
                })
              "
              text
              style="flex: 1; height: 44px"
            >
              <n-icon size="20" style="margin-right: 2px">
                <SearchOutline />
              </n-icon>
              <span style="font-size: 12px">搜图</span>
            </n-button>
          </div>
        </template>
      </BPictureCard>
    </template>
  </masonry-wall>
  <!--图片分享弹窗-->
  <BPictureShare ref="pictureShareRef" :link="shareLink" />
</template>

<script setup lang="ts">
import { computed, onUnmounted, ref, useTemplateRef } from 'vue'
import { useMessage } from 'naive-ui'
import MasonryWall from '@yeger/vue-masonry-wall'
import BPictureCard from '@/shared/components/BPictureCard.vue'
import {
  Bookmark,
  BookmarkOutline,
  HeartOutline,
  HeartSharp,
  SearchOutline,
  ShareSocialOutline,
} from '@vicons/ionicons5'
import { likeOrCollectionUsingPost } from '@/services/api/pictureController.ts'
import { useLoginUserStore } from '@/app/store/useLoginUserStore.ts'
import { PIC_INTERACTION_STATUS_ENUM, PIC_INTERACTION_TYPE_ENUM } from '@/shared/constants/picture.ts'
import BPictureShare from '@/modules/picture/components/BPictureShare.vue'
import { useRouter } from 'vue-router'

const message = useMessage()

interface Props {
  pictureList?: API.PictureHomeVO[]
  scrollContainer?: HTMLElement | null
}

const props = withDefaults(defineProps<Props>(), {
  pictureList: () => [],
  scrollContainer: null,
})

const pictureList = computed(() => props.pictureList || [])
const shareLink = ref<string>()
const pictureShareRef = useTemplateRef('pictureShareRef')
const router = useRouter()

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
  const isLogin = useLoginUserStore().checkLogin()
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
  const isLogin = useLoginUserStore().checkLogin()
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

//分享
const shareAction = (pictureHomeVO: API.PictureHomeVO) => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/detail/${pictureHomeVO.id}`
  pictureShareRef.value?.openModal()
}
</script>

<style scoped></style>
