package com.dbTool.dao.sys;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserGroupDao {

  @Select("select group_id from user_group where user_id = #{userId}")
  List<Integer> queryUserRoleIdListByUserId(@Param("userId") long userId);

  @Insert("insert into user_group (user_id,group_id) values (#{userId},#{groupId})")
  int insertUserGroup(@Param("userId") long userId, @Param("groupId") int groupId);
}
