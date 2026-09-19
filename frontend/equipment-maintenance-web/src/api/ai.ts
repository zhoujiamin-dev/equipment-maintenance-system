import request from '../utils/request'

export function analyzeFault(
  equipmentId: number,
  description: string,
) {
  return request.post(
    '/ai/analyze',
    description,
    {
      params: {
        equipmentId: equipmentId,
      },
      headers: {
        'Content-Type': 'text/plain;charset=UTF-8',
      },
    },
  )
}

export function analyzeFaultWithRag(
  equipmentId: number,
  description: string,
) {
  return request.post(
    '/rag/analyze',
    description,
    {
      params: {
        equipmentId: equipmentId,
      },
      headers: {
        'Content-Type': 'text/plain;charset=UTF-8',
      },
    },
  )
}

export function createWorkOrderFromAnalysis(analysisId: number) {
  return request.post(
    `/ai/${analysisId}/create-work-order`,
  )
}
