package com.dbTool.service;

import com.dbTool.domain.Menu;

import java.util.List;

public interface MenuService {

  List<Menu> getMenuByType(int type);


  List<Menu> getMenuList(int page,int limit) ;

  int getMenuCount();

  Menu getMenuById(int id);

  List<Menu> getParentMenu();

  void saveMenu(Menu menu);

  void deleteMenu(int id);

  void deleteMenus(List<Integer> ids);

  List<Menu> getAllMenuByRole(int roleId);

  int updateRoleMenus(int roleId,List<Integer> idList);
}
