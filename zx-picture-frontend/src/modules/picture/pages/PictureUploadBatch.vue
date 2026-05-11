<template>
  <div style="width: 90%; margin: 0 auto; padding: 24px">
    <n-breadcrumb style="margin-bottom: 12px">
      <n-breadcrumb-item v-if="spaceType === '1'">
        <router-link to="/space/person" style="color: #2080f0; font-weight: 500">
          当前图片上传至个人空间: {{ spaceName }} (点击返回)
        </router-link>
      </n-breadcrumb-item>
      <n-breadcrumb-item v-else-if="spaceId">
        <router-link
          :to="{ name: 'space-team', query: { space_id: spaceId } }"
          style="color: #2080f0; font-weight: 500"
        >
          当前图片上传至团队空间: {{ spaceName }} (点击返回)
        </router-link>
      </n-breadcrumb-item>
    </n-breadcrumb>

    <n-card title="批量上传图片">
      <n-upload
        multiple
        directory-dnd
        :custom-request="handleBatchUpload"
        :on-before-upload="checkUploadImage"
      >
        <n-upload-dragger>
          <div style="margin-bottom: 12px">
            <n-icon size="48" :depth="3">
              <ArchiveOutline />
            </n-icon>
          </div>
          <n-text style="font-size: 16px">点击或者拖动文件到该区域来上传</n-text>
          <n-p depth="3" style="margin: 8px 0 0 0">请不要上传敏感数据</n-p>
        </n-upload-dragger>
      </n-upload>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ArchiveOutline } from '@vicons/ionicons5'
import { checkUploadImage } from '@/shared/utils/util.ts'
import { uploadPictureByFileToSpaceUsingPost } from '@/services/api/pictureController.ts'

const route = useRoute()
const message = useMessage()
const spaceId = ref<string>()
const spaceName = ref<string>()
const spaceType = ref<string>()

const handleBatchUpload = async ({ file, onFinish, onError }: any) => {
  try {
    await uploadPictureByFileToSpaceUsingPost(
      { spaceId: spaceId.value as any },
      {},
      file.file as File,
    )
    onFinish()
    message.success(`${file.name} 上传成功`)
  } catch {
    onError()
    message.error(`${file.name} 上传失败`)
  }
}

onMounted(() => {
  spaceId.value = (route.query.space_id as string) || undefined
  spaceName.value = (route.query.space_name as string) || undefined
  spaceType.value = (route.query.space_type as string) || undefined
})
</script>
<style scoped></style>
