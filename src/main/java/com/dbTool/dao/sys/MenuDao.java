package com.dbTool.dao.sys;

import com.dbTool.domain.Menu;

import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface MenuDao {

  String MENU_SQL="select * from db_tools_menus where (id in (select menu_id from menu_auth_manage where group_id in (select group_id from user_group where user_id = #{userId})) or user_id = #{userId} or is_public = 1) ";

  String ADMIN_MENU_SQL="select * from db_tools_menus where (user_id = -1 or user_id = #{userId}) ";

  String ORDER_BY = " order by is_necessary desc , sort asc";

  String COL = "title, name, page_url, icon, open_type, parent_id, is_desk, user_id, is_necessary, is_public";

  @Select(MENU_SQL + " and parent_id = 0" + ORDER_BY)
  List<Menu> queryAllParentMenusByUserId(@Param("userId") long userId);

  @Select(ADMIN_MENU_SQL + " and parent_id = 0" + ORDER_BY)
  List<Menu> queryAdminAllParentMenusbyUserId(@Param("userId") long userId);

  @Select(MENU_SQL + " and parent_id = 0 and ( page_url is null or page_url = '' )" + ORDER_BY)
  List<Menu> queryAllEffectParentMenusByUserId(@Param("userId") long userId);

  @Select(ADMIN_MENU_SQL + " and parent_id = 0 and ( page_url is null or page_url = '' )" + ORDER_BY)
  List<Menu> queryAdminAllEffectParentMenusbyUserId(@Param("userId") long userId);


  @Select(MENU_SQL + " and is_desk = 1" + ORDER_BY)
  List<Menu> queryDeskMenusbyUserId(@Param("userId") long userId);

  @Select(ADMIN_MENU_SQL + " and is_desk = 1" + ORDER_BY)
  List<Menu> queryAdminDeskMenusbyUserId(@Param("userId") long userId);


  @Select(MENU_SQL + " and parent_id = #{menuId}" + ORDER_BY)
  List<Menu> queryAllChildMenus(@Param("userId") long userId, @Param("menuId") int menuId);

  @Select(ADMIN_MENU_SQL + " and parent_id = #{menuId}" + ORDER_BY)
  List<Menu> queryAdminAllChildMenus(@Param("userId") long userId, @Param("menuId") int menuId);

  @Select(MENU_SQL + ORDER_BY + " limit #{start} , #{limit}")
  List<Menu> queryMenuListByPage(@Param("userId") long userId, @Param("start") int start, @Param("limit") int limit);

  @Select(ADMIN_MENU_SQL + ORDER_BY + " limit #{start} , #{limit}")
  List<Menu> queryAdminMenuListByPage(@Param("userId") long userId, @Param("start") int start, @Param("limit") int limit);

  @Select("select count(1) from db_tools_menus where (id in (select menu_id from menu_auth_manage where group_id in (select group_id from user_group where user_id = #{userId})) or user_id = #{userId} or is_public = 1) ")
  int queryMenuCount(@Param("userId") long userId);

  @Select("select count(1) from db_tools_menus where (user_id = -1 or user_id = #{userId}) ")
  int queryAdminMenuCount(@Param("userId") long userId);

  @Select("select * from db_tools_menus where id = #{id}")
  Menu getMenuById(@Param("id") int id);

  @Update("update db_tools_menus set title = #{title} , name = #{name}," +
      " page_url = #{pageUrl}, icon = #{icon}, open_type = #{openType}," +
      "is_desk = #{isDesk}, is_necessary = #{isNecessary}, is_public = #{isPublic}," +
      " parent_id = #{parentId} where id=#{id}")
  void updateMenu(Menu menu);


  @Insert(" insert into db_tools_menus (" + COL + ") values (" +
      "#{title},#{name},#{pageUrl},#{icon},#{openType},parent_id = #{parentId}," +
      "#{isDesk},#{userId},#{isNecessary},#{isPublic})")
  void insertMenu(Menu menu);

  @Delete("delete from db_tools_menus where id= #{id}")
  void adminDelete(@Param("id") int id);

  @Delete("delete from db_tools_menus where id = #{id} and user_id = #{userId}")
  void delete(@Param("id") int id, @Param("userId") long userId);

  @Delete("<script>"
      + "delete from db_tools_menus where id in "
      + "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'>"
      + "#{item}"
      + "</foreach>"
      + "</script>")
  void adminDeleteMenus(@Param("ids") List<Integer> ids);

  @Delete("<script>"
      + "delete from db_tools_menus where user_id = #{userId} and id in "
      + "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'>"
      + "#{item}"
      + "</foreach>"
      + "</script>")
  void deleteMenus(@Param("ids") List<Integer> ids, @Param("userId") long userId);

  @Select("select * from db_tools_menus where is_necessary = 1 and id in("
      + "select distinct menu_id from menu_auth_manage where group_id = #{roleId}) and parent_id=0 and is_public = 0")
  List<Menu> queryParentSysMenuByRoleIdList(@Param("roleId") int roleId);

  @Select("select * from db_tools_menus where is_necessary = 1 and parent_id=0 and is_public = 0")
  List<Menu> queryParentAllSysMenu();


  @Select("select * from db_tools_menus where is_necessary = 1 and id in("
      + "select distinct menu_id from menu_auth_manage where group_id = #{roleId}) and parent_id=#{parentId} and is_public = 0")
  List<Menu> queryChildSysMenuByRoleIdList(@Param("roleId") int roleId, @Param("parentId") int parentId);

  @Select("select * from db_tools_menus where is_necessary = 1 and parent_id=#{parentId} and is_public = 0")
  List<Menu> queryChildAllSysMenu(@Param("parentId") int parentId);

  @Select("select * from db_tools_menus where is_necessary = 1 and id in("
      + "select distinct menu_id from menu_auth_manage where group_id = #{roleId}) and is_public = 0")
  List<Menu> queryAllSysMenuByRoleIdList(@Param("roleId") int roleId);


  @Delete("delete from menu_auth_manage where group_id = #{roleId} ")
  int deteleAllSelectMenusByRoleId(@Param("roleId") int roleId);

  @Insert("<script>"
      + "insert into menu_auth_manage ( menu_id,group_id) values "
      + "<foreach item='item' index='index' collection='idList' separator=',' >"
      + "(#{item},#{roleId})"
      + "</foreach>"
      + "</script>")
  int insertRoleMenu(@Param("roleId") int rolId,@Param("idList") List<Integer> idList);


}
