<template>
  <n-grid :x-gap="12" :y-gap="12" cols="1 s:2 m:3 l:4 xl:5" responsive="screen">
    <n-gi span="1" v-for="pictureVO in pictureList" :key="pictureVO.id">
      <BPictureCard
        style="box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1)"
        :src="pictureVO.compressUrl"
        :picture-id="pictureVO.id"
        cover-height="300px"
      >
        <template #actions>
          <div style="display: flex; width: 100%; padding: 8px 12px; border-top: 1px solid #f0f0f0; align-items: center; height: 48px; box-sizing: border-box">
            <n-ellipsis style="flex: 1; min-width: 0; font-weight: 600; font-size: 14px; margin-right: 4px">
              {{ pictureVO.picName }}
            </n-ellipsis>
            <div style="display: flex; align-items: center; gap: 0; flex-shrink: 0">
              <n-button
                v-if="loginUserMember?.spaceRole !== SPACE_ROLE_ENUM.VIEWER"
                text
                style="padding: 0 4px"
                @click="doEditPicture(pictureVO)"
                title="编辑"
              >
                <n-icon size="18">
                  <BuildOutline />
                </n-icon>
              </n-button>
              <n-button
                v-if="loginUserMember?.spaceRole !== SPACE_ROLE_ENUM.VIEWER"
                text
                style="padding: 0 4px"
                title="删除"
                @click="doClickDelete(pictureVO.id as number)"
              >
                <n-icon size="18">
                  <TrashOutline />
                </n-icon>
              </n-button>
              <n-button text style="padding: 0 4px" @click="shareAction(pictureVO)" title="分享">
                <n-icon size="18">
                  <ShareSocialOutline />
                </n-icon>
              </n-button>
            </div>
          </div>
        </template>
      </BPictureCard>
    </n-gi>
  </n-grid>
  <BPictureShare ref="pictureShareRef" :link="shareLink" />
</template>

<script setup lang="ts">
import { BuildOutline, ShareSocialOutline, TrashOutline } from '@vicons/ionicons5'
import { useRouter } from 'vue-router'
import BPictureShare from '@/modules/picture/components/BPictureShare.vue'
import BPictureCard from '@/shared/components/BPictureCard.vue'
import { ref, useTemplateRef } from 'vue'
import { deletePictureByIdUsingPost } from '@/services/api/pictureController.ts'
import { SPACE_ROLE_ENUM } from '@/shared/constants/space.ts'

interface Props {
  pictureList?: API.PictureVO[]
  spaceInfo?: API.SpaceTeamDetailVO | API.SpaceDetailVO
  loginUserMember?: API.SpaceUserVO
}

const shareLink = ref<string>()
const emits = defineEmits(['picture-deleted'])
const props = defineProps<Props>()
const router = useRouter()
const pictureShareRef = useTemplateRef('pictureShareRef')
const message = useMessage()

const doEditPicture = (pictureVO: API.PictureVO) => {
  router.push({
    name: 'picture-edit',
    params: { pictureId: pictureVO.id },
    query: {
      space_id: props.spaceInfo?.id,
      space_name: props.spaceInfo?.spaceName,
      space_type: props.spaceInfo?.spaceType,
    },
  })
}

const shareAction = (pictureVO: API.PictureVO) => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/detail/${pictureVO.id}`
  pictureShareRef.value?.openModal()
}

const doClickDelete = async (pictureId: number) => {
  await deletePictureByIdUsingPost({ id: pictureId, spaceId: props.spaceInfo?.id })
  emits('picture-deleted')
  message.success('删除成功')
}
</script>

<style scoped></style>
