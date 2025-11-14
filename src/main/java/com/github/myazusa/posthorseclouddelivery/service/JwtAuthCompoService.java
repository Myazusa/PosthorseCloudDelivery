package com.github.myazusa.posthorseclouddelivery.service;


import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.core.utils.JwtUtils;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserAuthRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserDetailsDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserRegisterRequestDTO;
import com.github.myazusa.posthorseclouddelivery.service.micro.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthCompoService {
    private final AuthenticationManager authenticationManager;
    private final AuthUserDetailsService authUserDetailsService;

    @Autowired
    public JwtAuthCompoService(AuthenticationManager authenticationManager, AuthUserDetailsService authUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.authUserDetailsService = authUserDetailsService;
    }

    /**
     * 用于登录验证的方法
     * @param userAuthRequestDTO 登录对象
     * @return 返回一个使用用户名生成的token
     */
    public String authUser(UserAuthRequestDTO userAuthRequestDTO){
        try {
            // 第一次验证用户，调用loadUserByUsername方法去数据库比对
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userAuthRequestDTO.getPhone(), userAuthRequestDTO.getPassword())
            );

            // 从数据库获取可靠的用户名生成token并返回
            UserDetailsDTO userDetailsDTO = (UserDetailsDTO) authentication.getPrincipal();
            return JwtUtils.generateToken(userDetailsDTO.getPhone(),userDetailsDTO.getUuid());
        } catch (AuthenticationException e) {
            throw new AuthUserException();
        }
    }

    /**
     * 用于注册的方法，没有返回如果插入失败直接返回异常响应
     * @param userRegisterRequestDTO 注册对象
     */
    public void registerUser(UserRegisterRequestDTO userRegisterRequestDTO){
        authUserDetailsService.register(userRegisterRequestDTO.getPhone(), userRegisterRequestDTO.getPassword());
    }


    /**
     * 用于重置密码的方法
     * @param authentication 传入token
     * @param userAuthRequestDTO 手机号和密码
     */
    public void resetPassword(Authentication authentication, UserAuthRequestDTO userAuthRequestDTO){
        UserDetailsDTO userDetailsDTO = (UserDetailsDTO) authentication.getPrincipal();
        String phone = userDetailsDTO.getUsername();
        if (phone == null || phone.isBlank()) {
            throw new AuthUserException("token解析出来的手机号为空");
        }
        if(!phone.equals(userAuthRequestDTO.getPhone())){
            throw new UsernameNotFoundException("传入的手机号与token解析出来的手机号不匹配");
        }
        authUserDetailsService.resetPassword(phone,userAuthRequestDTO.getPassword());
    }
}
