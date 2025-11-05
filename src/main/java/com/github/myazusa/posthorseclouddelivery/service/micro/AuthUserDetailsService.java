package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.myazusa.posthorseclouddelivery.core.exception.DataConflictException;
import com.github.myazusa.posthorseclouddelivery.mapper.UserAuthMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final UserAuthMapper userAuthMapper;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AuthUserDetailsService(UserAuthMapper userAuthMapper, PasswordEncoder passwordEncoder) {
        this.userAuthMapper = userAuthMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 验证是否不存在的用户名
        var user = userAuthMapper.selectOne(new QueryWrapper<UserDAO>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 获取用户的角色权限列表
        List<String> roles = userAuthMapper.getRolesByUsername(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                true, true, true,
                authorities
        );
    }

    public void register(String username, String password) {
        // 检查是否已存在用户
        var existing = userAuthMapper.selectOne(
                new LambdaQueryWrapper<UserDAO>().eq(UserDAO::getUsername, username)
        );
        if (existing != null) {
            throw new DataConflictException("数据冲突，用户名已存在");
        }

        // 无冲突则插入
        var user = new UserDAO().setUsername(username).setPassword(passwordEncoder.encode(password));
        userAuthMapper.insert(user);
    }

    public void resetPassword(String username, String password) {
        var user = userAuthMapper.selectOne(
                new LambdaQueryWrapper<UserDAO>().eq(UserDAO::getUsername, username)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(password));
        userAuthMapper.updateById(user);
    }
}
