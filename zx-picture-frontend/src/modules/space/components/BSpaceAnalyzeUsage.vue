<template>
  <n-card title="存储使用情况概览">
    <n-grid :x-gap="20" cols="1 m:2" responsive="screen">
      <n-gi>
        <v-chart :option="spaceOption" :loading="loading" autoresize class="chart-box"></v-chart>
      </n-gi>
      <n-gi>
        <v-chart :option="countOption" :loading="loading" autoresize class="chart-box"></v-chart>
      </n-gi>
    </n-grid>
  </n-card>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { GaugeChart, PieChart } from 'echarts/charts'
import { LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { analyzeSpaceUsedUsingPost } from '@/services/api/spaceAnalyzeController.ts'
import { formatSize } from '@/shared/utils/util.ts'

use([PieChart, GaugeChart, TitleComponent, TooltipComponent, LegendComponent, CanvasRenderer])

interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: string
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

const chartData = ref<API.SpaceUsageAnalyzeResponse>()
const loading = ref(false)

const fetchCharData = async () => {
  loading.value = true
  const { data } = await analyzeSpaceUsedUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId as any,
  })
  chartData.value = data
  loading.value = false
}

watch(chartData, (newCharData) => {
  updateChartOptions(newCharData as API.SpaceUsageAnalyzeResponse)
})

onMounted(() => {
  fetchCharData()
})

const spaceOption = ref({})
const countOption = ref({})

const updateChartOptions = (charData: API.SpaceUsageAnalyzeResponse) => {
  if (!charData) return

  const maxSize = parseFloat(charData.maxSize as any)
  const usedSize = parseFloat(charData.usedSize as any)
  const remainingSize = maxSize - usedSize

  spaceOption.value = {
    title: {
      text: '存储空间使用情况',
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
      formatter: function (params) {
        const name = params.name
        const value = params.value
        const percent = params.percent
        return `${name}: ${formatSize(value)} (${percent}%)`
      },
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['已使用空间', '剩余空间'],
    },
    series: [
      {
        name: '空间使用',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center',
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
            fontWeight: 'bold',
            formatter: '{d}%',
          },
        },
        labelLine: {
          show: false,
        },
        data: [
          { value: usedSize, name: '已使用空间' },
          { value: remainingSize, name: '剩余空间' },
        ],
      },
    ],
  }
  countOption.value = {
    title: {
      text: '图片数量使用比例',
      left: 'center',
    },
    tooltip: {
      formatter: '{a} <br/>{b} : {c}%',
    },
    series: [
      {
        name: '数量比例',
        type: 'gauge',
        progress: {
          show: true,
        },
        detail: {
          valueAnimation: true,
          formatter: '{value}%',
          color: 'auto',
        },
        axisLabel: {
          formatter: '{value}%',
        },
        data: [
          {
            value: charData.countUsageRatio?.toFixed(2),
            name: '使用比例',
          },
        ],
        min: 0,
        max: 100,
        axisLine: {
          lineStyle: {
            width: 10,
          },
        },
        splitLine: {
          length: 15,
          lineStyle: {
            width: 2,
            color: '#999',
          },
        },
        axisTick: {
          length: 8,
          lineStyle: {
            color: '#999',
            width: 1,
          },
        },
        anchor: {
          show: true,
          showAbove: true,
          size: 8,
          itemStyle: {
            color: '#ABA8F4',
          },
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
