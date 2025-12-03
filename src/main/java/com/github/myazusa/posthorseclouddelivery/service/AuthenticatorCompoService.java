package com.github.myazusa.posthorseclouddelivery.service;

import com.github.myazusa.posthorseclouddelivery.core.enums.UserRoleEnum;
import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserDetailsDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.VerificationPasswordResponseDTO;
import com.github.myazusa.posthorseclouddelivery.service.micro.UserRoleService;
import com.github.myazusa.posthorseclouddelivery.service.micro.VerificationCodeCacheService;
import com.github.myazusa.posthorseclouddelivery.service.micro.VerificationPasswordCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthenticatorCompoService {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{6,20}$");
    private final VerificationCodeCacheService verificationCodeCacheService;
    private final VerificationPasswordCacheService verificationPasswordCacheService;
    private final UserRoleService userRoleService;

    @Autowired
    public AuthenticatorCompoService(VerificationCodeCacheService verificationCodeCacheService, VerificationPasswordCacheService verificationPasswordCacheService, UserRoleService userRoleService) {
        this.verificationCodeCacheService = verificationCodeCacheService;
        this.verificationPasswordCacheService = verificationPasswordCacheService;
        this.userRoleService = userRoleService;
    }

    /**
     * 校验手机号格式是否正确
     * @param phone
     * @return
     */
    public boolean isValidPhone(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 保存生成的code到postgres中，保存时设置code有效时间是5min
     * @param phone
     * @param code
     */
    public void saveCode(String phone, String code) {
        verificationCodeCacheService.saveCode(phone, code);
    }

    /**
     * 生成一个6位的随机code
     * @return
     */
    public String generateCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    /**
     * 调用第三方平台发送手机短信给用户
     * @param phone
     * @param code
     */
    public void sendSms(String phone,String code){
        // todo: 实现第三方服务发送短信
    }

    /**
     * 是否短时间内重复发送，限制是60s内只可以发送一次
     * @param phone
     * @return
     */
    public boolean canSend(String phone){
        return verificationCodeCacheService.canSend(phone);
    }

    /**
     * 查询postgres校验看有没有这个code
     * @param phone
     * @param inputCode
     * @return
     */
    public boolean verifyCode(String phone, String inputCode) {
        return verificationCodeCacheService.verifyCode(phone,inputCode);
    }


    /**
     * 查询验证器密码
     * @param authentication
     * @return
     */
    public VerificationPasswordResponseDTO queryPassword(Authentication authentication) {
        var user = (UserDetailsDTO) authentication.getPrincipal();
        // 验证用户是否有这个添加设备权限
        if(!userRoleService.verifyRole(user.getUuid(), UserRoleEnum.addDevice)) throw new AuthUserException("此账号没有添加设备权限");

        var verificationPasswordDAO = verificationPasswordCacheService.queryPassword();
        return new VerificationPasswordResponseDTO().setVerifyPassword(verificationPasswordDAO.getVerifyPassword())
                .setExpireAt(verificationPasswordDAO.getExpireAt())
                .setCreatedAt(verificationPasswordDAO.getCreatedAt());
    }

    /***
     * 定时任务自动清除过期的code，毫秒
     */
    @Scheduled(fixedDelay = 60000)
    public void cleanupExpired() {
        verificationCodeCacheService.cleanupExpired();
    }

    /***
     * 定时任务自动更新password
     */
    @Scheduled(fixedDelay = 1800000)
    public void updatePassword() {
        verificationPasswordCacheService.updatePassword();
    }
}
