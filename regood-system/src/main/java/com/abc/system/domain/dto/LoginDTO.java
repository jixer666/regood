package com.abc.system.domain.dto;

import com.abc.common.util.AssertUtils;
import lombok.Data;

@Data
public class LoginDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 登录方式
     */
    private Integer authType;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 邮箱验证码
     */
    private String emailCode;

    /**
     * 邮箱验证码唯一标识
     */
    private String emailUuid;

    public void checkParams() {
        AssertUtils.isNotEmpty(username, "账户不能为空");
        AssertUtils.isNotEmpty(password, "密码不能为空");
        AssertUtils.isNotEmpty(authType, "登录方式不能为空");

    }

    public void checkEmailParams() {
        AssertUtils.isNotEmpty(email, "邮箱不能为空");
        AssertUtils.isNotEmpty(emailCode, "邮箱验证码不能为空");
        AssertUtils.isNotEmpty(emailUuid, "邮箱验证码uuid不能为空");
        AssertUtils.isNotEmpty(authType, "登录方式不能为空");
    }
}
