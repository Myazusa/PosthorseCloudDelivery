package com.github.myazusa.posthorseclouddelivery.mapper;

import com.github.myazusa.posthorseclouddelivery.model.dao.UserDeviceDAO;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDeviceMapper extends MPJBaseMapper<UserDeviceDAO> {
    // 可以继续添加自定义方法
}
