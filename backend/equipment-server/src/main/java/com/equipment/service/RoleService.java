package com.equipment.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.equipment.entity.SysRole;
import com.equipment.entity.SysUserRole;
import com.equipment.mapper.SysRoleMapper;
import com.equipment.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    public RoleService(SysUserRoleMapper sysUserRoleMapper,
                       SysRoleMapper sysRoleMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    public String getRoleCodeByUserId(Long userId) {

        QueryWrapper<SysUserRole> userRoleQuery = new QueryWrapper<>();
        userRoleQuery.eq("user_id", userId);

        SysUserRole userRole = sysUserRoleMapper.selectOne(userRoleQuery);

        if (userRole == null) {
            return null;
        }

        SysRole role = sysRoleMapper.selectById(userRole.getRoleId());

        if (role == null) {
            return null;
        }

        return role.getRoleCode();
    }
}