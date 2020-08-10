package com.dbTool.service.impl;

import com.dbTool.UserHolder;
import com.dbTool.dao.sys.MenuDao;
import com.dbTool.domain.DatabaseUserInfo;
import com.dbTool.domain.Menu;
import com.dbTool.service.MenuService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

  @Autowired
  UserHolder userHolder;

  @Autowired
  MenuDao menuDao;


  @Override
  public List<Menu> getMenuByType(int type) {
    DatabaseUserInfo user = userHolder.getUserInfo();
    List<Menu> menus = new ArrayList<>();

    if(type == 0){
      //allmenu
      if(user.getLevel() == 9){
        menus = menuDao.queryAdminAllParentMenusbyUserId(user.getId());
      }else{
        menus = menuDao.queryAllParentMenusByUserId(user.getId());
      }

      if(CollectionUtils.isNotEmpty(menus)){

        //所有菜单需要查询出其子菜单
        for (Menu menu : menus) {
          List<Menu> childMenu = null;
          if(user.getLevel() == 9 ){
            childMenu = menuDao.queryAdminAllChildMenus(user.getId(),menu.getId());
          }else{
            childMenu = menuDao.queryAllChildMenus(user.getId(),menu.getId());
          }
          if(CollectionUtils.isNotEmpty(childMenu)){
            menu.setChilds(childMenu);
          }
        }
      }
    }else if(type == 1){
      //desktopmenu
      if(user.getLevel() == 9){
        menus = menuDao.queryAdminDeskMenusbyUserId(user.getId());
      }else{
        menus = menuDao.queryDeskMenusbyUserId(user.getId());
      }
    }
    return menus;
  }

  @Override
  public List<Menu> getMenuList(int page, int limit) {
    DatabaseUserInfo user = userHolder.getUserInfo();
    List<Menu> menus = new ArrayList<>();
    //menulist
    if(user.getLevel() == 9){
      menus = menuDao.queryAdminMenuListByPage(user.getId(),(page-1)*limit,limit);
    }else{
      menus = menuDao.queryMenuListByPage(user.getId(),(page-1)*limit,limit);
    }

    return menus;
  }

  @Override
  public int getMenuCount() {
    DatabaseUserInfo user = userHolder.getUserInfo();
    if(user.getLevel() == 9){
      return menuDao.queryAdminMenuCount(user.getId());
    }else{
      return menuDao.queryMenuCount(user.getId());
    }
  }

  @Override
  public Menu getMenuById(int id) {
    DatabaseUserInfo user = userHolder.getUserInfo();
    return menuDao.getMenuById(id);
  }

  @Override
  public List<Menu> getParentMenu() {
    DatabaseUserInfo user = userHolder.getUserInfo();
    List<Menu> menus = new ArrayList<>();

    if(user.getLevel() == 9){
      menus = menuDao.queryAdminAllEffectParentMenusbyUserId(user.getId());
    }else{
      menus = menuDao.queryAllEffectParentMenusByUserId(user.getId());
    }
    return menus;
  }

  @Override
  public void saveMenu(Menu menu) {
    DatabaseUserInfo userInfo = userHolder.getUserInfo();
    if(!(menu.getIsPublic() || menu.getIsNecessary())){
      menu.setUserId(userInfo.getId());
    }else{
      menu.setUserId(-1);
    }
    if(menu.getId() > 0){
      //update
      menuDao.updateMenu(menu);
    }else{
      //insert
      menuDao.insertMenu(menu);
    }
  }

  @Override
  public void deleteMenu(int id) {
    DatabaseUserInfo userInfo = userHolder.getUserInfo();
    if(userInfo.getLevel() == 9){
      menuDao.adminDelete(id);
    }else{
      menuDao.delete(id,userInfo.getId());
    }
  }



  @Override
  public void deleteMenus(List<Integer> ids) {
    DatabaseUserInfo userInfo = userHolder.getUserInfo();
    if(userInfo.getLevel() == 9){
      menuDao.adminDeleteMenus(ids);
    }else{
      menuDao.deleteMenus(ids,userInfo.getId());
    }
  }

  @Override
  public List<Menu> getAllMenuByRole(int roleId) {
    List<Menu> allSysMenus = menuDao.queryParentAllSysMenu();

    for (Menu menu : allSysMenus) {
      List<Menu> menus = menuDao.queryChildAllSysMenu(menu.getId());
      menu.setChilds(menus);
    }
    if(roleId == 0){
      return allSysMenus;
    }

    List<Menu> roleSysMenus = menuDao.queryParentSysMenuByRoleIdList(roleId);
    for (Menu menu : roleSysMenus) {
      List<Menu> menus = menuDao.queryChildSysMenuByRoleIdList(roleId,menu.getId());
      menu.setChilds(menus);

      for (Menu sysMenu : allSysMenus) {
        if(menu.getId() == sysMenu.getId()){
          sysMenu.setRoleSelect(true);
          for (Menu childMenu : menus ) {
            for (Menu sysyChildMenu : sysMenu.getChilds()) {
              if(childMenu.getId() == sysyChildMenu.getId()){
                sysyChildMenu.setRoleSelect(true);
              }
            }
          }
        }
      }
    }

    return allSysMenus;
  }

  @Override
  public int updateRoleMenus(int roleId ,List<Integer> idList) {
//    int updateCount = 0;
//    if(CollectionUtils.isEmpty(idList)){
//      updateCount = menuDao.deteleAllSelectMenusByRoleId(roleId);
//    }else{
//      List<Menu> allSelectMenuBeforeUpdate = menuDao.queryAllSysMenuByRoleIdList(roleId);
//      if(CollectionUtils.isEmpty(allSelectMenuBeforeUpdate)){
//        updateCount = menuDao.insertRoleMenu(rolId,idList);
//      }else{
//        List<Integer> deleteSelectMenuids = new ArrayList<>();
//        List<Integer> allSelectMenuIdsBeforeUpdate = allSelectMenuBeforeUpdate.stream().map(menu -> {
//          return menu.getId();
//        }).collect(Collectors.toList());
//        List<>
//        idList.removeAll(allSelectMenuIdsBeforeUpdate);
//      }
        int delete = menuDao.deteleAllSelectMenusByRoleId(roleId);
        if(CollectionUtils.isNotEmpty(idList)) {
          int insert = menuDao.insertRoleMenu(roleId,idList);
        }

        return delete;

  }


}
