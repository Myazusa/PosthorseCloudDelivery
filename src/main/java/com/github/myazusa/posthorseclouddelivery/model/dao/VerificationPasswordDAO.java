package com.github.myazusa.posthorseclouddelivery.model.dao;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("verification_password_cache")
public class VerificationPasswordDAO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String verifyPassword;

    private LocalDateTime expireAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
