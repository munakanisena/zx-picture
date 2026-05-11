<template>
  <div style="padding: 12px">
    <n-space :size="12" vertical>
      <n-card :bordered="false" :style="{ borderRadius: '8px' }" title="🚀 团队空间详情">
        <n-descriptions :column="1" label-placement="left">
          <n-descriptions-item label="空间名称"> {{ teamInfo?.spaceName }}</n-descriptions-item>
          <n-descriptions-item label="空间详情">
            存储空间: {{ formatSize(teamInfo?.usedSize as number) }} /
            {{ formatSize(teamInfo?.maxSize as number) }}
            存储数量: {{ teamInfo?.usedCount }} / {{ teamInfo?.maxCount }} 张
          </n-descriptions-item>
          <n-descriptions-item label="创建日期">
            {{ formatDistanceToNow(teamInfo?.createTime as string) }}
          </n-descriptions-item>
        </n-descriptions>
      </n-card>

      <n-card :bordered="false" :style="{ borderRadius: '8px' }" title="👥 空间成员">
        <n-space
          v-if="loginUserMember?.spaceRole === SPACE_ROLE_ENUM.ADMIN"
          :size="12"
          style="margin-bottom: 20px; padding: 8px"
        >
          <n-input v-model:value="newMemberName" clearable placeholder="请输入要添加的用户名"/>
          <n-select
            v-model:value="newMemberRole"
            :options="toOptions(SPACE_ROLE_MAP)"
            placeholder="请选择角色"
            style="width: 150px"
          />
          <n-button type="primary" @click="handleAddMember">
            <template #icon>
              <n-icon>
                <AddIcon/>
              </n-icon>
            </template>
            添加成员
          </n-button>
        </n-space>

        <n-list bordered clickable hoverable>
          <n-list-item v-for="member in teamMembers" :key="member.id">
            <template #prefix>
              <n-avatar
                :size="48"
                :src="member.userDetailVO?.avatar"
                :style="{ marginRight: '16px' }"
                round
              />
            </template>
            <!--这里展示的id就是用户id,不是空间角色id-->
            <n-thing
              :description="`ID: ${member.userDetailVO?.id}`"
              :title="member.userDetailVO?.name"
            />
            <template #suffix>
              <n-space align="center">
                <n-select
                  :disabled="loginUserMember?.spaceRole !== SPACE_ROLE_ENUM.ADMIN || member.userDetailVO?.id === useLoginUserStore().userInfo.id"
                  :options="toOptions(SPACE_ROLE_MAP)"
                  :value="member.spaceRole"
                  style="width: 120px"
                  @update:value="(newRoleValue) => handleRoleChange(member.id, newRoleValue)"
                />
                <n-button
                  v-if="loginUserMember?.spaceRole === SPACE_ROLE_ENUM.ADMIN && member.spaceRole !== SPACE_ROLE_ENUM.ADMIN"
                  style="width: 120px"
                  @click="handleDeletedMember(member.id as any)"
                >
                  <template #icon>
                    <n-icon>
                      <RemoveIcon/>
                    </n-icon>
                  </template>
                  删除成员
                </n-button>
              </n-space>
            </template>
          </n-list-item>
        </n-list>
      </n-card>
    </n-space>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import {getSpaceDetailBySpaceIdUsingGet} from '@/services/api/spaceController'
import {formatSize, toOptions} from '@/shared/utils/util.ts'
import {formatDistanceToNow} from '@/shared/utils/formatDistanceToNow.ts'
import {SPACE_ROLE_ENUM, SPACE_ROLE_MAP} from '@/shared/constants/space.ts'
import {useMessage} from 'naive-ui'
import {Add as AddIcon, TrashOutline as RemoveIcon} from '@vicons/ionicons5'
import {
  addSpaceUserUsingPost,
  deleteSpaceUserUsingPost,
  editSpaceUserUsingPost,
  getTeamSpaceMembersBySpaceIdUsingGet,
} from '@/services/api/spaceUserController.ts'
import {useLoginUserStore} from '@/app/store/useLoginUserStore.ts'

const teamInfo = ref<API.SpaceTeamDetailVO>()
const teamMembers = ref<API.SpaceUserVO[]>([])
const newMemberName = ref<string>('')
const newMemberRole = ref()
const message = useMessage()
const loginUserMember = ref<API.SpaceUserVO>()
const {spaceId} = defineProps<{ spaceId: string }>()
const fetchSpaceUserVO = async () => {
  const {data} = await getTeamSpaceMembersBySpaceIdUsingGet({spaceId: spaceId as any})
  teamMembers.value = data ?? []
  loginUserMember.value = teamMembers.value.find(
    (item) => item.userDetailVO?.id === useLoginUserStore().userInfo.id,
  )
}

const fetchTeamInfo = async () => {
  const {data} = await getSpaceDetailBySpaceIdUsingGet({spaceId: spaceId as any})
  teamInfo.value = data ?? {}
}

// 处理添加成员逻辑
const handleAddMember = async () => {
  if (!newMemberName.value || !newMemberRole.value) {
    message.warning('请输入用户名并选择一个角色')
    return
  }
  if (newMemberRole.value === SPACE_ROLE_ENUM.ADMIN) {
    message.warning('不能添加成员为管理员')
    return
  }
  if (!teamInfo.value?.id) {
    message.error('无法获取空间ID，请刷新页面')
    return
  }
  await addSpaceUserUsingPost({
    userName: newMemberName.value,
    spaceId: teamInfo.value.id,
    spaceRole: newMemberRole.value as string,
  })
  message.success('添加成功')
  newMemberName.value = ''
  newMemberRole.value = null
  await fetchSpaceUserVO()
}

// 处理更新角色逻辑
const handleRoleChange = async (memberId: string, spaceRole: string) => {
  if (spaceRole === SPACE_ROLE_ENUM.ADMIN) {
    message.warning('不能将成员设为管理员')
    return
  }
  await editSpaceUserUsingPost({id: memberId, spaceId: teamInfo.value?.id, spaceRole: spaceRole})
  message.success('角色更新成功')
  await fetchSpaceUserVO()
}

// 处理移除成员逻辑
const handleDeletedMember = async (spaceUserId: string) => {
  if (!spaceUserId) {
    message.error('无法获取成员ID，请刷新页面')
    return
  }
  await deleteSpaceUserUsingPost({id: spaceUserId as any, spaceId: teamInfo.value?.id})
  message.success('移除成员成功')
  await fetchSpaceUserVO()
}

onMounted(() => {
  fetchSpaceUserVO()
  fetchTeamInfo()
})
</script>

<style scoped></style>
