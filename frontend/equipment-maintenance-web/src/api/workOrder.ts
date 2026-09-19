import request from '../utils/request'

export function getWorkOrderPage(params: {
  current: number
  size: number
  equipmentId?: number
  status?: string
  title?: string
}) {
  return request.get('/work-order/page', {
    params,
  })
}

export function startWorkOrder(id: number) {
  return request.put(
    `/work-order/${id}/status`,
    null,
    {
      params: {
        status: '处理中',
      },
    },
  )
}

export function completeWorkOrder(id: number, result: string) {
  return request.put(
    `/work-order/${id}/complete`,
    null,
    {
      params: {
        result: result,
      },
    },
  )
}

export function addWorkOrder(data: {
  equipmentId: number
  title: string
  description?: string
  assignee?: string
}) {
  return request.post(
    '/work-order/add',
    data,
  )
}

export function getWorkOrderHistory(equipmentId: number) {
  return request.get(
    `/work-order/history/${equipmentId}`,
  )
}
