package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserRoleEnum;
import com.github.myazusa.posthorseclouddelivery.core.exception.DataConflictException;
import com.github.myazusa.posthorseclouddelivery.mapper.UserMapper;
import com.github.myazusa.posthorseclouddelivery.mapper.UserRoleMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.*;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserDetailsDTO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public AuthUserDetailsService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 验证是否不存在的手机号
        var userDAO = userMapper.selectOne(new QueryWrapper<UserDAO>().eq("phone", username));
        if (userDAO == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 获取用户的角色权限列表
        List<String> roles = userMapper.getRolesByPhone(userDAO.getPhone());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UserDetailsDTO(
                userDAO.getUuid(),userDAO.getUsername(),userDAO.getPassword(),userDAO.getPhone(),List.of()
        );
    }

    public void register(String phone, String password) {
        // 检查是否已存在用户
        var existing = userMapper.selectOne(
                new LambdaQueryWrapper<UserDAO>().eq(UserDAO::getPhone, phone)
        );
        if (existing != null) {
            throw new DataConflictException("数据冲突，手机号已存在");
        }

        // 无冲突则插入
        var user = new UserDAO().setUuid(UuidCreator.getTimeOrderedEpoch()).setPhone(phone).setPassword(passwordEncoder.encode(password));
        userMapper.insert(user);
    }

    public void resetPassword(String phone, String password) {
        var user = userMapper.selectOne(
                new LambdaQueryWrapper<UserDAO>().eq(UserDAO::getPhone, phone)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(password));
        userMapper.updateById(user);
    }


}
