package com.dbTool.dao.sys;

import com.dbTool.domain.DatabaseUserFavorite;
import com.dbTool.domain.DatabaseUserInfo;
import com.dbTool.domain.DatabaseUserSqlHistory;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {


  /**user 操作相关 start*/
  @Select("select * from database_user_info where account = #{account}")
  DatabaseUserInfo queryUserInfoByAccount(@Param("account") String account);

  @Update("update database_user_info set last_login_time = #{lastLoginTime},last_login_ip = #{lastLoginIp} " +
      " where id = #{id}")
  void updateLastLoginData(DatabaseUserInfo user);


  @Update("update database_user_info set update_time= now(), last_update_password_time=now(), version=version+1, password = #{newPassword}" +
      " where id = #{userId}")
  int updateUserPassword(@Param("newPassword") String newPassword,@Param("userId") long userId);

  @Options(useGeneratedKeys = true)
  @Insert("insert into database_user_info (account, password, level, email, online_level, default_schema," +
      "after_login_redirect) values(#{account},#{password},#{level},#{email},#{onlineLevel}," +
      "#{defaultSchema},#{afterLoginRedirect})")
  int insertUser(DatabaseUserInfo databaseUserInfo);


  @Select("select * from database_user_info where id = #{id} and version = #{version}")
  DatabaseUserInfo getUserById(@Param("id") long id, @Param("version") long version);


  @Select("select id, account from database_user_info where account like concat ('%',#{key},'%')")
  List<DatabaseUserInfo> getUserNames(@Param("key") String key);

  @Select("select id, account, level, online_level, status, default_schema, email, last_update_password_time,last_login_time from database_user_info " +
      "where account like concat ('%',#{userName},'%') limit #{start} , #{limit}")
  List<DatabaseUserInfo> getUserInfo(@Param("start") int start, @Param("limit") int limit, @Param("userName") String userName);

  @Select("select id, account, level, online_level, status, default_schema, email, last_update_password_time,last_login_time from database_user_info " +
      " limit #{start} , #{limit}")
  List<DatabaseUserInfo> getAllUserInfo(@Param("start") int start, @Param("limit") int limit);


  @Select("SELECT COUNT(id) from database_user_info")
  int getTotalCountUsers();

  @Update("update database_user_info set account = #{account} , level = #{level} ," +
      "email = #{email} , online_level = #{onlineLevel} , default_schema = #{defaultSchema} ," +
      " status = #{status} where id = #{id}")
  int updateUserInfo(DatabaseUserInfo databaseUserInfo);

  @Update("update database_user_info set default_schema = #{defaultSchema} where id = #{userId}")
  int updateDefaultSchema(@Param("defaultSchema") String defaultSchema,@Param("userId") long userId);

  @Delete("<script>"
      + "delete from database_user_info where id in "
      + "<foreach item='item' index='index' collection='idList' open='(' separator=',' close=')'>"
      + "#{item}"
      + "</foreach>"
      + "</script>")
  int deleteUser(@Param("idList") List<Long> idList);

  @Update("update database_user_info set update_time= now(), last_update_password_time=now(), version=version+1, " +
      "password=#{password} where id=#{userId}")
  int resetUserPassword(@Param("userId") long userId, @Param("password") String password);


  /** user 操作相关 end*/




  /**  sql收藏 start  */

  @Insert("insert into database_user_favorite (user_id, favorite_sql_detail, name, client_ip) " +
      "values (#{userId}, #{favoriteSqlDetail},#{name}, #{clientIp} )")
  int saveUserFavoriteSql(DatabaseUserFavorite databaseUserFavorite);


  @Update(" update database_user_favorite set favorite_sql_detail = #{favoriteSqlDetail} ," +
      " name = #{name} , client_ip = #{clientIp} " +
      " where id = #{id} and user_id = #{userId}")
  int updateUserFavoriteSql(DatabaseUserFavorite databaseUserFavorite);


  @Select(" select * from database_user_favorite where user_id = #{userId}  order by id desc")
  List<DatabaseUserFavorite> queryDatabaseUserFavoriteSql(@Param("userId") long userId);

  @Delete("delete from  database_user_favorite where user_id=#{userId} and id=#{id}")
  int deleteUserFavoriteSql(@Param("id") long id, @Param("userId") long userId);


  @Insert("insert into database_user_favorite(user_id ,favorite_sql_detail ,name,delete_flag) " +
      "select #{shareUserId} as user_id, favorite_sql_detail ,name , delete_flag from database_user_favorite " +
      "where id = #{sqlId} and user_id = #{currentUserId}")
  int shareUserFavoriteSql(@Param("shareUserId") int shareUserId, @Param("sqlId") long sqlId ,@Param("currentUserId") long currentUserId);


  /** sql收藏 end */


  /** sqlHistory start */
  @Select("select id,sql_detail,create_time,env from database_user_sql_history where user_id=#{userId} order by id desc limit 500")
  List<DatabaseUserSqlHistory> queryDatabaseUserHistorySql(@Param("userId") long userId);


  @Select("select sql_detail from database_user_sql_history where id = #{sqlId}")
  String getSqlDetailById(@Param("sqlId") int sqlId);
  /** sqlHistory end */



  @Select("SELECT CAST(substring(name, #{namePrefixLength}) as SIGNED) + 1 as c from page_block" +
      " WHERE site_id=#{siteId} and menu_id=#{menuId} and " +
      " ${reg} " +
      " ORDER BY c DESC limit 1")
  Integer queryBlockIndex(@Param("siteId") long siteId, @Param("menuId") long menuId, @Param("reg") String reg, @Param("namePrefixLength") int namePrefixLength);


}
