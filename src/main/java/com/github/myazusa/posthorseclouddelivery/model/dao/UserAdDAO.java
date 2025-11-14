package com.github.myazusa.posthorseclouddelivery.model.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
@TableName("user_ad")
public class UserAdDAO {
    private UUID userUuid;
    private UUID adUuid;
}
