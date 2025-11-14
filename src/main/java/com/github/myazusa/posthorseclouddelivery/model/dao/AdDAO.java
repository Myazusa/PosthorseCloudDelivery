package com.github.myazusa.posthorseclouddelivery.model.dao;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
@TableName("ad")
public class AdDAO {
    private UUID uuid;
    private String title;
    private String type;
    private String filePath;
    private Long fileSize;

    // 以下字段插入时数据库自动填充，可以不赋值
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private Integer duration;
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private Integer priority;
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private LocalDateTime createdAt;
}
