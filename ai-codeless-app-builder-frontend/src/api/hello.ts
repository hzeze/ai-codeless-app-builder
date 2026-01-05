// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /hello/ */
export async function hello(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/hello/', {
    method: 'GET',
    ...(options || {}),
  })
}
