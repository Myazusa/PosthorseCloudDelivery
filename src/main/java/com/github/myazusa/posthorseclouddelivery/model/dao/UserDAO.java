package com.github.myazusa.posthorseclouddelivery.model.dao;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
@TableName("user")
public class UserDAO {
    private UUID uuid;
    private String phone;
    private String password;

    // 以下字段插入时数据库自动填充，可以不赋值
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private String username;
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private Boolean enabled;
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private LocalDateTime createTime;
}
