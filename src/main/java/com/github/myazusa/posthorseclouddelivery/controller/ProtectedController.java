package com.github.myazusa.posthorseclouddelivery.controller;

import com.github.myazusa.posthorseclouddelivery.model.dto.InformationResponseDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.ListFilesRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UploadFilesRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserAuthRequestDTO;
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

    @Autowired
    public ProtectedController(JwtAuthCompoService jwtAuthCompoService, UserRepositoryCompoService userRepositoryCompoService) {
        this.jwtAuthCompoService = jwtAuthCompoService;
        this.userRepositoryCompoService = userRepositoryCompoService;
    }

    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(Authentication authentication, @RequestBody UserAuthRequestDTO userAuthRequestDTO){
        jwtAuthCompoService.resetPassword(authentication,userAuthRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("变更成功"));
    }

    @PostMapping("/create_repository")
    public ResponseEntity<?> createRepository(Authentication authentication){
        userRepositoryCompoService.createRepository(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("创建文件夹成功"));
    }

    @PostMapping("/upload_files")
    public ResponseEntity<?> uploadFile(Authentication authentication, @RequestBody UploadFilesRequestDTO uploadFilesRequestDTO){
        userRepositoryCompoService.saveFiles(authentication,uploadFilesRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("上传成功"));
    }

    @PostMapping("/list_files")
    public ResponseEntity<?> listFiles(Authentication authentication, @RequestBody ListFilesRequestDTO listFilesRequestDTO){
        var data = userRepositoryCompoService.listFiles(authentication,listFilesRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("获取文件列表成功").setData(data));
    }
}
