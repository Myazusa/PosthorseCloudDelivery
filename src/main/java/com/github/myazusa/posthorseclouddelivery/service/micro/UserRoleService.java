package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserRoleEnum;
import com.github.myazusa.posthorseclouddelivery.mapper.RoleMapper;
import com.github.myazusa.posthorseclouddelivery.mapper.UserRoleMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.RoleDAO;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserRoleDAO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserRoleService {
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public UserRoleService(UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    public boolean verifyRole(UUID uuid, UserRoleEnum role) {
        // 联表验证用户权限
        var wrapper = new MPJLambdaWrapper<UserRoleDAO>();

        wrapper.selectAll(UserRoleDAO.class)
                .eq(UserRoleDAO::getUserUuid, uuid)
                .leftJoin(RoleDAO.class, RoleDAO::getUuid, UserRoleDAO::getRoleUuid)
                .eq(RoleDAO::getName,role.getUserRoleString());

        return userRoleMapper.exists(wrapper);
    }

    @Transactional
    public void addRoles(UUID userUuid, List<UserRoleEnum> roleEnums) {
        for (UserRoleEnum roleEnum : roleEnums) {
            var wrapper = new LambdaQueryWrapper<RoleDAO>().select(RoleDAO::getUuid).eq(RoleDAO::getName, roleEnum.getUserRoleString());
            List<Object> objs = roleMapper.selectObjs(wrapper);
            userRoleMapper.insert(new UserRoleDAO().setUserUuid(userUuid).setRoleUuid(UuidCreator.fromString((String) objs.getFirst())));
        }
    }
}
