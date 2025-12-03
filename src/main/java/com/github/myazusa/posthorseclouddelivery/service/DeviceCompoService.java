package com.github.myazusa.posthorseclouddelivery.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserRoleEnum;
import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.core.exception.InvalidParamException;
import com.github.myazusa.posthorseclouddelivery.model.dao.DeviceDAO;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserDeviceDAO;
import com.github.myazusa.posthorseclouddelivery.model.dto.AddDeviceRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.BindDeviceRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserDetailsDTO;
import com.github.myazusa.posthorseclouddelivery.service.micro.DeviceService;
import com.github.myazusa.posthorseclouddelivery.service.micro.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeviceCompoService {
    private final DeviceService deviceService;
    private final UserRoleService userRoleService;

    @Autowired
    public DeviceCompoService(DeviceService deviceService, UserRoleService userRoleService) {
        this.deviceService = deviceService;
        this.userRoleService = userRoleService;
    }

    /**
     * 添加设备
     * @param addDeviceRequestDTO
     * @return 返回设备的uuid
     */
    public UUID addDevice(AddDeviceRequestDTO addDeviceRequestDTO){
        return deviceService.addDevice(addDeviceRequestDTO.getDeviceName(),addDeviceRequestDTO.getLatitude(),addDeviceRequestDTO.getLongitude(),addDeviceRequestDTO.getVerifyPassword());
    }

    /**
     * 绑定用户和设备
     * @param authentication
     * @param bindDeviceRequestDTO
     */
    public void bindDevice(Authentication authentication, BindDeviceRequestDTO bindDeviceRequestDTO){
        var user = (UserDetailsDTO) authentication.getPrincipal();
        if (!userRoleService.verifyRole(user.getUuid(), UserRoleEnum.bindDevice)) throw new AuthUserException("该账号没有绑定用户和设备的权限");

        UUID userUuid;
        List<UUID> deviceUuidList;
        try {
            userUuid = UuidCreator.fromString(bindDeviceRequestDTO.getUserUuid());
            deviceUuidList = bindDeviceRequestDTO.getDeviceUuidList().stream().map(UuidCreator::fromString).toList();

        } catch (Exception e) {
            throw new InvalidParamException("传入了无效的uuid，请检查字符串是否符合UUIDv7规范");
        }
        deviceService.bindDevice(userUuid,deviceUuidList);
    }

    /**
     * 查询用户已绑定的设备列表
     * @param authentication
     * @return
     */
    public List<DeviceDAO> queryBoundDevice(Authentication authentication){
        var user = (UserDeviceDAO) authentication.getPrincipal();
        return deviceService.queryBoundDevice(user.getUserUuid());
    }

    /**
     * 解绑用户已绑定的设备列表，管理员和用户自己通用
     * @param authentication 传入用户的uuid
     * @param bindDeviceRequestDTO 如果用户的uuid是带有绑定设备权限的管理员，就需要传入要解绑的用户uuid
     */
    public void unbindDevice(Authentication authentication, BindDeviceRequestDTO bindDeviceRequestDTO) {
        var user = (UserDeviceDAO) authentication.getPrincipal();
        // 先验证是否有绑定设备权限，有的话就要提供要解绑的用户uuid
        if(userRoleService.verifyRole(user.getUserUuid(), UserRoleEnum.unbindDevice)){
            deviceService.unbindDevice(UuidCreator.fromString(bindDeviceRequestDTO.getUserUuid()),bindDeviceRequestDTO.getDeviceUuidList());
        }else {
            deviceService.unbindDevice(user.getUserUuid(),bindDeviceRequestDTO.getDeviceUuidList());
        }
    }
}
