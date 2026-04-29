package com.mykitchen.mapper;

import com.mykitchen.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {
    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{limit}")
    List<Notification> findByUserId(@Param("userId") Long userId, @Param("limit") Long limit);

    @Select("SELECT * FROM notification WHERE id = #{id}")
    Notification findById(Long id);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    int countUnread(Long userId);

    @Insert("INSERT INTO notification(user_id, type, title, content, is_read, create_time) " +
            "VALUES(#{userId}, #{type}, #{title}, #{content}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Update("UPDATE notification SET is_read = 1 WHERE id = #{id}")
    int markAsRead(Long id);

    @Update("UPDATE notification SET is_read = 1 WHERE user_id = #{userId}")
    int markAllAsRead(Long userId);

    @Delete("DELETE FROM notification WHERE id = #{id}")
    int delete(Long id);
}
