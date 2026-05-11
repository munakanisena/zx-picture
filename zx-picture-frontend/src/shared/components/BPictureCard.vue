<template>
  <n-card embedded content-style="padding: 0" v-bind="$attrs">
    <!-- 封面 -->
    <template #cover>
      <slot name="cover">
        <div
          :style="{ height: coverHeight, overflow: 'hidden', cursor: 'pointer' }"
          @click="handleClick"
        >
          <n-image
            :preview-disabled="previewDisabled"
            lazy
            :src="src"
            :object-fit="objectFit"
            style="width: 100%; height: 100%"
          />
        </div>
      </slot>
    </template>
    <!-- 头部（外部自定义优先，否则使用默认标题） -->
    <template v-if="$slots.header" #header>
      <slot name="header" />
    </template>
    <template v-else-if="title" #header>
      <n-ellipsis style="font-weight: 600; font-size: 15px">
        {{ title }}
      </n-ellipsis>
    </template>
    <!-- 操作栏 -->
    <slot name="actions" />
  </n-card>
</template>
<script setup lang="ts">
import { useRouter } from 'vue-router'
const props = withDefaults(
  defineProps<{
    src?: string
    title?: string
    pictureId?: number
    coverHeight?: string
    objectFit?: 'cover' | 'contain' | 'fill' | 'none' | 'scale-down'
    previewDisabled?: boolean
  }>(),
  {
    coverHeight: '100%',
    objectFit: 'cover',
    previewDisabled: true,
  },
)

const router = useRouter()

const handleClick = () => {
  if (props.pictureId) {
    router.push({ name: 'picture-detail', params: { pictureId: props.pictureId } })
  }
}
</script>
