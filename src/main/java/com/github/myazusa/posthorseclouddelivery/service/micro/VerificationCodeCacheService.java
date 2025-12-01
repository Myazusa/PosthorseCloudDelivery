package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.myazusa.posthorseclouddelivery.mapper.VerificationCodeCacheMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.VerificationCodeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationCodeCacheService {
    private final VerificationCodeCacheMapper verificationCodeCacheMapper;

    @Autowired
    public VerificationCodeCacheService(VerificationCodeCacheMapper verificationCodeCacheMapper) {
        this.verificationCodeCacheMapper = verificationCodeCacheMapper;
    }

    public void saveCode(String phone, String code) {
        VerificationCodeDAO cache = new VerificationCodeDAO();
        cache.setPhone(phone);
        cache.setCode(code);
        // 设置过期时间为当前时间+5min
        cache.setExpireAt(LocalDateTime.now().plusMinutes(5));
        verificationCodeCacheMapper.insert(cache);
    }

    public boolean verifyCode(String phone, String inputCode) {
        VerificationCodeDAO cache = verificationCodeCacheMapper.selectOne(
                new LambdaQueryWrapper<VerificationCodeDAO>()
                        .eq(VerificationCodeDAO::getPhone, phone)
                        .eq(VerificationCodeDAO::getCode, inputCode)
                        .orderByDesc(VerificationCodeDAO::getCreatedAt)
                        .last("LIMIT 1")
        );
        if (cache == null) return false;
        if (cache.getExpireAt().isBefore(LocalDateTime.now())) return false;
        // 用完删除
        verificationCodeCacheMapper.deleteById(cache.getId());
        return true;
    }

    public boolean canSend(String phone) {
        VerificationCodeDAO last = verificationCodeCacheMapper.selectOne(
                new LambdaQueryWrapper<VerificationCodeDAO>()
                        .eq(VerificationCodeDAO::getPhone, phone)
                        .orderByDesc(VerificationCodeDAO::getCreatedAt)
                        .last("LIMIT 1")
        );
        if (last == null) return true;
        return last.getCreatedAt().isBefore(LocalDateTime.now().minusSeconds(60));
    }

    public void cleanupExpired() {
        verificationCodeCacheMapper.delete(new LambdaQueryWrapper<VerificationCodeDAO>()
                .lt(VerificationCodeDAO::getExpireAt, LocalDateTime.now()));
    }
}
