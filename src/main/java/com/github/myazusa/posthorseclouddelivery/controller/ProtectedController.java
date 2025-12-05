package com.github.myazusa.posthorseclouddelivery.controller;

import com.github.myazusa.posthorseclouddelivery.model.dao.RoleDAO;
import com.github.myazusa.posthorseclouddelivery.model.dto.*;
import com.github.myazusa.posthorseclouddelivery.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

// 这是受保护的接口
@Controller
@RequestMapping("/api/protected")
public class ProtectedController {
    private final JwtAuthCompoService jwtAuthCompoService;
    private final RepositoryCompoService repositoryCompoService;
    private final AuthenticatorCompoService authenticatorCompoService;
    private final DeviceCompoService deviceCompoService;
    private final UserCompoService userCompoService;

    @Autowired
    public ProtectedController(JwtAuthCompoService jwtAuthCompoService, RepositoryCompoService repositoryCompoService, AuthenticatorCompoService authenticatorCompoService, DeviceCompoService deviceCompoService, UserCompoService userCompoService) {
        this.jwtAuthCompoService = jwtAuthCompoService;
        this.repositoryCompoService = repositoryCompoService;
        this.authenticatorCompoService = authenticatorCompoService;
        this.deviceCompoService = deviceCompoService;
        this.userCompoService = userCompoService;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(Authentication authentication, @RequestBody UserAuthRequestDTO userAuthRequestDTO){
        jwtAuthCompoService.resetPassword(authentication,userAuthRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("变更成功"));
    }

    @PostMapping("/create-repository")
    public ResponseEntity<?> createRepository(Authentication authentication){
        repositoryCompoService.createRepository(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("创建文件夹成功"));
    }

    @PostMapping("/upload-files")
    public ResponseEntity<?> uploadFiles(Authentication authentication, @RequestBody UploadFilesRequestDTO uploadFilesRequestDTO){
        repositoryCompoService.saveFiles(authentication,uploadFilesRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("上传文件成功"));
    }

    @PostMapping("/list-files")
    public ResponseEntity<?> listFiles(Authentication authentication, @RequestBody ListFilesRequestDTO listFilesRequestDTO){
        var data = repositoryCompoService.listFiles(authentication,listFilesRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("获取文件列表成功").setData(data));
    }

    @PostMapping("/delete-files")
    public ResponseEntity<?> deleteFiles(Authentication authentication,@RequestBody DeleteFilesRequestDTO deleteFilesRequestDTO){
        repositoryCompoService.deleteFiles(authentication,deleteFilesRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("删除文件成功"));
    }

    @PostMapping("/query-authenticator")
    public ResponseEntity<?> queryAuthenticator(Authentication authentication){
        var dto = authenticatorCompoService.queryPassword(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("获取验证器密码成功").setData(dto));
    }

    @PostMapping("/bind-device")
    public ResponseEntity<?> bindDevice(Authentication authentication,@RequestBody BindDeviceRequestDTO bindDeviceRequestDTO){
        deviceCompoService.bindDevice(authentication,bindDeviceRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("绑定成功"));
    }

    @PostMapping("/unbind-device")
    public ResponseEntity<?> unbindDevice(Authentication authentication,@RequestBody BindDeviceRequestDTO bindDeviceRequestDTO){
        deviceCompoService.unbindDevice(authentication,bindDeviceRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("解除绑定成功"));
    }

    @PostMapping("/query-bound-device")
    public ResponseEntity<?> queryBoundDevice(Authentication authentication,@RequestBody UserUuidRequestDTO userUuidRequestDTO){
        var data = deviceCompoService.queryBoundDevice(authentication,userUuidRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("查询账号绑定设备成功").setData(data));
    }


    @PostMapping("/add-roles")
    public ResponseEntity<?> addRoles(Authentication authentication,@RequestBody AlterRoleRequestDTO alterRoleRequestDTO){
        userCompoService.addRoles(authentication, alterRoleRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("添加权限成功"));
    }

    @PostMapping("/remove-roles")
    public ResponseEntity<?> removeRoles(Authentication authentication,@RequestBody AlterRoleRequestDTO alterRoleRequestDTO){
        userCompoService.removeRoles(authentication,alterRoleRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("移除权限成功"));
    }

    @PostMapping("/query-roles")
    public ResponseEntity<?> queryRoles(Authentication authentication,@RequestBody UserUuidRequestDTO userUuidRequestDTO){
        var data = userCompoService.queryRoles(authentication,userUuidRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("获取用户权限成功").setData(data));
    }

    @PostMapping("/query-users")
    public ResponseEntity<?> queryUsers(Authentication authentication,@RequestBody ListUserRequestDTO listUserRequestDTO){
        var data = userCompoService.queryUsers(authentication,listUserRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("获取用户列表成功").setData(data));
    }

    @PostMapping("/query-user-info")
    public ResponseEntity<?> queryUserInfo(Authentication authentication,@RequestBody UserUuidRequestDTO userUuidRequestDTO){
        var data = userCompoService.queryUserInfo(authentication,userUuidRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("获取用户信息成功").setData(data));
    }
}
