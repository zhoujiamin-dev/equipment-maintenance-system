package com.equipment.service;

import com.equipment.entity.AiAnalysis;
import com.equipment.mapper.AiAnalysisMapper;
import org.springframework.stereotype.Service;
import com.equipment.entity.WorkOrder;
import com.equipment.service.WorkOrderService;
import com.equipment.exception.BusinessException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiAnalysisService {

    private final AiAnalysisMapper aiAnalysisMapper;
    private final WorkOrderService workOrderService;

    public AiAnalysisService(
            AiAnalysisMapper aiAnalysisMapper,
            WorkOrderService workOrderService) {
        this.aiAnalysisMapper = aiAnalysisMapper;
        this.workOrderService = workOrderService;
    }

    public boolean save(AiAnalysis aiAnalysis) {
        return aiAnalysisMapper.insert(aiAnalysis) > 0;
    }

    @Transactional
    public Long createWorkOrder(Long analysisId) {

        AiAnalysis aiAnalysis = aiAnalysisMapper.selectById(analysisId);

        if (aiAnalysis == null) {
            throw new BusinessException(3001, "AI分析记录不存在");
        }

        if (aiAnalysis.getWorkOrderId() != null) {
            throw new BusinessException(3002, "该AI分析已创建维修工单");
        }

        WorkOrder workOrder = new WorkOrder();

        workOrder.setEquipmentId(aiAnalysis.getEquipmentId());
        workOrder.setTitle("AI辅助分析维修工单");
        workOrder.setDescription(aiAnalysis.getDescription());

        workOrderService.add(workOrder);

        aiAnalysis.setWorkOrderId(workOrder.getId());
        aiAnalysisMapper.updateById(aiAnalysis);

        return workOrder.getId();
    }
}