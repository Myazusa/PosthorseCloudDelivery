package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.myazusa.posthorseclouddelivery.core.enums.SortOrderEnum;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserSortByEnum;
import com.github.myazusa.posthorseclouddelivery.mapper.UserMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<UserDAO> queryUsers(Integer pageNumber, Integer pageSize, SortOrderEnum sortOrderEnum , UserSortByEnum userSortByEnum, String keyword) {
        Page<UserDAO> page = new Page<>(pageNumber, pageSize);

        var wrapper = new LambdaQueryWrapper<UserDAO>();
        // 如果有关键词，先搜索关键词
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(UserDAO::getPhone, keyword));
        }

        // 排序
        if (sortOrderEnum != null) {
            boolean isAsc = "asc".equalsIgnoreCase(sortOrderEnum.getSortOrderString());
            wrapper.orderBy(true, isAsc, getSortField(userSortByEnum));
        }else {
            wrapper.orderByDesc(UserDAO::getCreateTime);
        }
        Page<UserDAO> userDAOPage = userMapper.selectPage(page,wrapper);

        return new ArrayList<>(userDAOPage.getRecords());
    }

    public UserDAO queryUserInfo(UUID userUuid) {
        return userMapper.selectById(userUuid);
    }

    private SFunction<UserDAO, ?> getSortField(UserSortByEnum sortBy) {
        return switch (sortBy) {
            case uuid -> UserDAO::getUuid;
            case username -> UserDAO::getUsername;
            case createTime -> UserDAO::getCreateTime;
            case phone -> UserDAO::getPhone;
        };
    }
}
