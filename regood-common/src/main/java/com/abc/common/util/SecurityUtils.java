package com.abc.common.util;

import com.abc.common.constant.CommonConstants;
import com.abc.common.domain.dto.LoginUserDTO;
import com.abc.common.exception.GlobalException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class SecurityUtils {

    private static final String SECRET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    public static Long getUserId() {
        try {
            return getLoginUser().getUserId();
        } catch (Exception e) {
            log.error("获取用户ID异常：{}", e.getMessage(), e);
            throw new GlobalException("获取用户ID异常");
        }
    }

    /**
     * 获取用户
     */
    public static LoginUserDTO getLoginUser() {
        try {
            return (LoginUserDTO) getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.error("获取用户信息异常：{}", e.getMessage(), e);
            throw new GlobalException("获取用户信息异常");
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 从token中获取用户ID
     */
    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get(CommonConstants.JWT_USERID, Long.class);
        } catch (Exception e) {
            log.error("从token解析用户ID失败：{}", e.getMessage());
            return null;
        }
    }
}
