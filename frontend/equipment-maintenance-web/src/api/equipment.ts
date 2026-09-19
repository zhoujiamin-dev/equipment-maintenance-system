import request from '../utils/request'

export function getEquipmentPage(params: {
  current: number
  size: number
  name?: string
  status?: string
}) {
  return request.get('/equipment/page', {
    params,
  })
}

export function deleteEquipment(id: number) {
  return request.delete(`/equipment/${id}`)
}
