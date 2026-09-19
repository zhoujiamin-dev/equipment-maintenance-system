package com.equipment.controller;

import com.equipment.common.Result;
import com.equipment.entity.Equipment;
import com.equipment.service.EquipmentService;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/equipment")
@CrossOrigin(origins = "http://localhost:5173")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/list")
    public Result<List<Equipment>> list() {
        return Result.success("查询成功", equipmentService.list());
    }

    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable Long id) {
        return Result.success("查询成功", equipmentService.getById(id));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody @Valid Equipment equipment) {
        equipmentService.add(equipment);
        return Result.success("新增成功", null);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Valid Equipment equipment) {
        equipmentService.update(equipment);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/page")
    public Result<Page<Equipment>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {

        return Result.success(
                "查询成功",
                equipmentService.page(current, size, name, status)
        );
    }
}