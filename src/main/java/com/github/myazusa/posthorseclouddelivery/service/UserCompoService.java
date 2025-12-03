package com.github.myazusa.posthorseclouddelivery.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserRoleEnum;
import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.model.dto.AddRoleDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserDetailsDTO;
import com.github.myazusa.posthorseclouddelivery.service.micro.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCompoService {
    private final UserRoleService userRoleService;


    @Autowired
    public UserCompoService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * 管理员添加权限的方法
     * @param authentication 管理员账号的token
     * @param addRoleDTO
     */
    public void addRoles(Authentication authentication, AddRoleDTO addRoleDTO) {
        var user = (UserDetailsDTO)authentication.getPrincipal();
        if(!userRoleService.verifyRole(user.getUuid(),UserRoleEnum.addRole)) throw new AuthUserException("该账户无权限给其他账户添加权限");

        List<UserRoleEnum> roleEnums = addRoleDTO.getRoles().stream().map(UserRoleEnum::fromString).toList();

        userRoleService.addRoles(UuidCreator.fromString(addRoleDTO.getUserUuid()),roleEnums);
    }
}
