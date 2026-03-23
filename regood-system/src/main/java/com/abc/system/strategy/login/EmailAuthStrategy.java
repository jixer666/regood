package com.abc.system.strategy.login;

import cn.hutool.core.util.RandomUtil;
import com.abc.common.constant.CommonConstants;
import com.abc.common.domain.dto.LoginUserDTO;
import com.abc.common.domain.entity.User;
import com.abc.common.domain.enums.CommonRoleEnum;
import com.abc.common.util.AssertUtils;
import com.abc.common.util.IdUtils;
import com.abc.system.convert.UserConvert;
import com.abc.system.domain.dto.LoginDTO;
import com.abc.system.domain.dto.RegisterDTO;
import com.abc.system.service.IndexService;
import com.abc.system.service.MenuService;
import com.abc.system.service.RoleService;
import com.abc.system.service.TokenService;
import com.abc.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmailAuthStrategy implements AuthStrategy {

    @Autowired
    private UserService userService;

    @Autowired
    private IndexService indexService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Override
    public LoginUserDTO authenticate(LoginDTO loginDTO) {
        preLoginCheck(loginDTO);
        User user = registerUser(loginDTO.getEmail());
        return buildLoginUserDTO(user);
    }

    private User registerUser(String email) {
        User user = userService.getUserByEmail(email);
        if (Objects.nonNull(user)) {
            return user;
        }

        User saveUser = User.builder()
                .userId(IdUtils.getId())
                .username(RandomUtil.randomString(10))
                .password(new BCryptPasswordEncoder().encode(RandomUtil.randomString(10)))
                .nickname(CommonConstants.DEFAULT_NICKNAME)
                .avatar(CommonConstants.DEFAULT_AVATAR)
                .email(email)
                .build();
        userService.saveUser(saveUser);
        
        user = userService.getUserByEmail(email);
        roleService.saveUserRoleByRoleKeys(user.getUserId(), Collections.singletonList(CommonRoleEnum.COMMON_USER.getRoleKey()));

        return user;
    }

    private LoginUserDTO buildLoginUserDTO(User user) {
        List<String> roles = roleService.getRoleKeysByUserId(user.getUserId());
        Set<String> perms = menuService.getMenusByUserId(user.getUserId())
                .stream()
                .map(menu -> menu.getPerms())
                .filter(item -> !Objects.isNull(item))
                .collect(Collectors.toSet());

        return new LoginUserDTO(user, perms, new HashSet<>(roles));
    }

    private void preLoginCheck(LoginDTO loginDTO) {
        loginDTO.checkEmailParams();

        Boolean checkEmailCode = indexService.checkEmailCode(loginDTO.getEmailUuid(), loginDTO.getEmailCode());
        AssertUtils.isTrue(checkEmailCode, "邮箱验证码错误");
    }

    @Override
    public User doRegister(RegisterDTO registerDTO) {
        preRegisterCheck(registerDTO);

        User user = UserConvert.convertToUserByRegisterDTO(registerDTO);
        user.setNickname(registerDTO.getEmail().split("@")[0]);
        userService.saveUser(user);
        
        roleService.saveUserRoleByRoleKeys(user.getUserId(), Collections.singletonList(CommonRoleEnum.COMMON_USER.getRoleKey()));

        return user;
    }

    public void preRegisterCheck(RegisterDTO registerDTO) {
        registerDTO.checkEmailParams();
        User user = userService.getUserByEmail(registerDTO.getEmail());
        AssertUtils.isEmpty(user, "邮箱已被绑定");

        Boolean checkEmailCode = indexService.checkEmailCode(registerDTO.getEmailUuid(), registerDTO.getEmailCode());
        AssertUtils.isTrue(checkEmailCode, "邮箱验证码错误");
    }
}
