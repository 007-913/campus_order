package org.example.campus_order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.campus_order.utils.JwtUtil;
import org.example.campus_order.utils.Result;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 放行登录接口，不用校验 token
        String path = request.getServletPath();
        if (path.startsWith("/login") || path.equals("/auth/login")) {
            return true;
        }

        // 获取请求头 token（兼容 token 和 Authorization: Bearer xxx）
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            Result<?> res = Result.error("未登录");
            OBJECT_MAPPER.writeValue(response.getWriter(), res);
            return false;
        }

        try {
            JwtUtil.verifyToken(token);

            Long userId = JwtUtil.getUserId(token);
            Integer role = JwtUtil.getRole(token);
            Long shopId = JwtUtil.getShopId(token);

            request.setAttribute("loginId", userId);
            request.setAttribute("loginRole", role);
            request.setAttribute("loginShopId", shopId);

            return true;
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            Result<?> res = Result.error("登录失效，请重新登录");
            OBJECT_MAPPER.writeValue(response.getWriter(), res);
            return false;
        }
    }
}
