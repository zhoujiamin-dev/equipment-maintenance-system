package com.equipment.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.equipment.entity.SysUser;
import com.equipment.mapper.SysUserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SysUserMapper sysUserMapper;

    public AuthService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public SysUser login(String username, String password) {

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);

        SysUser user = sysUserMapper.selectOne(queryWrapper);

        if (user == null) {
            return null;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        boolean passwordCorrect =
                encoder.matches(password, user.getPassword());

        if (!passwordCorrect) {
            return null;
        }

        return user;
    }
}
