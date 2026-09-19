package com.equipment.service;

import com.equipment.entity.WorkOrder;
import com.equipment.mapper.WorkOrderMapper;
import org.springframework.stereotype.Service;
import com.equipment.mapper.EquipmentMapper;
import com.equipment.entity.Equipment;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.equipment.exception.BusinessException;

import java.util.List;

@Service
public class WorkOrderService {

    private final WorkOrderMapper workOrderMapper;
    private final EquipmentMapper equipmentMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public WorkOrderService(WorkOrderMapper workOrderMapper,
                            EquipmentMapper equipmentMapper,
                            StringRedisTemplate stringRedisTemplate) {
        this.workOrderMapper = workOrderMapper;
        this.equipmentMapper = equipmentMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public List<WorkOrder> list() {
        return workOrderMapper.selectList(null);
    }

    public WorkOrder getById(Long id) {
        WorkOrder workOrder = workOrderMapper.selectById(id);

        if (workOrder == null) {
            throw new BusinessException(2001, "工单不存在");
        }

        return workOrder;
    }

    public boolean add(WorkOrder workOrder) {

        // 3. 判断设备是否真实存在
        if (equipmentMapper.selectById(workOrder.getEquipmentId()) == null) {
            throw new BusinessException(2004, "设备不存在，无法创建工单");
        }

        // 4. 判断是否存在未完成工单
        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("equipment_id", workOrder.getEquipmentId())
                .in("status", "待处理", "处理中");

        Long count = workOrderMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new BusinessException(2005, "该设备已有未完成工单，请勿重复创建");
        }

        // 5. 没填状态时自动设为“待处理”
        if (workOrder.getStatus() == null ||
                workOrder.getStatus().trim().isEmpty()) {
            workOrder.setStatus("待处理");
        }

        // 6. 保存
        return workOrderMapper.insert(workOrder) > 0;
    }

    public boolean update(WorkOrder workOrder) {
        WorkOrder oldWorkOrder = workOrderMapper.selectById(workOrder.getId());

        if (oldWorkOrder == null) {
            throw new BusinessException(2009, "工单不存在，无法修改");
        }

        return workOrderMapper.updateById(workOrder) > 0;
    }

    public boolean delete(Long id) {
        WorkOrder workOrder = workOrderMapper.selectById(id);

        if (workOrder == null) {
            throw new BusinessException(2010, "工单不存在，无法删除");
        }

        return workOrderMapper.deleteById(id) > 0;
    }

    @Transactional
    public boolean updateStatus(Long id, String newStatus) {

        WorkOrder workOrder = workOrderMapper.selectById(id);

        if (workOrder == null) {
            throw new BusinessException(2001, "工单不存在");
        }

        String oldStatus = workOrder.getStatus();

        if ("待处理".equals(oldStatus) && "处理中".equals(newStatus)) {

            workOrder.setStatus("处理中");

            Equipment equipment =
                    equipmentMapper.selectById(workOrder.getEquipmentId());

            if (equipment != null) {
                equipment.setStatus("维修中");
                equipmentMapper.updateById(equipment);

                String cacheKey = "equipment:" + equipment.getId();
                stringRedisTemplate.delete(cacheKey);

                System.out.println("工单状态更新，删除设备缓存：" + cacheKey);
            }

        } else {
            throw new BusinessException(2006, "工单状态流转不合法");
        }

        return workOrderMapper.updateById(workOrder) > 0;
    }

    @Transactional
    public boolean complete(Long id, String result) {

        WorkOrder workOrder = workOrderMapper.selectById(id);

        if (workOrder == null) {
            throw new BusinessException(2001, "工单不存在");
        }

        if (!"处理中".equals(workOrder.getStatus())) {
            throw new BusinessException(2007, "只有处理中的工单才能完成");
        }

        if (result == null || result.trim().isEmpty()) {
            throw new BusinessException(2008, "完成工单时必须填写处理结果");
        }

        workOrder.setStatus("已完成");
        workOrder.setResult(result);

        Equipment equipment =
                equipmentMapper.selectById(workOrder.getEquipmentId());

        if (equipment != null) {
            equipment.setStatus("正常");
            equipmentMapper.updateById(equipment);

            String cacheKey = "equipment:" + equipment.getId();
            stringRedisTemplate.delete(cacheKey);

            System.out.println("工单完成，删除设备缓存：" + cacheKey);
        }

        return workOrderMapper.updateById(workOrder) > 0;
    }

    public Page<WorkOrder> page(Long current, Long size, Long equipmentId, String status, String title) {
        Page<WorkOrder> page = new Page<>(current, size);

        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<>();

        if (equipmentId != null) {
            queryWrapper.eq("equipment_id", equipmentId);
        }

        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq("status", status);
        }

        if (title != null && !title.trim().isEmpty()) {
            queryWrapper.like("title", title);
        }


        return workOrderMapper.selectPage(page, queryWrapper);
    }

    public List<WorkOrder> getHistoryByEquipmentId(Long equipmentId) {

        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("equipment_id", equipmentId)
                .eq("status", "已完成")
                .orderByDesc("update_time");

        return workOrderMapper.selectList(queryWrapper);
    }
}
