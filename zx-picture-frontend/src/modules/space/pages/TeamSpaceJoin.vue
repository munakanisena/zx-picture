<template>
  <div style="padding: 12px; height: 100%">
    <n-space vertical :size="12">
      <n-card
        title="🚀 我加入的团队空间"
        :bordered="false"
        :style="{ borderRadius: '8px', height: '100%' }"
      >
        <n-list v-if="spaceTeamList?.length > 0" hoverable clickable bordered>
          <n-list-item v-for="team in spaceTeamList" :key="team.id">
            <n-thing
              :title="'空间名称: ' + team.spaceName"
              :description="`ID: ${team.id}`"
            ></n-thing>
            <template #suffix>
              <n-space align="center">
                <n-button
                  @click="$router.push({ name: 'space-team', query: { space_id: team.id } })"
                >
                  <template #icon>
                    <n-icon>
                      <EnterIcon />
                    </n-icon>
                  </template>
                  前往团队空间
                </n-button>
              </n-space>
            </template>
          </n-list-item>
        </n-list>
        <n-empty description="暂无数据" v-else> </n-empty>
      </n-card>
    </n-space>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getJoinTeamSpacesByLoginUserUsingGet } from '@/services/api/spaceController.ts'
import { EnterOutline as EnterIcon } from '@vicons/ionicons5'

const spaceTeamList = ref<API.SpaceTeamDetailVO[]>([])

const message = useMessage()

const fetchSpaceTeamList = async () => {
  const { data } = await getJoinTeamSpacesByLoginUserUsingGet()
  if (data && data.length > 0) {
    spaceTeamList.value = data
  } else {
    message.warning('您还没有加入团队空间')
    spaceTeamList.value = []
  }
}

onMounted(() => {
  fetchSpaceTeamList()
})
</script>

<style scoped></style>
