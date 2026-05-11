<template>
  <n-flex vertical>
    <n-button round size="large" type="primary" @click="handleEdit">
      <template #icon>
        <n-icon><PencilOutline /></n-icon>
      </template>
      编辑信息
    </n-button>
    <n-button round size="large" type="primary" secondary @click="handleUpload">
      <template #icon>
        <n-icon><CloudUploadOutline /></n-icon>
      </template>
      上传图片
    </n-button>
  </n-flex>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { PencilOutline, CloudUploadOutline } from '@vicons/ionicons5'
import { useLoginUserStore } from '@/app/store/useLoginUserStore.ts'

const userStore = useLoginUserStore()

const router = useRouter()
const handleEdit = () => {
  router.push({ name: 'user-edit', params: { id: userStore.userInfo.id } })
}

const handleUpload = () => {
  router.push({ name: 'picture-upload' })
}
</script>
