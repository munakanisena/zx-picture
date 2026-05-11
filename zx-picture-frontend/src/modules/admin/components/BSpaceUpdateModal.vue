<template>
  <n-modal v-model:show="show" preset="card" title="编辑空间" style="width: 500px">
    <n-form :model="formValue">
      <n-form-item label="空间名称" prop="spaceName">
        <n-input v-model:value="formValue.spaceName" />
      </n-form-item>
      <n-form-item label="空间等级" prop="spaceLevel">
        <n-select v-model:value="formValue.spaceLevel" :options="toOptions(SPACE_LEVEL_MAP)" />
      </n-form-item>
      <n-form-item label="空间图片的最大总大小" prop="maxSize">
        <n-input-number v-model:value="formValue.maxSize" :min="0" style="width: 100%" />
      </n-form-item>
      <n-form-item label="空间图片的最大数量" prop="maxCount">
        <n-input-number v-model:value="formValue.maxCount" :min="0" style="width: 100%" />
      </n-form-item>
    </n-form>
    <template #footer>
      <n-space justify="end">
        <n-button @click="closeUpdateModal">取消</n-button>
        <n-button type="primary" @click="userUpdate" :loading="loading">保存</n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<script setup lang="ts">
import { defineExpose, ref, watch } from 'vue'
import { toOptions } from '@/shared/utils/util.ts'
import { SPACE_LEVEL_MAP } from '@/shared/constants/space.ts'
import { updateSpaceUsingPost } from '@/services/api/spaceController.ts'

const message = useMessage()
const show = ref(false)
const loading = ref(false)

const props = defineProps<{
  currentSpace: API.SpaceVO
  refresh: () => void
}>()

// 表单数据
const formValue = ref<API.SpaceUpdateRequest>({})

const userUpdate = async () => {
  try {
    loading.value = true
    await updateSpaceUsingPost({ ...formValue.value })
    message.success('空间信息更新成功')
    //刷新列表
    props.refresh()
    closeUpdateModal()
  } finally {
    loading.value = false
  }
}

watch(
  () => props.currentSpace,
  (newSpace) => {
    formValue.value = { ...newSpace }
  },
  { immediate: true },
)

const openUpdateModal = () => {
  show.value = true
}

const closeUpdateModal = () => {
  show.value = false
}

//可以将函数暴露给父组件
defineExpose({
  openUpdateModal,
})
</script>
