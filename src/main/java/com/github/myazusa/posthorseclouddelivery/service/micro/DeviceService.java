package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.core.exception.DataConflictException;
import com.github.myazusa.posthorseclouddelivery.mapper.DeviceMapper;
import com.github.myazusa.posthorseclouddelivery.mapper.UserDeviceMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.DeviceDAO;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserDeviceDAO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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

    @Transactional
    public void bindDevice(UUID userUuid, List<UUID> deviceUuidList) {
        // todo:插入失败后后面就停了，需要优化成不停的
        for (var deviceUuid : deviceUuidList) {
            try {
                userDeviceMapper.insert(new UserDeviceDAO().setUserUuid(userUuid).setDeviceUuid(deviceUuid));
            }catch (Exception e) {
                throw new DataConflictException("uuid为："+deviceUuid.toString()+"的设备已经被绑定过，回滚当前所有绑定操作");
            }
        }
    }

    public List<DeviceDAO> queryBoundDevice(UUID userUuid) {
        var wrapper = new MPJLambdaWrapper<UserDeviceDAO>()
                .select(UserDeviceDAO::getDeviceUuid)
                .eq(UserDeviceDAO::getUserUuid,userUuid);
        List<Object> objs = userDeviceMapper.selectObjs(wrapper);
        // todo：还需要考虑到筛选设备名检索
        if (CollectionUtils.isEmpty(objs)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<DeviceDAO> deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.in(DeviceDAO::getUuid, objs)
                .orderByDesc(DeviceDAO::getLastOnline);

        return deviceMapper.selectList(deviceWrapper);
    }

    public void unbindDevice(UUID userUuid, List<String> deviceUuidList) {
        var wrapper = new MPJLambdaWrapper<UserDeviceDAO>()
                .select(UserDeviceDAO::getDeviceUuid)
                .eq(UserDeviceDAO::getUserUuid,userUuid)
                .in(UserDeviceDAO::getDeviceUuid, deviceUuidList);

        userDeviceMapper.delete(wrapper);
    }
}
