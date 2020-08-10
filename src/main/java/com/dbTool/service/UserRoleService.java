package com.dbTool.service;

import com.dbTool.dao.sys.UserRoleDao;
import com.dbTool.domain.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {

  @Autowired
  UserRoleDao userRoleDao;

  public List<UserRole> getUserRoleList(int limit, int page) {
    return userRoleDao.getUserRoleList((page-1)*limit,limit);
  }

  public UserRole getUserRoleById(int id) {
    return userRoleDao.getUserRoleById(id);
  }

  public int updateUserRole(UserRole userRole) {
    return userRoleDao.updateUserRole(userRole);
  }

  public int insertUserRole(UserRole userRole){
    int id = userRoleDao.insertUserRole(userRole);
    return userRole.getId();
  }
}
