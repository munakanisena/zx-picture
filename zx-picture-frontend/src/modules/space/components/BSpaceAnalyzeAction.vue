<template>
  <n-card title="用户上传趋势">
    <n-space align="center" justify="end" style="margin-bottom: 12px">
      <n-text depth="3">时间维度</n-text>
      <n-select
        v-model:value="timeDimension"
        :options="timeDimensionOptions"
        style="width: 100px"
        @update:value="fetchCharData"
      />
      <template v-if="userInfo.role === USER_ROLE_ENUM.ADMIN">
        <n-input
          v-model:value="userId"
          placeholder="用户ID"
          style="width: 140px"
        />
        <n-button type="primary" ghost @click="fetchCharData">搜索</n-button>
      </template>
    </n-space>
    <v-chart :option="uploadActionOption" :loading="loading" autoresize class="chart-box" />
  </n-card>
</template>

<script setup lang="ts">
import VChart from 'vue-echarts'
import { computed, onMounted, ref } from 'vue'
import { timeDimensionOptions } from '@/shared/constants/space.ts'
import { analyzeSpaceUserActionUsingPost } from '@/services/api/spaceAnalyzeController.ts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { useLoginUserStore } from '@/app/store/useLoginUserStore.ts'
import { USER_ROLE_ENUM } from '@/shared/constants/user.ts'

use([TooltipComponent, GridComponent, LineChart, CanvasRenderer])
const timeDimension = ref<string>('day')
const loading = ref(false)
const userInfo = useLoginUserStore().userInfo
const userId = ref(userInfo.id)

const fetchCharData = async () => {
  loading.value = true
  const { data } = await analyzeSpaceUserActionUsingPost({
    timeDimension: timeDimension.value,
    userId: userId.value,
  })
  loading.value = false
  if (!data || data.length === 0) {
    uploadActionOption.value = {
      title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 20 } },
    }
    return
  }
  uploadActionOption.value = buildOption(data)
}

const chartTitle = computed(() => {
  switch (timeDimension.value) {
    case 'day': return '每日上传数量'
    case 'week': return '每周上传数量'
    case 'month': return '每月上传数量'
    default: return '上传数量趋势'
  }
})

const uploadActionOption = ref()

const buildOption = (list: API.SpaceUserAnalyzeResponse[]) => {
  return {
    title: { text: chartTitle.value, left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: list.map((item) => item.period), name: '时间区间' },
    yAxis: { type: 'value', name: '上传数量' },
    series: [{
      name: '上传数量',
      type: 'line',
      data: list.map((item) => item.count),
      smooth: true,
      emphasis: { focus: 'series' },
    }],
  }
}

onMounted(() => {
  fetchCharData()
})
</script>

<style scoped>
.chart-box {
  width: 100%;
  height: 420px;
}
</style>
