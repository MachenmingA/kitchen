package com.mykitchen.mapper;

import com.mykitchen.entity.OperationLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OperationLogMapper {
    @Select("SELECT * FROM operation_log ORDER BY create_time DESC LIMIT #{limit}")
    List<OperationLog> findRecent(Long limit);

    @Select("SELECT * FROM operation_log WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{limit}")
    List<OperationLog> findByUserId(@Param("userId") Long userId, @Param("limit") Long limit);

    @Insert("INSERT INTO operation_log(user_id, operation, method, url, ip, create_time) " +
            "VALUES(#{userId}, #{operation}, #{method}, #{url}, #{ip}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);
}
