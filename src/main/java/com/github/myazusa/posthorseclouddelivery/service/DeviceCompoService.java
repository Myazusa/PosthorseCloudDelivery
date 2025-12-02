package com.github.myazusa.posthorseclouddelivery.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.UserRoleEnum;
import com.github.myazusa.posthorseclouddelivery.core.exception.AuthUserException;
import com.github.myazusa.posthorseclouddelivery.core.exception.InvalidParamException;
import com.github.myazusa.posthorseclouddelivery.model.dto.AddDeviceRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.BindDeviceRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserDetailsDTO;
import com.github.myazusa.posthorseclouddelivery.service.micro.AuthUserDetailsService;
import com.github.myazusa.posthorseclouddelivery.service.micro.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeviceCompoService {
    private final DeviceService deviceService;
    private final AuthUserDetailsService authUserDetailsService;

    @Autowired
    public DeviceCompoService(DeviceService deviceService, AuthUserDetailsService authUserDetailsService) {
        this.deviceService = deviceService;
        this.authUserDetailsService = authUserDetailsService;
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
        if (!authUserDetailsService.verifyRole(user.getUuid(), UserRoleEnum.deviceBinder)) throw new AuthUserException("该账号没有绑定用户和设备的权限");

        UUID userUuid;
        UUID deviceUuid;
        try {
            userUuid = UuidCreator.fromString(bindDeviceRequestDTO.getUserUuid());
            deviceUuid = UuidCreator.fromString(bindDeviceRequestDTO.getDeviceUuid());
        } catch (Exception e) {
            throw new InvalidParamException("传入了无效的uuid，请检查字符串是否符合UUIDv7规范");
        }
        deviceService.bindDevice(userUuid,deviceUuid);
    }
}
