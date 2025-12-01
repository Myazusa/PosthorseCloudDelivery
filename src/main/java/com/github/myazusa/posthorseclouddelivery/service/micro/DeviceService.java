package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.mapper.DeviceMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.DeviceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeviceService {
    private final DeviceMapper deviceMapper;
    private final VerificationPasswordCacheService verificationPasswordCacheService;

    @Autowired
    public DeviceService(DeviceMapper deviceMapper, VerificationPasswordCacheService verificationPasswordCacheService) {
        this.deviceMapper = deviceMapper;
        this.verificationPasswordCacheService = verificationPasswordCacheService;
    }

    public UUID addDevice(String deviceName,double latitude,double longitude,String verifyPassword) {
        if (!verificationPasswordCacheService.verifyPassword(verifyPassword)) throw new AuthUserException("验证器密码不正确");
        var deviceUuid = UuidCreator.getTimeOrderedEpoch();
        if (deviceName == null) {
            deviceMapper.insert(new DeviceDAO().setUuid(deviceUuid).setLongitude(longitude).setLatitude(latitude));
        }
        return deviceUuid;
    }
}
