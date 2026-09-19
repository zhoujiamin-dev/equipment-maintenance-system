package com.equipment.service;

import com.equipment.entity.Equipment;
import com.equipment.mapper.EquipmentMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.exception.BusinessException;

import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@Service
public class EquipmentService {

    private final EquipmentMapper equipmentMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public EquipmentService(EquipmentMapper equipmentMapper,
                            StringRedisTemplate stringRedisTemplate,
                            ObjectMapper objectMapper) {
        this.equipmentMapper = equipmentMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Equipment> list() {
        return equipmentMapper.selectList(null);
    }

    public Equipment getById(Long id) {

        // 1. 生成 Redis 中的 key
        String cacheKey = "equipment:" + id;

        // 2. 先查 Redis
        String cachedEquipment = stringRedisTemplate.opsForValue().get(cacheKey);

        if (cachedEquipment != null && !cachedEquipment.isEmpty()) {
            try {
                System.out.println("命中 Redis 缓存：" + cacheKey);
                return objectMapper.readValue(cachedEquipment, Equipment.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Redis 缓存数据解析失败", e);
            }
        }

        // 3. Redis 没有，再查询 MySQL
        System.out.println("Redis 未命中，查询 MySQL：" + cacheKey);

        Equipment equipment = equipmentMapper.selectById(id);

        if (equipment == null) {
            throw new BusinessException(1001, "设备不存在");
        }

        // 4. 查询到数据以后写入 Redis，缓存 10 分钟
        try {
            String json = objectMapper.writeValueAsString(equipment);

            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    json,
                    Duration.ofMinutes(10)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("设备数据写入 Redis 失败", e);
        }

        return equipment;
    }

    public boolean add(Equipment equipment) {

        // 1. 设备编号不能为空
        if (equipment.getEquipmentNo() == null ||
                equipment.getEquipmentNo().trim().isEmpty()) {
            throw new BusinessException(1002, "设备编号不能为空");
        }

        // 2. 设备名称不能为空
        if (equipment.getEquipmentName() == null ||
                equipment.getEquipmentName().trim().isEmpty()) {
            throw new BusinessException(1003, "设备名称不能为空");
        }

        // 3. 检查设备编号是否重复
        QueryWrapper<Equipment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("equipment_no", equipment.getEquipmentNo());

        Long count = equipmentMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new BusinessException(1004, "设备编号已存在");
        }

        // 4. 如果没有填写状态，默认设为“正常”
        if (equipment.getStatus() == null ||
                equipment.getStatus().trim().isEmpty()) {
            equipment.setStatus("正常");
        }

        // 5. 保存设备
        return equipmentMapper.insert(equipment) > 0;
    }

    public boolean update(Equipment equipment) {

        Equipment oldEquipment = equipmentMapper.selectById(equipment.getId());

        if (oldEquipment == null) {
            throw new BusinessException(1005, "设备不存在，无法修改");
        }

        boolean success = equipmentMapper.updateById(equipment) > 0;

        if (success) {
            String cacheKey = "equipment:" + equipment.getId();
            stringRedisTemplate.delete(cacheKey);

            System.out.println("设备修改成功，删除 Redis 缓存：" + cacheKey);
        }

        return success;
    }

    public boolean delete(Long id) {

        Equipment equipment = equipmentMapper.selectById(id);

        if (equipment == null) {
            throw new BusinessException(1006, "设备不存在，无法删除");
        }

        boolean success = equipmentMapper.deleteById(id) > 0;

        if (success) {
            String cacheKey = "equipment:" + id;
            stringRedisTemplate.delete(cacheKey);

            System.out.println("设备删除成功，删除 Redis 缓存：" + cacheKey);
        }

        return success;
    }

    public Page<Equipment> page(Long current, Long size, String name, String status) {

        // 1. 创建分页对象
        Page<Equipment> page = new Page<>(current, size);

        // 2. 创建查询条件
        QueryWrapper<Equipment> queryWrapper = new QueryWrapper<>();

        // 3. 如果传了设备名称，就进行模糊查询
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like("equipment_name", name);
        }

        // 4. 如果传了设备状态，就按照状态查询
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq("status", status);
        }

        // 5. 分页查询
        return equipmentMapper.selectPage(page, queryWrapper);
    }
}