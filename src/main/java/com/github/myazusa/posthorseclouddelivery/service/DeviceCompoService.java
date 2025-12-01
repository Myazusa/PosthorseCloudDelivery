package com.github.myazusa.posthorseclouddelivery.service;

import com.github.myazusa.posthorseclouddelivery.model.dto.AddDeviceRequestDTO;
import com.github.myazusa.posthorseclouddelivery.service.micro.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeviceCompoService {
    private final DeviceService deviceService;

    @Autowired
    public DeviceCompoService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 添加设备
     * @param addDeviceRequestDTO
     * @return 返回设备的uuid
     */
    public UUID addDevice(AddDeviceRequestDTO addDeviceRequestDTO){
        return deviceService.addDevice(addDeviceRequestDTO.getDeviceName(),addDeviceRequestDTO.getLatitude(),addDeviceRequestDTO.getLongitude(),addDeviceRequestDTO.getVerifyPassword());
    }
}
