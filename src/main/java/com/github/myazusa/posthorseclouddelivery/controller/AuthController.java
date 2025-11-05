package com.github.myazusa.posthorseclouddelivery.controller;

import com.github.myazusa.posthorseclouddelivery.model.dto.InformationResponseDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserAuthRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserRegisterRequestDTO;
import com.github.myazusa.posthorseclouddelivery.service.JwtAuthCompoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtAuthCompoService jwtAuthCompoService;

    @Autowired
    public AuthController(JwtAuthCompoService jwtAuthCompoService) {
        this.jwtAuthCompoService = jwtAuthCompoService;
    }

    @PostMapping("/login")
    public ResponseEntity<InformationResponseDTO> userLogin(@RequestBody UserAuthRequestDTO userAuthRequestDTO){
        String token = jwtAuthCompoService.authUser(userAuthRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("登录成功").setData(token));
    }

    @PostMapping("/register")
    public ResponseEntity<InformationResponseDTO> register(@RequestBody UserRegisterRequestDTO userRegisterRequestDTO) {
        jwtAuthCompoService.registerUser(userRegisterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("注册成功"));
    }
}
