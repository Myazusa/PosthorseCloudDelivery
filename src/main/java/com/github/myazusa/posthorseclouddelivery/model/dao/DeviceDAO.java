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
@TableName("device")
public class DeviceDAO {
    private UUID uuid;

    private LocalDateTime lastOnline;

    private double latitude;
    private double longitude;

    // 以下字段插入时数据库自动填充，可以不赋值
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private String deviceName;

}
