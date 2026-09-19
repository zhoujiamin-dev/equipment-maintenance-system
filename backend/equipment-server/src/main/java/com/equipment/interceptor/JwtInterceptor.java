package com.equipment.interceptor;

import com.equipment.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.equipment.entity.SysUser;
import com.equipment.mapper.SysUserMapper;
import com.equipment.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final SysUserMapper sysUserMapper;
    private final RoleService roleService;

    public JwtInterceptor(JwtUtil jwtUtil,
                          SysUserMapper sysUserMapper,
                          RoleService roleService) {
        this.jwtUtil = jwtUtil;
        this.sysUserMapper = sysUserMapper;
        this.roleService = roleService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || token.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"未登录或Token缺失\"}");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"Token无效或已过期\"}");
            return false;
        }

        String username = jwtUtil.getUsername(token);

        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.eq("username", username);

        SysUser user = sysUserMapper.selectOne(userQuery);

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"用户不存在\"}");
            return false;
        }

        String role = roleService.getRoleCodeByUserId(user.getId());

        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        if ("DELETE".equalsIgnoreCase(method)
                && requestUri.startsWith("/equipment/")
                && !"ADMIN".equals(role)) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"无权限删除设备\"}");
            return false;
        }

        return true;
    }
}