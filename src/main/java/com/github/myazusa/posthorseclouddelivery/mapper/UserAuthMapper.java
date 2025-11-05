package com.github.myazusa.posthorseclouddelivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserDAO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserAuthMapper extends BaseMapper<UserDAO> {
    @Select("""
        SELECT r.name FROM role r JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}
    """)
    List<String> getRolesByUsername(Integer userId);
}
