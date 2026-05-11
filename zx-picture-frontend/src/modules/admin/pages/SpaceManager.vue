<template>
  <div style="padding: 24px">
    <n-flex align="center" justify="space-between" style="height: 100%">
      <n-h1>空间管理</n-h1>
      <n-space>
        <n-button
          secondary
          type="primary"
          @click="
              $router.push({
                name: 'analyze-analyze',
                params: {
                  spaceId: 0,
                },
                query: {
                  queryAll: 1,
                },
              })
            "
        >
          <template #icon>
            <n-icon :component="PieChartOutline"/>
          </template>
          全空间分析
        </n-button>
        <n-button
          secondary
          type="primary"
          @click="
              $router.push({
                name: 'analyze-analyze',
                params: {
                  spaceId: 0,
                },
                query: {
                  queryPublic: 1,
                },
              })
            "
        >
          <template #icon>
            <n-icon :component="PieChartOutline"/>
          </template>
          公共空间分析
        </n-button>
      </n-space>
    </n-flex>
    <n-divider/>
    <!--搜索栏-->
    <n-card :bordered="false">
      <n-grid :cols="5" :x-gap="12" item-responsive responsive="screen">
        <n-gi span="4 m:2 l:1">
          <n-form-item label="空间名称" label-placement="left">
            <n-input
              v-model:value="searchParams.spaceName"
              clearable
              placeholder="请输入空间名称"
            />
          </n-form-item>
        </n-gi>
        <n-gi span="4 m:2 l:1">
          <n-form-item label="用户ID" label-placement="left">
            <n-input v-model:value="searchParams.userId" clearable placeholder="请输入用户ID"/>
          </n-form-item>
        </n-gi>
        <n-gi span="4 m:2 l:1">
          <n-form-item label="空间类型" label-placement="left">
            <n-select
              v-model:value="searchParams.spaceType"
              :options="toOptions(SPACE_TYPE_MAP)"
              placeholder="请输入空间类型"
            />
          </n-form-item>
        </n-gi>
        <n-gi span="4 m:2 l:1">
          <n-form-item label="空间级别" label-placement="left">
            <n-select
              v-model:value="searchParams.spaceLevel"
              :options="toOptions(SPACE_LEVEL_MAP)"
              clearable
              placeholder="请选择空间级别"
            />
          </n-form-item>
        </n-gi>
        <n-gi span="4 m:2 l:1">
          <n-space>
            <n-button type="primary" @click="handleSearch">
              <template #icon>
                <n-icon :component="SearchOutline"/>
              </template>
              搜索
            </n-button>
            <n-button @click="handleResetSearchParma">
              <template #icon>
                <n-icon :component="RefreshOutline"/>
              </template>
              刷新重置数据
            </n-button>
          </n-space>
        </n-gi>
      </n-grid>
    </n-card>
    <div style="height: 16px"></div>
    <n-data-table
      :columns="columns"
      :data="spaceVOList"
      :loading="loading"
      :pagination="pagination"
      remote
      size="large"
      @update:page="handlePageChange"
    ></n-data-table>
  </div>
  <BSpaceUpdateModal
    v-if="!!currentSpace"
    ref="space-update-modal"
    :current-space="currentSpace"
    :refresh="fetchSpaceList"
  />
</template>

<script lang="ts" setup>
import {h, nextTick, onMounted, reactive, ref, useTemplateRef} from 'vue'
import {type DataTableColumns, NButton, NIcon, NInput, NSelect, NSpace, NTag, useMessage,} from 'naive-ui'
import {PieChartOutline, RefreshOutline, SearchOutline} from '@vicons/ionicons5'
import {toOptions} from '@/shared/utils/util.ts'
import {SPACE_LEVEL_MAP, SPACE_TYPE_MAP} from '@/shared/constants/space.ts'
import {deleteSpaceUsingPost, getSpacePageListAsManageUsingPost} from '@/services/api/spaceController.ts'
import BSpaceUpdateModal from '@/modules/admin/components/BSpaceUpdateModal.vue'
import {useRouter} from 'vue-router'

const message = useMessage()
const loading = ref(false)
const spaceVOList = ref<API.SpaceVO[]>()
const loadingBar = useLoadingBar()
const router = useRouter()
const spaceUpdateModal = useTemplateRef('space-update-modal')

// 定义搜索参数的初始状态
const initialSearchParams: API.SpaceQueryRequest = {
  current: 1,
  pageSize: 10,
}

// 初始化
const searchParams = reactive<API.SpaceQueryRequest>({...initialSearchParams})

// 分页配置
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  prefix({itemCount}: any) {
    return `共${itemCount}条`
  },
})

/**
 * 获取图片列表
 */
function handleSearch() {
  pagination.page = 1
  fetchSpaceList()
}

const fetchSpaceList = async () => {
  loadingBar.start()
  const {data} = await getSpacePageListAsManageUsingPost({
    ...searchParams,
    current: pagination.page,
    pageSize: pagination.pageSize,
  })
  if (!data.records?.length) return
  spaceVOList.value = data.records
  pagination.itemCount = data.total as number
  loadingBar.finish()
}

// 分页变化
function handlePageChange(page: number) {
  pagination.page = page
  fetchSpaceList()
}

//删除图片
const clickDelete = async (SpaceId: number) => {
  await deleteSpaceUsingPost({id: SpaceId})
  await fetchSpaceList()
  message.success('删除成功')
}

const currentSpace = ref<API.SpaceVO>()
//编辑空间
const editSpace = async (space: API.SpaceVO) => {
  currentSpace.value = space
  //等待组件挂载完毕
  await nextTick()
  spaceUpdateModal.value?.openUpdateModal()
}

//重置搜索条件
const handleResetSearchParma = async () => {
  Object.assign(searchParams, initialSearchParams)
  await fetchSpaceList()
}

onMounted(() => {
  fetchSpaceList()
})

// 表格列配置
const columns: DataTableColumns<API.SpaceVO> = [
  {
    title: '空间id',
    key: 'id',
  },
  {
    title: '空间名称',
    key: 'spaceName',
  },
  {
    title: '用户id',
    key: 'userId',
  },
  {
    title: '空间级别',
    key: 'spaceLevel',
    align: 'center',
    render(row) {
      return h(
        NTag,
        {
          type: 'info',
        },
        {
          default: () => {
            return SPACE_LEVEL_MAP[row.spaceLevel as number]
          },
        },
      )
    },
  },
  {
    title: '空间类型',
    key: 'spaceType',
    align: 'center',
    render(row) {
      return h(
        NTag,
        {
          type: 'info',
        },
        {
          default: () => {
            return SPACE_TYPE_MAP[row.spaceType as number]
          },
        },
      )
    },
  },
  {
    title: '操作',
    key: 'actions',
    render(row) {
      return h(
        NSpace,
        {},
        {
          default: () => [
            h(
              NButton,
              {
                size: 'small',
                type: 'info',
                secondary: true,
                onClick: () => {
                  router.push({
                    name: 'analyze-analyze',
                    params: {
                      spaceId: row?.id,
                    },
                  })
                },
              },
              {default: () => '空间分析'},
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                secondary: true,
                onClick: () => editSpace(row),
              },
              {default: () => '编辑'},
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'error',
                secondary: true,
                onClick: () => clickDelete(row.id as number),
              },
              {default: () => '删除'},
            ),
          ],
        },
      )
    },
  },
]
</script>
<style scoped></style>
