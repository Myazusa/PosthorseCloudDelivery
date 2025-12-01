package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.myazusa.posthorseclouddelivery.mapper.VerificationPasswordCacheMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.VerificationPasswordDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationPasswordCacheService {
    private final VerificationPasswordCacheMapper verificationPasswordCacheMapper;

    @Autowired
    public VerificationPasswordCacheService(VerificationPasswordCacheMapper verificationPasswordCacheMapper, VerificationPasswordCacheMapper verificationPasswordCacheMapper1) {
        this.verificationPasswordCacheMapper = verificationPasswordCacheMapper1;
    }

    public boolean verifyPassword(String inputPassword) {
        VerificationPasswordDAO cache = verificationPasswordCacheMapper.selectOne(
                new LambdaQueryWrapper<VerificationPasswordDAO>()
                        .eq(VerificationPasswordDAO::getVerifyPassword, inputPassword)
        );
        if (cache == null) return false;
        return !cache.getExpireAt().isBefore(LocalDateTime.now());
    }

    public VerificationPasswordDAO queryPassword() {
        return verificationPasswordCacheMapper.selectOne(
                new LambdaQueryWrapper<VerificationPasswordDAO>()
                        .eq(VerificationPasswordDAO::getId, 1L)
        );
    }

    public void updatePassword(){
        VerificationPasswordDAO cache = new VerificationPasswordDAO();
        int password = (int) (Math.random() * 900000) + 100000;
        // 设置过期时间为当前时间+30min
        cache.setId(1L).setVerifyPassword(String.valueOf(password)).setExpireAt(LocalDateTime.now().plusMinutes(30));
        verificationPasswordCacheMapper.insertOrUpdate(cache);
    }
}
