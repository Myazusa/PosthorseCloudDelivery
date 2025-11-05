package com.github.myazusa.posthorseclouddelivery.model.dao;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("verification_code_cache")
public class VerificationCodeDAO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String code;
    private LocalDateTime expireAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
