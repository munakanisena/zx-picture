import { createRouter, createWebHistory } from 'vue-router'
import {
  getPrivateSpaceDetailByLoginUserUsingGet,
  getTeamSpaceDetailByLoginUserUsingGet,
} from '@/services/api/spaceController.ts'
import { SPACE_TYPE_ENUM } from '@/shared/constants/space.ts'
import { useLoginUserStore } from '@/app/store/useLoginUserStore.ts'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      //此时，AuthLayout.vue 会作为 所有子路由的父容器，例如访问 /login 时，
      //会先渲染 AuthLayout.vue，再在其内部渲染 SignIn.vue。
      path: '/*',
      component: () => import('../../modules/auth/pages/AuthLayout.vue'),
      children: [
        {
          path: '/login',
          name: 'login',
          meta: { title: '登录' },
          component: () => import('../../modules/auth/pages/SignIn.vue'),
          props: (route) => ({ from: route.query.from }),
        },
        {
          path: '/reset-password',
          name: 'reset-password',
          meta: { title: '重置密码' },
          component: () => import('../../modules/auth/pages/ResetPassword.vue'),
        },
      ],
    },
    {
      path: '/*',
      component: () => import('../../layouts/MainLayout.vue'),
      children: [
        {
          path: '/',
          name: 'home',
          meta: { title: '首页' },
          component: () => import('../../modules/home/pages/Home.vue'),
        },
        {
          path: '/user-profile',
          name: 'user-profile',
          meta: { title: '个人资料' },
          component: () => import('../../modules/user/pages/UserProfile.vue'),
        },
        {
          path: '/user-edit',
          name: 'user-edit',
          meta: { title: '编辑资料' },
          component: () => import('../../modules/user/pages/UserEdit.vue'),
        },
        //--------------图片页面--------------
        {
          path: '/picture-upload',
          name: 'picture-upload',
          meta: { title: '上传图片' },
          component: () => import('../../modules/picture/pages/PictureUpload.vue'),
        },
        {
          path: '/picture-upload/batch',
          name: 'picture-upload-batch',
          meta: { title: '批量上传图片' },
          component: () => import('../../modules/picture/pages/PictureUploadBatch.vue'),
        },
        {
          path: '/picture/detail/:pictureId',
          name: 'picture-detail',
          meta: { title: '图片详情' },
          props: true,
          component: () => import('../../modules/picture/pages/PictureDetail.vue'),
        },
        {
          path: '/picture-edit/:pictureId',
          name: 'picture-edit',
          meta: { title: '编辑图片' },
          props: (route) => ({
            pictureId: route.params.pictureId,
            spaceId: route.query.space_id,
            spaceName: route.query.space_name,
            spaceType: route.query.space_type,
          }),
          component: () => import('../../modules/picture/pages/PictureEdit.vue'),
        },
        {
          path: '/picture-ai/extend/:pictureId',
          name: 'picture-ai-extend',
          meta: { title: 'AI扩图' },
          props: true,
          component: () => import('../../modules/picture/pages/PictureAIExtend.vue'),
        },
        {
          path: '/picture-capture',
          name: 'picture-capture',
          meta: { title: '抓取图片' },
          component: () => import('../../modules/picture/pages/PictureCapture.vue'),
        },
        {
          path: '/picture-search/by-picture',
          name: 'picture-search-by-picture',
          meta: { title: '以图搜图' },
          component: () => import('../../modules/picture/pages/PictureSearchByPicture.vue'),
        },
        {
          path: '/picture-collect',
          name: 'picture-collect',
          meta: { title: '图片收藏' },
          component: () => import('../../modules/picture/pages/PictureCollect.vue'),
        },

        //空间模块
        {
          path: '/space-active',
          name: 'space-active',
          meta: { title: '激活空间' },
          props: (route) => ({
            type: route.query.type,
            from: route.query.from,
          }),
          component: () => import('../../modules/space/pages/SpaceActive.vue'),
        },
        {
          path: '/space/person',
          name: 'space-person',
          meta: { title: '个人空间' },
          component: () => import('../../modules/space/pages/Space.vue'),
          beforeEnter: async (to, from, next) => {
            try {
              //先查询是否已经存在个人空间
              const { data } = await getPrivateSpaceDetailByLoginUserUsingGet()
              if (data == null) {
                //没有就前往激活空间页面
                next({
                  name: 'space-active',
                  query: { type: SPACE_TYPE_ENUM.PRIVATE, from: '/space/person' },
                })
              } else {
                next()
              }
            } catch (error) {
              console.error('获取个人空间详情失败:', error)
              next({
                name: 'space-active',
                query: { type: SPACE_TYPE_ENUM.PRIVATE, from: '/space/person' },
              })
            }
          },
        },
        {
          path: '/space/team',
          name: 'space-team',
          meta: { title: '团队空间' },
          component: () => import('../../modules/space/pages/TeamSpace.vue'),
          beforeEnter: async (to, from, next) => {
            try {
              //如果前往的是加入的团队空间 直接放行
              if (!!to.query?.space_id) {
                next()
                return
              }
              //否则就是前往自己的空间
              const { data } = await getTeamSpaceDetailByLoginUserUsingGet()
              if (data == null) {
                //前往激活团队空间页面
                next({
                  name: 'space-active',
                  query: { type: SPACE_TYPE_ENUM.TEAM, from: '/space/team' },
                })
              } else {
                next()
              }
            } catch (error) {
              console.error('获取团队空间详情失败:', error)
              next({
                name: 'space-active',
                query: { type: SPACE_TYPE_ENUM.TEAM, from: '/space/team' },
              })
            }
          },
        },
        {
          path: '/space/team/join',
          name: 'space-team-join',
          meta: { title: '加入团队' },
          component: () => import('../../modules/space/pages/TeamSpaceJoin.vue'),
        },
        {
          path: '/space/team/member/:spaceId',
          name: 'space-team-member',
          meta: { title: '团队成员' },
          props: true,
          component: () => import('../../modules/space/pages/TeamSpaceMember.vue'),
        },
        {
          path: '/admin/analyze-analyze/:spaceId',
          name: 'analyze-analyze',
          meta: { title: '空间分析' },
          props: (route) => ({
            spaceId: route.params.spaceId,
            queryAll: Number(route.query.queryAll),
            queryPublic: Number(route.query.queryPublic),
          }),
          component: () => import('../../modules/space/pages/SpaceAnalyze.vue'),
        },

        //------------管理员页面--------------
        {
          path: '/admin/user-manager',
          name: 'user-manager',
          meta: { title: '用户管理' },
          component: () => import('../../modules/admin/pages/UserManager.vue'),
        },
        {
          path: '/admin/picture-manager',
          name: 'picture-manager',
          meta: { title: '图片管理' },
          component: () => import('../../modules/admin/pages/PictureManager.vue'),
        },
        {
          path: '/admin/category-manager',
          name: 'category-manager',
          meta: { title: '分类管理' },
          component: () => import('../../modules/admin/pages/CategoryManager.vue'),
        },
        {
          path: '/admin/analyze-manager',
          name: 'analyze-manager',
          meta: { title: '空间管理' },
          component: () => import('../../modules/admin/pages/SpaceManager.vue'),
        },
        {
          path: '/timeline',
          name: 'timeline',
          meta: { title: '项目开发历程' },
          component: () => import('../../modules/other/pages/TimeLine.vue'),
        },
        {
          path: '/:pathMath(.*)',
          component: () => import('../../modules/other/pages/NotFound.vue'),
        },
      ],
    },
  ],
})



//路由白名单
const WHITE_LIST = ['/login', '/', '/reset-password', '/timeline', '/picture/detail/*']
function isWhiteListed(path: string): boolean {
      return WHITE_LIST.some((pattern) => {
            if (pattern.endsWith('/*')) {
                return path.startsWith(pattern.slice(0, -1))
        }
            return path === pattern
      })
}
router.beforeEach(async (to, from, next) => {
  //白名单直接放行
  if (isWhiteListed(to.path)) {
    next()
    return
  }
  const userStore = useLoginUserStore()
  if (!userStore.isLogin) {
    next(`/login?from=${to.fullPath}`)
    return
  }
  next()
})

/**
 * 标签转换
 */
router.afterEach((to) => {
  const definedTitle = '惠眸图界'
  const title = to.meta.title
  if (title !== undefined) {
    document.title = title + ' | ' + definedTitle
  } else {
    document.title = definedTitle
  }
})

export default router
