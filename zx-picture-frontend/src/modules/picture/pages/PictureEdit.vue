<template>
  <div style="width: 90%; margin: 0 auto; padding: 24px">
    <n-grid
      x-gap="24"
      y-gap="12"
      cols="1 l:24"
      responsive="screen"
      item-responsive
    >
      <!--上传组件-->
      <n-gi span="1 l:16" v-if="!pictureDetail">
        <BPictureUpload :space-id="undefined" @fetchPictureDetail="fetchPictureDetail" />
      </n-gi>
      <!--图片展示-->
      <n-gi span="1 l:16" v-else>
        <n-card embedded>
          <template #cover>
            <div style="height: 500px; max-height: 600px">
              <n-image
                style="width: 100%; height: 100%"
                :src="pictureDetail?.compressUrl"
                object-fit="contain"
                alt="图片预览"
              />
            </div>
          </template>
          <template #action>
            <n-button
              style="width: 100%"
              type="success"
              size="large"
              @click="imageCropper?.openModalAndSendImage(pictureDetail.compressUrl)"
            >
              裁剪图片
            </n-button>
          </template>
        </n-card>
      </n-gi>
      <!--右侧信息栏-->
      <n-gi span="1 l:8">
        <n-flex vertical :size="16">
          <n-breadcrumb v-if="!!pictureDetail">
            <n-breadcrumb-item
              v-if="useLoginUserStore().userInfo.role === USER_ROLE_ENUM.ADMIN && !spaceId"
            >
              <router-link :to="{ name: 'picture-manager' }">返回图片管理</router-link>
            </n-breadcrumb-item>
            <n-breadcrumb-item v-else>
              <router-link v-if="spaceType == 1" to="/space/person">
                {{ spaceName }}
              </router-link>
              <router-link v-else :to="{ name: 'space-team', query: { space_id: spaceId || '' } }">
                {{ spaceName }}
              </router-link>
            </n-breadcrumb-item>
          </n-breadcrumb>
          <BPictureInfo
            v-if="!!pictureDetail"
            :pictureDetail="pictureDetail"
            :show-more="false"
          />
          <BPictureEditForm v-if="!!pictureDetail" :pictureDetail="pictureDetail" />
        </n-flex>
      </n-gi>
    </n-grid>
    <!--AI 扩图独立区域-->
    <n-card v-if="!!pictureDetail" title="AI 扩图" style="margin-top: 24px">
      <n-flex align="center" justify="space-between">
        <n-text depth="3">基于当前图片使用 AI 智能扩展画面，每天限一次</n-text>
        <n-button type="primary" secondary @click="goAIExtend">
          开始扩图
        </n-button>
      </n-flex>
    </n-card>
  </div>
  <BPictureCropper
    :spaceType="spaceType"
    :picture-id="pictureDetail.id as any"
    v-if="!!pictureDetail"
    ref="picture-cropper"
    @upload="handleUploadCrop"
  />
</template>

<script setup lang="ts">
import { onMounted, ref, useTemplateRef } from 'vue'
import BPictureUpload from '@/modules/picture/components/BPictureUpload.vue'
import BPictureInfo from '@/modules/picture/components/BPictureInfo.vue'
import BPictureCropper from '@/modules/picture/components/BPictureCropper.vue'
import BPictureEditForm from '@/modules/picture/components/BPictureEditForm.vue'
import {
  getPictureDetailByIdUsingGet,
  uploadPictureByFileToPublicUsingPost,
  uploadPictureByFileToSpaceUsingPost,
} from '@/services/api/pictureController.ts'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/app/store/useLoginUserStore.ts'
import { USER_ROLE_ENUM } from '@/shared/constants/user.ts'

const { pictureId, spaceId, spaceName, spaceType } = defineProps<{
  pictureId: string
  spaceId?: string
  spaceName?: string
  spaceType?: string
}>()
const router = useRouter()
const message = useMessage()
const pictureDetail = ref<API.PictureDetailVO>()
const imageCropper = useTemplateRef('picture-cropper')
const loadingBar = useLoadingBar()

const goAIExtend = () => {
  router.push({ name: 'picture-ai-extend', params: { pictureId } })
}

const handleUploadCrop = async (file: File) => {
  loadingBar.start()
  try {
    if (spaceId) {
      const { data } = await uploadPictureByFileToSpaceUsingPost(
        {},
        { id: pictureDetail.value?.id, spaceId: spaceId },
        file,
      )
      pictureDetail.value = data as API.PictureDetailVO
    } else {
      const { data } = await uploadPictureByFileToPublicUsingPost(
        {},
        { id: pictureDetail.value?.id },
        file,
      )
      pictureDetail.value = data as API.PictureDetailVO
    }
    message.success('图片上传成功！')
  } finally {
    loadingBar.finish()
  }
}

const fetchPictureDetail = (pictureInfo: API.PictureDetailVO) => {
  pictureDetail.value = pictureInfo as API.PictureDetailVO
}

onMounted(async () => {
  const { data } = await getPictureDetailByIdUsingGet({ pictureId: pictureId })
  pictureDetail.value = data
})
</script>

<style scoped></style>
