package org.example.campus_order.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.Date;

/**
 * JWT 工具类
 * 角色统一: 0=管理员, 1=商家, 2=学生
 */
public class JwtUtil {

    private static final String SECRET_KEY = "campus_order_secret_key_2024";
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    /** 角色: 管理员 */
    public static final int ROLE_ADMIN = 0;
    /** 角色: 商家 */
    public static final int ROLE_SHOP = 1;
    /** 角色: 学生 */
    public static final int ROLE_USER = 2;

    /**
     * 创建 token（含 shopId，商家登录时传入）
     */
    public static String createToken(Long userId, Integer role, Long shopId) {
        var builder = JWT.create()
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME));
        if (shopId != null) {
            builder.withClaim("shopId", shopId);
        }
        return builder.sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /**
     * 创建 token（不含 shopId）
     */
    public static String createToken(Long userId, Integer role) {
        return createToken(userId, role, null);
    }

    /** 校验 token */
    public static void verifyToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(SECRET_KEY)).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException(ResultEnum.TOKEN_ERR.getMsg());
        }
    }

    /** 获取用户 ID */
    public static Long getUserId(String token) {
        return JWT.decode(token).getClaim("userId").asLong();
    }

    /** 获取角色 */
    public static Integer getRole(String token) {
        return JWT.decode(token).getClaim("role").asInt();
    }

    /** 获取店铺 ID（商家角色才有，其他角色返回 null） */
    public static Long getShopId(String token) {
        var claim = JWT.decode(token).getClaim("shopId");
        return claim.isNull() ? null : claim.asLong();
    }
}