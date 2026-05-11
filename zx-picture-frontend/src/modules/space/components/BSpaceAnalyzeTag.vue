<template>
  <n-card title="图片标签分析">
    <v-chart :option="tagWordCloudOption" :loading="loading" autoresize class="chart-box"></v-chart>
  </n-card>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { TitleComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { analyzeSpaceTagsUsingPost } from '@/services/api/spaceAnalyzeController.ts'
import 'echarts-wordcloud'

use([TitleComponent, TooltipComponent, CanvasRenderer])

interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: string
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

const chartData = ref<API.SpaceTagAnalyzeResponse[]>()
const loading = ref(false)
const fetchCharData = async () => {
  loading.value = true
  const { data } = await analyzeSpaceTagsUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId as any,
  })
  chartData.value = data
  loading.value = false
}
watch(chartData, (newCharData) => {
  updateChartOptions(newCharData as API.SpaceTagAnalyzeResponse[])
})

onMounted(() => {
  fetchCharData()
})

const tagWordCloudOption = ref({})

const updateChartOptions = (tagList: API.SpaceTagAnalyzeResponse[]) => {
  if (!tagList || tagList.length === 0) return

  const tagData = tagList.map((item) => ({
    name: item.tagName,
    value: item.count,
  }))

  tagWordCloudOption.value = {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => `${params.name}: ${params.value} 次`,
    },
    series: [
      {
        type: 'wordCloud',
        gridSize: 10,
        sizeRange: [14, 50],
        rotationRange: [-90, 90],
        shape: 'circle',
        textStyle: {
          color: () =>
            `rgb(${Math.round(Math.random() * 255)}, ${Math.round(Math.random() * 255)}, ${Math.round(Math.random() * 255)})`,
        },
        data: tagData,
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
