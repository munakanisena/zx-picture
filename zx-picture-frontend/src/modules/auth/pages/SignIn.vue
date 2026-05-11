<template>
  <n-card>
    <n-image
      :src="logo"
      object-fit="cover"
      width="192px"
      preview-disabled
      style="display: flex; justify-content: center"
    />
    <n-tabs default-value="login" justify-content="space-evenly" type="line" size="large" animated>
      <n-tab-pane name="login" tab="登录">
          <sign-in-form />
      </n-tab-pane>
      <n-tab-pane name="register" tab="注册">
          <sign-up-form />
      </n-tab-pane>
    </n-tabs>
  </n-card>
</template>
<script lang="ts" setup>
import { useRouter } from 'vue-router'
import SignInForm from '@/modules/auth/components/SignInForm.vue'
import SignUpForm from '@/modules/auth/components/SignUpForm.vue'
import { useLoginUserStore } from '@/app/store/useLoginUserStore.ts'
import { watch } from 'vue'
import logo from '@/shared/assets/images/logo.png'

const { from } = defineProps<{ from?: string }>()
const router = useRouter()
const userStore = useLoginUserStore()

// 监听登录状态
watch(
  () => userStore.isLogin,
  () => {
    if (userStore.isLogin) {
      if (from) {
        router.replace(from)
      } else {
        router.replace('/')
      }
    }
  },
  { immediate: true },
)
</script>
