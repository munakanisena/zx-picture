// @ts-ignore
/* eslint-disable */
import request from '@/services/http/request'

/** health GET /api/health */
export async function healthUsingGet(options?: { [key: string]: any }) {
  return request<string>('/api/health', {
    method: 'GET',
    ...(options || {}),
  })
}
