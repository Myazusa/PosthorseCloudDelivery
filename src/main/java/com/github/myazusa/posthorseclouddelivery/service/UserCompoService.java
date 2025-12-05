package com.github.myazusa.posthorseclouddelivery.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.SortOrderEnum;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserRoleEnum;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserSortByEnum;
import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.core.exception.InvalidParamException;
import com.github.myazusa.posthorseclouddelivery.model.dao.RoleDAO;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserDAO;
import com.github.myazusa.posthorseclouddelivery.model.dto.*;
import com.github.myazusa.posthorseclouddelivery.service.micro.UserRoleService;
import com.github.myazusa.posthorseclouddelivery.service.micro.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCompoService {
    private final UserRoleService userRoleService;
    private final UserService userService;

    @Autowired
    public UserCompoService(UserRoleService userRoleService, UserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
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
            if (userUuidRequestDTO.getIsQueryOthers() == null) throw new InvalidParamException("没有传入所需的isQueryOthers参数");
            if(userUuidRequestDTO.getIsQueryOthers()) {
                return userRoleService.queryRoles(UuidCreator.fromString(userUuidRequestDTO.getUserUuid()));
            }
        }
        return userRoleService.queryRoles(user.getUuid());
    }

    /**
     * 查询用户方法，管理员用，不是查用户信息而是列出所有用户
     * @param authentication
     * @param listUserRequestDTO
     * @return
     */
    public List<UserDAO> queryUsers(Authentication authentication, ListUserRequestDTO listUserRequestDTO){
        var user = (UserDetailsDTO)authentication.getPrincipal();
        if (!userRoleService.verifyRole(user.getUuid(),UserRoleEnum.queryOthers)) throw new AuthUserException("该账号无权限查询用户");

        return userService.queryUsers(
                listUserRequestDTO.getPageNumber(),
                listUserRequestDTO.getPageSize(),
                SortOrderEnum.fromString(listUserRequestDTO.getSortOrder()),
                UserSortByEnum.fromString(listUserRequestDTO.getSortBy()),
                listUserRequestDTO.getKeyword()
        );
    }

    /**
     * 查询用户信息的方法，管理和用户通用
     * @param authentication
     * @param userUuidRequestDTO 如果是管理身份查询则需要这个
     * @return
     */
    public UserInfoResponseDTO queryUserInfo(Authentication authentication, UserUuidRequestDTO userUuidRequestDTO) {
        var user = (UserDetailsDTO)authentication.getPrincipal();

        UserDAO dao;
        if (userRoleService.verifyRole(user.getUuid(),UserRoleEnum.queryOthers)) {
            if (userUuidRequestDTO.getIsQueryOthers() == null) throw new InvalidParamException("没有传入所需的isQueryOthers参数");
            if (userUuidRequestDTO.getIsQueryOthers()) {
                dao = userService.queryUserInfo(UuidCreator.fromString(userUuidRequestDTO.getUserUuid()));
            }else {
                dao = userService.queryUserInfo(user.getUuid());
            }
        }else {
            dao = userService.queryUserInfo(user.getUuid());
        }

        return new UserInfoResponseDTO().setUuid(dao.getUuid())
                .setUsername(dao.getUsername())
                .setPhone(dao.getPhone())
                .setCreateTime(dao.getCreateTime())
                .setEnabled(dao.getEnabled());
    }
}
