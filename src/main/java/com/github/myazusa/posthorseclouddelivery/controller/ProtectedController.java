package com.github.myazusa.posthorseclouddelivery.controller;

import com.github.myazusa.posthorseclouddelivery.model.dto.*;
import com.github.myazusa.posthorseclouddelivery.service.AuthenticatorCompoService;
import com.github.myazusa.posthorseclouddelivery.service.DeviceCompoService;
import com.github.myazusa.posthorseclouddelivery.service.JwtAuthCompoService;
import com.github.myazusa.posthorseclouddelivery.service.UserRepositoryCompoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

// 这是受保护的接口
@Controller
@RequestMapping("/api/protected")
public class ProtectedController {
    private final JwtAuthCompoService jwtAuthCompoService;
    private final UserRepositoryCompoService userRepositoryCompoService;
    private final AuthenticatorCompoService authenticatorCompoService;
    private final DeviceCompoService deviceCompoService;

    @Autowired
    public ProtectedController(JwtAuthCompoService jwtAuthCompoService, UserRepositoryCompoService userRepositoryCompoService, AuthenticatorCompoService authenticatorCompoService, DeviceCompoService deviceCompoService) {
        this.jwtAuthCompoService = jwtAuthCompoService;
        this.userRepositoryCompoService = userRepositoryCompoService;
        this.authenticatorCompoService = authenticatorCompoService;
        this.deviceCompoService = deviceCompoService;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(Authentication authentication, @RequestBody UserAuthRequestDTO userAuthRequestDTO){
        jwtAuthCompoService.resetPassword(authentication,userAuthRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("变更成功"));
    }

    @PostMapping("/create-repository")
    public ResponseEntity<?> createRepository(Authentication authentication){
        userRepositoryCompoService.createRepository(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("创建文件夹成功"));
    }

    @PostMapping("/upload-files")
    public ResponseEntity<?> uploadFiles(Authentication authentication, @RequestBody UploadFilesRequestDTO uploadFilesRequestDTO){
        userRepositoryCompoService.saveFiles(authentication,uploadFilesRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("上传文件成功"));
    }

    @PostMapping("/list-files")
    public ResponseEntity<?> listFiles(Authentication authentication, @RequestBody ListFilesRequestDTO listFilesRequestDTO){
        var data = userRepositoryCompoService.listFiles(authentication,listFilesRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("获取文件列表成功").setData(data));
    }

    @PostMapping("/delete-files")
    public ResponseEntity<?> deleteFiles(Authentication authentication,@RequestBody DeleteFilesRequestDTO deleteFilesRequestDTO){
        userRepositoryCompoService.deleteFiles(authentication,deleteFilesRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("删除文件成功"));
    }

    @PostMapping("/query-authenticator")
    public ResponseEntity<?> queryAuthenticator(Authentication authentication){
        var dto = authenticatorCompoService.queryPassword(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("获取验证器密码成功").setData(dto));
    }

    @PostMapping("/bind-device")
    public ResponseEntity<?> bindDevice(Authentication authentication,BindDeviceRequestDTO bindDeviceRequestDTO){
        deviceCompoService.bindDevice(authentication,bindDeviceRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("绑定成功"));
    }
}
