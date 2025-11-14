package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.exception.DataConflictException;
import com.github.myazusa.posthorseclouddelivery.mapper.UserAuthMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserDAO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserDetailsDTO;
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
        // 验证是否不存在的手机号
        var userDAO = userAuthMapper.selectOne(new QueryWrapper<UserDAO>().eq("phone", username));
        if (userDAO == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 获取用户的角色权限列表
        List<String> roles = userAuthMapper.getRolesByPhone(userDAO.getPhone());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UserDetailsDTO(
                userDAO.getUuid(),userDAO.getUsername(),userDAO.getPassword(),userDAO.getPhone(),List.of()
        );
    }

    public void register(String phone, String password) {
        // 检查是否已存在用户
        var existing = userAuthMapper.selectOne(
                new LambdaQueryWrapper<UserDAO>().eq(UserDAO::getPhone, phone)
        );
        if (existing != null) {
            throw new DataConflictException("数据冲突，手机号已存在");
        }

        // 无冲突则插入
        var user = new UserDAO().setUuid(UuidCreator.getTimeOrderedEpoch()).setPhone(phone).setPassword(passwordEncoder.encode(password));
        userAuthMapper.insert(user);
    }

    public void resetPassword(String phone, String password) {
        var user = userAuthMapper.selectOne(
                new LambdaQueryWrapper<UserDAO>().eq(UserDAO::getPhone, phone)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(password));
        userAuthMapper.updateById(user);
    }
}
