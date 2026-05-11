<template>
  <n-card title="图片分类分析">
    <n-grid :x-gap="20" cols="1 m:2" responsive="screen">
      <n-gi>
        <v-chart :option="countBarOption" autoresize class="chart-box"></v-chart>
      </n-gi>
      <n-gi>
        <v-chart :option="sizePieOption" autoresize class="chart-box"></v-chart>
      </n-gi>
    </n-grid>
  </n-card>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TitleComponent,
  TooltipComponent,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { analyzeSpaceCategoryUsingPost } from '@/services/api/spaceAnalyzeController.ts'

use([
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  CanvasRenderer,
])

interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: string
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})
const chartData = ref<API.SpaceCategoryAnalyzeResponse[]>()
const loading = ref(false)
const fetchCharData = async () => {
  loading.value = true
  const { data } = await analyzeSpaceCategoryUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId as any,
  })
  chartData.value = data
  loading.value = false
}

watch(chartData, (newCharData) => {
  updateChartOptions(newCharData as API.SpaceCategoryAnalyzeResponse[])
})

onMounted(() => {
  fetchCharData()
})

const countBarOption = ref({})
const sizePieOption = ref({})

const formatBytes = (bytes, decimals = 2) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i]
}

const updateChartOptions = (categoryList: API.SpaceCategoryAnalyzeResponse[]) => {
  if (!categoryList || categoryList.length === 0) return

  const categoryNames = categoryList.map((item) => item.categoryName)
  const counts = categoryList.map((item) => item.count)
  const totalSizesForPie = categoryList.map((item) => ({
    value: item.totalSize,
    name: item.categoryName,
  }))

  countBarOption.value = {
    title: {
      text: '各分类图片数量',
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
      data: categoryNames,
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
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true,
    },
    series: [
      {
        name: '图片数量',
        type: 'bar',
        data: counts,
        itemStyle: {
          color: '#f39575',
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}',
        },
      },
    ],
  }

  sizePieOption.value = {
    title: {
      text: '各分类空间占用',
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
      formatter: function (params: any) {
        return `${params.name}: ${formatBytes(params.value)} (${params.percent}%)`
      },
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: categoryNames,
    },
    series: [
      {
        name: '空间占用',
        type: 'pie',
        radius: '60%',
        center: ['50%', '60%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: '{b}\n{d}%',
          fontSize: 12,
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
        labelLine: {
          show: true,
        },
        data: totalSizesForPie,
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
