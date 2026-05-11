<template>
  <n-grid
    x-gap="12"
    responsive="screen"
    y-gap="12"
    style="height: 100%; margin: 0 auto"
    item-responsive
  >
    <n-gi span="24 l:8 " style="margin: 12px">
      <BUserCard style="height: 100%" />
    </n-gi>
    <n-gi span="24 l:16" style="padding: 12px">
      <n-flex align="center" style="height: 100%" :vertical="true">
        <BUserStats />
        <BUserActive />
      </n-flex>
    </n-gi>
  </n-grid>
</template>
<script setup lang="ts">
import BUserCard from '@/modules/user/components/BUserCard.vue'
import BUserStats from '@/modules/user/components/BUserStats.vue'

import BUserActive from '@/modules/user/components/BUserActive.vue'
</script>
<style scoped></style>
