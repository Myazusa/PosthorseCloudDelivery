package com.github.myazusa.posthorseclouddelivery.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserRoleEnum;
import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.model.dao.RoleDAO;
import com.github.myazusa.posthorseclouddelivery.model.dto.AlterRoleRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserDetailsDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserUuidRequestDTO;
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
     * @param alterRoleRequestDTO
     */
    public void addRoles(Authentication authentication, AlterRoleRequestDTO alterRoleRequestDTO) {
        var user = (UserDetailsDTO)authentication.getPrincipal();
        if(!userRoleService.verifyRole(user.getUuid(),UserRoleEnum.addRole)) throw new AuthUserException("该账户无权限给其他账户添加权限");

        List<UserRoleEnum> roleEnums = alterRoleRequestDTO.getRoles().stream().map(UserRoleEnum::fromString).toList();

        userRoleService.addRoles(UuidCreator.fromString(alterRoleRequestDTO.getUserUuid()),roleEnums);
    }

    /**
     * 管理员移除权限的方法
     * @param authentication
     * @param alterRoleRequestDTO
     */
    public void removeRoles(Authentication authentication, AlterRoleRequestDTO alterRoleRequestDTO) {
        var user = (UserDetailsDTO)authentication.getPrincipal();
        if(!userRoleService.verifyRole(user.getUuid(),UserRoleEnum.removeRole)) throw new AuthUserException("该账户无权限删除其他账户权限");

        List<UserRoleEnum> roleEnums = alterRoleRequestDTO.getRoles().stream().map(UserRoleEnum::fromString).toList();

        userRoleService.removeRoles(UuidCreator.fromString(alterRoleRequestDTO.getUserUuid()),roleEnums);
    }

    /**
     * 查询权限的方法，管理和用户共用
     * @param authentication
     * @param userUuidRequestDTO 当为管理员时，才使用这里面的uuid
     * @return
     */
    public List<RoleDAO> queryRoles(Authentication authentication, UserUuidRequestDTO userUuidRequestDTO) {
        var user = (UserDetailsDTO)authentication.getPrincipal();
        if (userRoleService.verifyRole(user.getUuid(),UserRoleEnum.queryOthersRole)) {
            return userRoleService.queryRoles(UuidCreator.fromString(userUuidRequestDTO.getUserUuid()));
        }
        return userRoleService.queryRoles(user.getUuid());
    }
}
