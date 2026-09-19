package com.equipment.controller;

import com.equipment.common.Result;
import com.equipment.entity.WorkOrder;
import com.equipment.service.WorkOrderService;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/work-order")
@CrossOrigin(origins = "http://localhost:5173")

public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping("/list")
    public Result<List<WorkOrder>> list() {
        return Result.success("查询成功", workOrderService.list());
    }

    @GetMapping("/history/{equipmentId}")
    public Result<List<WorkOrder>> getHistory(@PathVariable Long equipmentId) {
        return Result.success(workOrderService.getHistoryByEquipmentId(equipmentId));
    }

    @GetMapping("/page")
    public Result<Page<WorkOrder>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String title) {

        Page<WorkOrder> page =
                workOrderService.page(current, size, equipmentId, status, title);

        return Result.success("查询成功", page);
    }


    @GetMapping("/{id}")
    public Result<WorkOrder> getById(@PathVariable Long id) {
        return Result.success("查询成功", workOrderService.getById(id));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody @Valid WorkOrder workOrder) {
        workOrderService.add(workOrder);
        return Result.success("新增工单成功", null);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody WorkOrder workOrder) {
        workOrderService.update(workOrder);
        return Result.success("修改工单成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        workOrderService.delete(id);
        return Result.success("删除工单成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestParam String status) {
        workOrderService.updateStatus(id, status);
        return Result.success("工单状态修改成功", null);
    }

    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id,
                                 @RequestParam String result) {
        workOrderService.complete(id, result);
        return Result.success("工单完成成功", null);
    }
}