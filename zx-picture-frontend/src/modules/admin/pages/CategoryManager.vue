<template>
  <div style="padding: 24px">
    <n-h1>分类管理</n-h1>
    <n-divider />
    <n-card :bordered="false">
      <n-flex align="center">
        <n-input
          v-model:value="searchParams.name"
          placeholder="请输入分类名称搜索"
          clearable
          style="width: 200px"
        />
        <n-select
          v-model:value="searchParams.parentId"
          :options="categoryToOptions(allCategories)"
          placeholder="父级分类"
          clearable
          style="width: 200px"
        />
        <n-button type="primary" @click="handleSearch">
          <template #icon>
            <n-icon><Search /></n-icon>
          </template>
          搜索
        </n-button>
        <n-button @click="handleResetSearchParams">
          <template #icon>
            <n-icon><Refresh /></n-icon>
          </template>
          重置
        </n-button>
        <div style="flex: 1" />
        <n-button type="success" @click="handleAdd">
          <template #icon>
            <n-icon><Add /></n-icon>
          </template>
          添加分类
        </n-button>
      </n-flex>
    </n-card>
    <div style="height: 16px" />
    <n-data-table
      remote
      :columns="columns"
      :data="categoryList"
      size="large"
      :loading="loading"
      :pagination="pagination"
      @update:page="handlePageChange"
    />
    <BCategoryUpdateModal
      v-if="showModal"
      ref="category-update-modal-ref"
      :mode="modalMode"
      :current-category="currentCategory"
      :all-categories="allCategories"
      :refresh="refreshAll"
      @close="showModal = false"
    />
  </div>
</template>

<script setup lang="ts">
import { h, nextTick, onMounted, reactive, ref, useTemplateRef } from 'vue'
import {
  type DataTableColumns,
  NButton,
  NIcon,
  NSelect,
  NSpace,
  NTag,
  useMessage,
} from 'naive-ui'
import { Add, Refresh, Search } from '@vicons/ionicons5'
import {
  getCategoryPageListAsManageUsingPost,
  deleteCategoryUsingPost,
} from '@/services/api/categoryController'
import { listHomeCategoriesUsingGet } from '@/services/api/homeController'
import { categoryToOptions } from '@/shared/utils/util.ts'
import BCategoryUpdateModal from '@/modules/admin/components/BCategoryUpdateModal.vue'
import dayjs from 'dayjs'

const message = useMessage()
const loading = ref(false)
const categoryList = ref<API.CategoryVO[]>([])
const allCategories = ref<API.CategoryVO[]>([])

const initialSearchParams: API.CategoryQueryRequest = {
  current: 1,
  pageSize: 10,
  name: undefined,
  parentId: undefined,
}

const searchParams = reactive<API.CategoryQueryRequest>({ ...initialSearchParams })

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  prefix({ itemCount }: any) {
    return `共${itemCount}条`
  },
})

const fetchCategoryList = async () => {
  loading.value = true
  const { data } = await getCategoryPageListAsManageUsingPost({
    ...searchParams,
    current: pagination.page,
    pageSize: pagination.pageSize,
  })
  if (data) {
    categoryList.value = data.records || []
    pagination.itemCount = data.total as number
  }
  loading.value = false
}

const refreshAll = async () => {
  const { data } = await listHomeCategoriesUsingGet()
  allCategories.value = data || []
  fetchCategoryList()
}

const showModal = ref(false)
const modalMode = ref<'add' | 'edit'>('add')
const currentCategory = ref<API.CategoryVO>()
const categoryUpdateModalRef = useTemplateRef('category-update-modal-ref')

const handleAdd = async () => {
  modalMode.value = 'add'
  currentCategory.value = undefined
  showModal.value = true
  await nextTick()
  categoryUpdateModalRef.value?.openModal()
}

const handleEdit = async (category: API.CategoryVO) => {
  modalMode.value = 'edit'
  currentCategory.value = category
  showModal.value = true
  await nextTick()
  categoryUpdateModalRef.value?.openModal()
}

const handleSearch = () => {
  pagination.page = 1
  fetchCategoryList()
}

const handleResetSearchParams = async () => {
  Object.assign(searchParams, initialSearchParams)
  pagination.page = 1
  await fetchCategoryList()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchCategoryList()
}

const deleteCategory = async (category: API.CategoryVO) => {
  await deleteCategoryUsingPost({ id: category.id })
  message.success('删除成功')
  await fetchCategoryList()
}

const columns: DataTableColumns<API.CategoryVO> = [
  { title: 'ID', key: 'id', width: 80 },
  {
    title: '分类名称',
    key: 'name',
    render(row) {
      return h(NTag, { type: 'primary' }, { default: () => row.name })
    },
  },
  {
    title: '父级分类ID',
    key: 'parentId',
    render(row) {
      return row.parentId ?? '-'
    },
  },
  {
    title: '使用次数',
    key: 'useNum',
    render(row) {
      return row.useNum ?? 0
    },
  },
  {
    title: '创建时间',
    key: 'createTime',
    render(row) {
      return dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss')
    },
  },
  {
    title: '操作',
    key: 'actions',
    render(row) {
      return h(NSpace, {}, {
        default: () => [
          h(NButton, { size: 'small', type: 'primary', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
          h(NButton, { size: 'small', type: 'error', onClick: () => deleteCategory(row) }, { default: () => '删除' }),
        ],
      })
    },
  },
]

onMounted(async () => {
  const { data } = await listHomeCategoriesUsingGet()
  allCategories.value = data || []
  fetchCategoryList()
})
</script>
<style scoped></style>
