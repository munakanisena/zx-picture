<template>
  <n-card title="图片大小分布分析">
    <v-chart :option="sizeBarOption" autoresize class="chart-box"></v-chart>
  </n-card>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TitleComponent,
  TooltipComponent,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { analyzeSpaceSizeUsingPost } from '@/services/api/spaceAnalyzeController.ts'

use([BarChart, TitleComponent, TooltipComponent, GridComponent, LegendComponent, CanvasRenderer])

interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: string
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

const chartData = ref<API.SpaceSizeAnalyzeResponse[]>()
const loading = ref(false)
const fetchCharData = async () => {
  loading.value = true
  const { data } = await analyzeSpaceSizeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId as any,
  })
  chartData.value = data
  loading.value = false
}
watch(chartData, (newCharData) => {
  updateChartOptions(newCharData as API.SpaceSizeAnalyzeResponse[])
})

onMounted(() => {
  fetchCharData()
})

const sizeBarOption = ref({})

const updateChartOptions = (sizeList: API.SpaceSizeAnalyzeResponse[]) => {
  if (!sizeList || sizeList.length === 0) return

  const sizeRanges = sizeList.map((item) => item.sizeRange)
  const counts = sizeList.map((item) => item.count)

  sizeBarOption.value = {
    title: {
      text: '图片大小分布',
      left: 'center',
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      },
      formatter: '{b}: {c} 张',
    },
    xAxis: {
      type: 'category',
      data: sizeRanges,
      axisLabel: {
        rotate: 45,
        interval: 0,
      },
    },
    yAxis: {
      type: 'value',
      name: '图片数量 (张)',
      axisLabel: {
        formatter: '{value} 张',
      },
      minInterval: 1,
    },
    grid: {
      left: '3%',
      right: '4%',
      top: '10%',
      bottom: '15%',
      containLabel: true,
    },
    series: [
      {
        name: '图片数量',
        type: 'bar',
        data: counts,
        itemStyle: {
          color: '#FAC858',
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}',
        },
      },
    ],
  }
}
</script>

<style scoped>
.chart-box {
  width: 100%;
  height: 380px;
}
</style>
