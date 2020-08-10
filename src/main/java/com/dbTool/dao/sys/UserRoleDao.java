package com.dbTool.dao.sys;

import com.dbTool.domain.UserRole;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserRoleDao {

  @Select("select * from role_group where id in (select group_id from user_group where user_id = #{userId})")
  List<UserRole> queryUserRoleLsitByUserId(@Param("userId") long userId);

  @Select("select * from role_group limit #{start},#{limit}")
  List<UserRole> getUserRoleList(@Param("start") int start,@Param("limit") int limit);

  @Select("select * from role_group where id = #{id}")
  UserRole getUserRoleById(@Param("id") int id);

  @Update("update role_group set role_name=#{userRole.roleName},description=#{userRole.description} where id=#{userRole.id}")
  int updateUserRole(@Param("userRole") UserRole userRole);

  @Options(useGeneratedKeys=true)
  @Insert("insert role_group (role_name,description) values (#{roleName},#{description})")
  int insertUserRole(UserRole userRole);
}
