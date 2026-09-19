package com.equipment.controller;

import com.equipment.common.Result;
import com.equipment.entity.SysUser;
import com.equipment.service.AuthService;
import com.equipment.service.RoleService;
import com.equipment.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RoleService roleService;

    public AuthController(AuthService authService,
                          JwtUtil jwtUtil,
                          RoleService roleService) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.roleService = roleService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");

        SysUser user = authService.login(username, password);

        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        String role = roleService.getRoleCodeByUserId(user.getId());

        user.setPassword(null);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        data.put("role", role);

        return Result.success(data);
    }
}