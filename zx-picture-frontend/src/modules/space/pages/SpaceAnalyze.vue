<template>
  <div style="padding: 24px">
    <n-space vertical :size="20">
      <BSpaceAnalyzeUsage :space-id="spaceId" :query-all="!!queryAll" :query-public="!!queryPublic" />
      <BSpaceAnalyzeCategory :space-id="spaceId" :query-all="!!queryAll" :query-public="!!queryPublic" />
      <n-grid :x-gap="20" cols="1 m:2" responsive="screen">
        <n-gi>
          <BSpaceAnalyzeTag :space-id="spaceId" :query-all="!!queryAll" :query-public="!!queryPublic" />
        </n-gi>
        <n-gi>
          <BSpaceAnalyzeSize :space-id="spaceId" :queryAll="!!queryAll" :query-public="!!queryPublic" />
        </n-gi>
      </n-grid>
      <BSpaceAnalyzeAction />
      <BSpaceAnalyzeRank v-if="isAdmin" />
    </n-space>
  </div>
</template>
<script setup lang="ts">
import BSpaceAnalyzeUsage from '@/modules/space/components/BSpaceAnalyzeUsage.vue'
import BSpaceAnalyzeCategory from '@/modules/space/components/BSpaceAnalyzeCategory.vue'
import BSpaceAnalyzeSize from '@/modules/space/components/BSpaceAnalyzeSize.vue'
import BSpaceAnalyzeTag from '@/modules/space/components/BSpaceAnalyzeTag.vue'
import BSpaceAnalyzeRank from '@/modules/space/components/BSpaceAnalyzeRank.vue'
import BSpaceAnalyzeAction from '@/modules/space/components/BSpaceAnalyzeAction.vue'
import { computed } from 'vue'
import { useLoginUserStore } from '@/app/store/useLoginUserStore'
import { USER_ROLE_ENUM } from '@/shared/constants/user.ts'

const { spaceId, queryAll, queryPublic } = defineProps<{
  spaceId?: string
  queryAll?: number
  queryPublic?: number
}>()

const isAdmin = computed(() => {
  return useLoginUserStore().userInfo.role === USER_ROLE_ENUM.ADMIN
})
</script>
<style scoped></style>
