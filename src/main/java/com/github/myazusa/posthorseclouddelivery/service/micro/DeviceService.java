package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.mapper.DeviceMapper;
import com.github.myazusa.posthorseclouddelivery.mapper.UserDeviceMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.DeviceDAO;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserDeviceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeviceService {
    private final DeviceMapper deviceMapper;
    private final UserDeviceMapper userDeviceMapper;
    private final VerificationPasswordCacheService verificationPasswordCacheService;

    @Autowired
    public DeviceService(DeviceMapper deviceMapper, UserDeviceMapper userDeviceMapper, VerificationPasswordCacheService verificationPasswordCacheService) {
        this.deviceMapper = deviceMapper;
        this.userDeviceMapper = userDeviceMapper;
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

    public void bindDevice(UUID userUuid, UUID deviceUuid) {
        userDeviceMapper.insert(new UserDeviceDAO().setUserUuid(userUuid).setDeviceUuid(deviceUuid));
    }
}
