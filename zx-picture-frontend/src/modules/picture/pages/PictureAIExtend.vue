<template>
  <div style="width: 90%; max-width: 800px; margin: 0 auto; padding: 24px">
    <n-flex align="center" justify="space-between" style="margin-bottom: 20px">
      <n-h1 style="margin: 0">AI 扩图</n-h1>
      <n-button @click="$router.back()">
        <template #icon>
          <n-icon><ArrowBackOutline /></n-icon>
        </template>
        返回
      </n-button>
    </n-flex>

    <n-card title="图片要求" style="margin-bottom: 16px">
      <n-text depth="2">格式：JPG、JPEG、PNG、HEIF、WEBP</n-text>
      <br />
      <n-text depth="2">大小：不超过 10MB</n-text>
      <br />
      <n-text depth="2">分辨率：不低于 512×512 像素且不超过 4096×4096 像素，单边 [512, 4096] 像素</n-text>
    </n-card>

    <!-- 扩图结果 -->
    <template v-if="hasTask">
      <n-spin :show="show" description="扩图任务执行中，请耐心等待不要刷新页面...">
        <div style="min-height: 400px; display: flex; align-items: center; justify-content: center">
          <img
            v-if="expandPicUrl"
            :src="expandPicUrl"
            alt="扩图结果"
            referrerpolicy="no-referrer"
            style="max-width: 100%; max-height: 70vh; object-fit: contain"
          />
          <n-flex v-else vertical align="center" justify="center" style="height: 400px">
            <n-icon size="64" depth="3">
              <ImageOutline />
            </n-icon>
            <n-text depth="3">等待扩图结果...</n-text>
          </n-flex>
        </div>
      </n-spin>
      <div style="margin-top: 20px" />
      <n-button type="primary" block size="large" :disabled="!expandPicUrl" @click="clickDownload">
        下载结果
      </n-button>
    </template>

    <!-- 扩图参数 -->
    <n-card v-else title="扩图参数">
      <n-form label-placement="left" label-width="100">
        <n-grid :x-gap="16" :y-gap="12" cols="1 m:2" responsive="screen">
          <n-gi>
            <n-form-item label="旋转角度">
              <n-input-number v-model:value="formValue.angle" :min="0" :max="359" :step="1" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="输出宽高比">
              <n-select
                v-model:value="formValue.outputRatio"
                :options="ratioOptions"
                placeholder="不设置"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="水平扩展">
              <n-input-number
                v-model:value="formValue.xScale"
                :min="1.0"
                :max="3.0"
                :step="0.1"
                :disabled="hasOutputRatio"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="垂直扩展">
              <n-input-number
                v-model:value="formValue.yScale"
                :min="1.0"
                :max="3.0"
                :step="0.1"
                :disabled="hasOutputRatio"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="向上扩展">
              <n-input-number v-model:value="formValue.topOffset" :min="0" :step="1" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="向下扩展">
              <n-input-number v-model:value="formValue.bottomOffset" :min="0" :step="1" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="向左扩展">
              <n-input-number v-model:value="formValue.leftOffset" :min="0" :step="1" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="向右扩展">
              <n-input-number v-model:value="formValue.rightOffset" :min="0" :step="1" />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-divider />
        <n-grid :x-gap="16" :y-gap="12" cols="1 m:2" responsive="screen">
          <n-gi>
            <n-form-item label="最佳质量">
              <n-switch v-model:value="formValue.bestQuality" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="限制输出大小">
              <n-switch v-model:value="formValue.limitImageSize" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="添加AI水印">
              <n-switch v-model:value="formValue.addWatermark" />
            </n-form-item>
          </n-gi>
        </n-grid>
      </n-form>
      <n-button
        type="primary"
        block
        size="large"
        :loading="loading"
        @click="doExpandPicture"
        style="margin-top: 8px"
      >
        开始扩图
      </n-button>
    </n-card>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import {
  createPictureExtendTaskUsingPost,
  getPictureDetailByIdUsingGet,
  queryPictureExtendTaskUsingGet,
} from '@/services/api/pictureController.ts'
import { useLoginUserStore } from '@/app/store/useLoginUserStore.ts'
import { ArrowBackOutline, ImageOutline } from '@vicons/ionicons5'
import { downloadImage, validateImageForExtend } from '@/shared/utils/util.ts'

const show = ref(false)
const loading = ref(false)
const { pictureId } = defineProps<{ pictureId: string }>()
const route = useRoute()
const message = useMessage()
const expandPicUrl = ref('')
const taskId = ref('')

const hasTask = computed(() => !!taskId.value)

const ratioOptions = [
  { label: '不设置', value: '' },
  { label: '1:1', value: '1:1' },
  { label: '3:4', value: '3:4' },
  { label: '4:3', value: '4:3' },
  { label: '9:16', value: '9:16' },
  { label: '16:9', value: '16:9' },
]

const formValue = reactive({
  angle: 0,
  outputRatio: '' as string,
  xScale: 1.0,
  yScale: 1.0,
  topOffset: 0,
  bottomOffset: 0,
  leftOffset: 0,
  rightOffset: 0,
  bestQuality: false,
  limitImageSize: true,
  addWatermark: false,
})

const hasOutputRatio = computed(() => formValue.outputRatio !== '')

let pollingTimer: ReturnType<typeof setInterval> | null = null
const pollCount = ref(0)

const MAX_POLL = 60

const clearPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
  pollCount.value = 0
}

const doExpandPicture = async () => {
  loading.value = true
  try {
    const detailRes = await getPictureDetailByIdUsingGet({ pictureId: pictureId as any })
    const detail = detailRes.data
    if (detail) {
      const check = validateImageForExtend(detail)
      if (!check.valid) {
        message.warning(check.message)
        return
      }
    }
    const hasExpandParam =
      formValue.xScale > 1.0 ||
      formValue.yScale > 1.0 ||
      formValue.outputRatio !== '' ||
      formValue.topOffset > 0 ||
      formValue.bottomOffset > 0 ||
      formValue.leftOffset > 0 ||
      formValue.rightOffset > 0
    if (!hasExpandParam) {
      message.warning('请至少设置一个有效的扩展参数')
      return
    }
    show.value = true
    const { data } = await createPictureExtendTaskUsingPost({
      pictureId: pictureId as any,
      parameters: { ...formValue },
    })
    taskId.value = data?.output?.taskId as string
    message.success('扩图任务已提交，请等待处理...')
    startPolling()
  } catch {
    show.value = false
  } finally {
    loading.value = false
  }
}

const startPolling = () => {
  if (!taskId.value) {
    message.warning('当前任务已失效')
    return
  }
  checkExpandResult()
  pollingTimer = setInterval(checkExpandResult, 5000)
}

const checkExpandResult = async () => {
  if (!taskId.value) return
  pollCount.value++
  if (pollCount.value > MAX_POLL) {
    clearPolling()
    message.error('扩图任务超时，请重试')
    return
  }
  const { data } = await queryPictureExtendTaskUsingGet({ taskId: taskId.value })
  const taskResult = data.output
  if (taskResult.taskStatus === 'SUCCEEDED') {
    message.success('扩图成功')
    expandPicUrl.value = taskResult.outputImageUrl as string
    show.value = false
    clearPolling()
  } else if (taskResult.taskStatus === 'FAILED') {
    message.error('扩图任务执行失败')
    clearPolling()
  }
}

const clickDownload = async () => {
  useLoginUserStore().checkLogin()
  if (!expandPicUrl.value) {
    message.warning('请先完成扩图任务')
    return
  }
  await downloadImage(expandPicUrl.value, '扩图结果.jpg')
}

onMounted(() => {
  const queryTaskId = route.query.taskId as string
  if (queryTaskId) {
    taskId.value = queryTaskId
    show.value = true
    startPolling()
  }
})

onUnmounted(() => {
  clearPolling()
})
</script>
<style scoped></style>
