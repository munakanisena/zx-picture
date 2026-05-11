<template>
  <n-card
    style="
      text-align: center;
      height: 100%;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      border-radius: 16px;
    "
    title="收藏图片"
  >
    <n-empty
      style="
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
      "
      size="large"
      v-if="!collectPictureList || collectPictureList.length === 0"
      description="暂无收藏图片"
    />
    <n-grid :x-gap="12" :y-gap="12" cols="1 s:2 l:3" responsive="screen">
      <n-gi span="1" v-for="pictureVO in collectPictureList" :key="pictureVO.id">
        <BPictureCard
          style="box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1)"
          :src="pictureVO.compressUrl"
          :title="pictureVO.picName"
          :picture-id="pictureVO.id"
          cover-height="270px"
        />
      </n-gi>
    </n-grid>
  </n-card>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getCollectPictureListUsingPost } from '@/services/api/pictureController.ts'
import BPictureCard from '@/shared/components/BPictureCard.vue'

const collectPictureList = ref<API.PictureHomeVO[]>()
const fetchCollectPictureList = async () => {
  const { data } = await getCollectPictureListUsingPost({
    current: 1,
    pageSize: 3,
  })
  if (data?.records?.length == 0) return
  collectPictureList.value = data.records || []
}

onMounted(() => {
  fetchCollectPictureList()
})
</script>
<style scoped></style>
