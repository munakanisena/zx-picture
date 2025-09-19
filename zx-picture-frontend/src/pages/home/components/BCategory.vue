<template>
  <n-tabs
    ref="tabsInstRef"
    v-model:value="selectedCategoryId"
    default-value="all"
    justify-content="space-evenly"
    type="line"
    @update:value="doHomeSearch"
    size="large"
  >
    <n-tab name="all" tab="全部图片" />
    <n-tab
      v-for="category in categoryList"
      :key="category.id"
      :name="category.id"
      :tab="category.name"
    />
  </n-tabs>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref } from 'vue'
import { listHomeCategoriesUsingGet } from '@/api/homeController.ts'
import type { TabsInst } from 'naive-ui'

const emit = defineEmits(['categoryChange'])
const tabsInstRef = ref<TabsInst>()
const selectedCategoryId = ref<number>()

const categoryList = ref<API.CategoryVO[]>()
const doHomeSearch = (key: string | number) => {
  if (key === 'all') {
    emit('categoryChange', null)
  } else {
    emit('categoryChange', key as number)
  }
}

onMounted(async () => {
  const { data } = await listHomeCategoriesUsingGet()
  categoryList.value = data
  await nextTick()
  tabsInstRef.value?.syncBarPosition()
})
</script>

<style scoped></style>
