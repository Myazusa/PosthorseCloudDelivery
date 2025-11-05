package com.github.myazusa.posthorseclouddelivery.model.dao;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("user")
public class UserDAO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;

    // 以下字段插入时数据库自动填充，可以不赋值
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private Boolean enabled;
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private LocalDateTime createTime;
}
