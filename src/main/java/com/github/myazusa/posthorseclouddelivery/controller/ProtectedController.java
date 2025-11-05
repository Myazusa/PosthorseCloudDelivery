package com.github.myazusa.posthorseclouddelivery.controller;

import com.github.myazusa.posthorseclouddelivery.model.dto.InformationResponseDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserAuthRequestDTO;
import com.github.myazusa.posthorseclouddelivery.service.JwtAuthCompoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

// 这是受保护的接口
@Controller
@RequestMapping("/api/protected")
public class ProtectedController {
    private final JwtAuthCompoService jwtAuthCompoService;

    @Autowired
    public ProtectedController(JwtAuthCompoService jwtAuthCompoService) {
        this.jwtAuthCompoService = jwtAuthCompoService;
    }

    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(HttpServletRequest servletRequest, @RequestBody UserAuthRequestDTO userAuthRequestDTO){
        String header = servletRequest.getHeader("Authorization");
        jwtAuthCompoService.resetPassword(header,userAuthRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("变更成功"));
    }

}
