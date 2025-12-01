package com.github.myazusa.posthorseclouddelivery.model.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.UUID;

@Data
@TableName("role")
public class RoleDAO {
    private UUID uuid;
    private String name;
    private String description;

}
