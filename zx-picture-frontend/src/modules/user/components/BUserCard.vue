<template>
  <n-card style="box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); border-radius: 16px; height: 100%">
    <template #header>
      <n-flex vertical justify="center" align="center" :size="12">
        <n-avatar :src="userInfo.avatar" :size="128" round />
        <n-h4 style="font-size: 24px; font-weight: 700; margin: 0">{{ userInfo.name }}</n-h4>
        <n-tag :type="userInfo.role === 'admin' ? 'error' : 'success'">
          {{ USER_ROLE_MAP[userInfo.role] }}
        </n-tag>
      </n-flex>
    </template>
    <n-flex vertical :size="12">
      <n-flex align="center" :size="6">
        <n-icon size="16" :component="JoinIcon" />
        <span>加入于 {{ formatDistanceToNow(userInfo.createTime as string) }}</span>
      </n-flex>
      <n-flex align="center" :size="6">
        <n-icon size="16" :component="IntroduceIcon" />
        <span>{{ userInfo.introduction || '你还没有设置签名哟' }}</span>
      </n-flex>
    </n-flex>
    <n-divider />
    <BUserButton />
  </n-card>
</template>
<script setup lang="ts">
import { useLoginUserStore } from '@/app/store/useLoginUserStore.ts'
import {
  CalendarClearOutline as JoinIcon,
  DocumentTextOutline as IntroduceIcon,
} from '@vicons/ionicons5'
import BUserButton from '@/modules/user/components/BUserButton.vue'
import { formatDistanceToNow } from '@/shared/utils/formatDistanceToNow.ts'
import { USER_ROLE_MAP } from '@/shared/constants/user.ts'

const userInfo = useLoginUserStore().userInfo
</script>

<style scoped></style>
