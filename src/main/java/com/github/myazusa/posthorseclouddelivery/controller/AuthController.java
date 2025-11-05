package com.github.myazusa.posthorseclouddelivery.controller;

import com.github.myazusa.posthorseclouddelivery.model.dto.InformationResponseDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserAuthRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserRegisterRequestDTO;
import com.github.myazusa.posthorseclouddelivery.model.dto.UserVerificationCodeRequestDTO;
import com.github.myazusa.posthorseclouddelivery.service.JwtAuthCompoService;
import com.github.myazusa.posthorseclouddelivery.service.SmsCompoService;
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
    private final SmsCompoService smsCompoService;

    @Autowired
    public AuthController(JwtAuthCompoService jwtAuthCompoService, SmsCompoService smsCompoService) {
        this.jwtAuthCompoService = jwtAuthCompoService;
        this.smsCompoService = smsCompoService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody UserAuthRequestDTO userAuthRequestDTO){
        String token = jwtAuthCompoService.authUser(userAuthRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("登录成功").setData(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequestDTO userRegisterRequestDTO) {
        jwtAuthCompoService.registerUser(userRegisterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("注册成功"));
    }

    @PostMapping("/send_code")
    public ResponseEntity<?> sendCode(@RequestBody UserVerificationCodeRequestDTO userVerificationCodeRequestDTO) {
        String phone = userVerificationCodeRequestDTO.getUsername();

        // 检验手机号是否符合格式
        if (!smsCompoService.isValidPhone(phone)) {
            return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage("手机号格式错误"));
        }

        // 检验是否重复发送
        if (!smsCompoService.canSend(phone)) {
            return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage("发送过于频繁，请稍后再试"));
        }

        // 生成code并记录，调用第三方服务去发短信
        String code = smsCompoService.generateCode();
        smsCompoService.sendSms(phone, code);
        smsCompoService.saveCode(phone, code);
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success").setMessage("成功发送验证码"));
    }

    @PostMapping("/verify_code")
    public ResponseEntity<?> verifyCode(@RequestBody UserVerificationCodeRequestDTO userVerificationCodeRequestDTO) {
        if (userVerificationCodeRequestDTO.getCode() == null || userVerificationCodeRequestDTO.getCode().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage("未填写验证码"));
        }

        if (!smsCompoService.verifyCode(userVerificationCodeRequestDTO.getUsername(), userVerificationCodeRequestDTO.getCode())) {
            return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("error").setMessage("验证码错误或已过期"));
        }

        // todo: 这里需要走登录逻辑
        return ResponseEntity.status(HttpStatus.OK).body(new InformationResponseDTO().setState("success"));
    }
}
