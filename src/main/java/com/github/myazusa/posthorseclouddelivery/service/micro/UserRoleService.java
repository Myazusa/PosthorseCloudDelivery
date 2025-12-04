package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserRoleEnum;
import com.github.myazusa.posthorseclouddelivery.mapper.RoleMapper;
import com.github.myazusa.posthorseclouddelivery.mapper.UserRoleMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.RoleDAO;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserRoleDAO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    public boolean verifyRole(UUID userUuid, UserRoleEnum role) {
        // 联表验证用户权限
        var wrapper = new MPJLambdaWrapper<UserRoleDAO>();

        wrapper.selectAll(UserRoleDAO.class)
                .eq(UserRoleDAO::getUserUuid, userUuid)
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

    @Transactional
    public void removeRoles(UUID userUuid, List<UserRoleEnum> roleEnums) {
        for (UserRoleEnum roleEnum : roleEnums) {
            var wrapper = new LambdaQueryWrapper<RoleDAO>().select(RoleDAO::getUuid).eq(RoleDAO::getName, roleEnum.getUserRoleString());
            List<Object> objs = roleMapper.selectObjs(wrapper);
            var uuid = UuidCreator.fromString((String) objs.getFirst());
            var wrapper2 = new LambdaQueryWrapper<UserRoleDAO>().eq(UserRoleDAO::getUserUuid, userUuid).eq(UserRoleDAO::getRoleUuid, uuid);
            userRoleMapper.delete(wrapper2);
        }
    }

    public List<RoleDAO> queryRoles(UUID userUuid) {
        var wrapper = new LambdaQueryWrapper<UserRoleDAO>();
        wrapper.select(UserRoleDAO::getRoleUuid)
                .eq(UserRoleDAO::getUserUuid, userUuid);
        List<Object> objs = userRoleMapper.selectObjs(wrapper);

        var list = objs.stream().map(role->UuidCreator.fromString((String) role));
        return roleMapper.selectList(new LambdaQueryWrapper<RoleDAO>().in(RoleDAO::getUuid,list));
    }
}
