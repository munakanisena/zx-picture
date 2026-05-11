<template>
  <n-modal v-model:show="show" preset="card" :title="isEdit ? '编辑分类' : '添加分类'" style="width: 480px">
    <n-form label-placement="left" label-width="90">
      <n-form-item label="分类名称">
        <n-input v-model:value="formValue.name" placeholder="请输入分类名称" />
      </n-form-item>
      <n-form-item label="父级分类">
        <n-select
          v-model:value="formValue.parentId"
          :options="parentOptions"
          placeholder="无（顶级分类）"
          clearable
        />
      </n-form-item>
      <n-form-item v-if="isEdit" label="使用次数">
        <n-input-number v-model:value="formValue.useNum" :min="0" />
      </n-form-item>
    </n-form>
    <template #footer>
      <n-space justify="end">
        <n-button @click="closeModal">取消</n-button>
        <n-button type="primary" :loading="loading" @click="handleSubmit">保存</n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useMessage } from 'naive-ui'
import { addCategoryUsingPost, updateCategoryUsingPost } from '@/services/api/categoryController'
import { categoryToOptions } from '@/shared/utils/util.ts'

const message = useMessage()
const show = ref(false)
const loading = ref(false)

const props = defineProps<{
  mode: 'add' | 'edit'
  currentCategory?: API.CategoryVO
  allCategories: API.CategoryVO[]
  refresh: () => void
}>()

const emit = defineEmits<{
  close: []
}>()

const isEdit = computed(() => props.mode === 'edit')

const parentOptions = computed(() => {
  const categories = props.allCategories || []
  if (isEdit.value && props.currentCategory?.id) {
    return categoryToOptions(categories.filter((c) => c.id !== props.currentCategory?.id))
  }
  return categoryToOptions(categories)
})

const formValue = ref<API.CategoryUpdateRequest>({})

watch(
  () => props.currentCategory,
  (newVal) => {
    if (isEdit.value && newVal) {
      formValue.value = {
        id: newVal.id,
        name: newVal.name,
        parentId: newVal.parentId,
        useNum: newVal.useNum,
      }
    } else {
      formValue.value = {}
    }
  },
  { immediate: true },
)

const openModal = () => {
  show.value = true
}

const closeModal = () => {
  show.value = false
  emit('close')
}

const handleSubmit = async () => {
  if (!formValue.value.name?.trim()) {
    message.warning('请输入分类名称')
    return
  }
  try {
    loading.value = true
    if (isEdit.value) {
      await updateCategoryUsingPost({ ...formValue.value, parentId: formValue.value.parentId ?? 0 })
      message.success('分类更新成功')
    } else {
      await addCategoryUsingPost({
        name: formValue.value.name,
        parentId: formValue.value.parentId ?? 0,
      })
      message.success('分类添加成功')
    }
    closeModal()
    props.refresh()
  } finally {
    loading.value = false
  }
}

defineExpose({ openModal })
</script>
