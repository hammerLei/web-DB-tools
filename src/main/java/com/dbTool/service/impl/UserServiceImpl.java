package com.dbTool.service.impl;

import com.dbTool.constant.Constant;
import com.dbTool.UserHolder;
import com.dbTool.dao.sys.UserDao;
import com.dbTool.dao.sys.UserGroupDao;
import com.dbTool.dao.sys.UserRoleDao;
import com.dbTool.domain.DatabaseUserFavorite;
import com.dbTool.domain.DatabaseUserInfo;
import com.dbTool.domain.DatabaseUserSqlHistory;
import com.dbTool.domain.UserRole;
import com.dbTool.service.UserService;
import com.dbTool.util.StringUtil;
import com.dbTool.util.JacksonUtils;
import com.dbTool.util.MD5Support;
import com.dbTool.util.ReturnJacksonUtil;

import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserDao userDao;

  @Autowired
  UserHolder userHolder;

  @Autowired
  UserRoleDao userRoleDao;

  @Autowired
  UserGroupDao userGroupDao;

  @Override
  public DatabaseUserInfo getUserInfoByAccount(String accout){
    DatabaseUserInfo userInfo = userDao.queryUserInfoByAccount(accout);
    if(userInfo != null){
      List<UserRole> userRoleList = userRoleDao.queryUserRoleLsitByUserId(userInfo.getId());
      userInfo.setUserRoleList(userRoleList);
      return userInfo;
    }
    return null;
  }

  @Override
  public void updateLastLoginData(DatabaseUserInfo user){
    userDao.updateLastLoginData(user);
  }

  @Override
  public int saveUserFavoriteSql(String name,String statement){
    DatabaseUserFavorite databaseUserFavorite = new DatabaseUserFavorite();
    databaseUserFavorite.setName(name);
    databaseUserFavorite.setFavoriteSqlDetail(StringUtil.replceSingleQuot(statement));
    databaseUserFavorite.setClientIp(userHolder.getClientIp());
    databaseUserFavorite.setUserId(userHolder.getUserInfo().getId());
    return userDao.saveUserFavoriteSql(databaseUserFavorite);
  }

  @Override
  public int updateUserFavoriteSql(long id,String name,String statement){
    DatabaseUserFavorite databaseUserFavorite = new DatabaseUserFavorite();
    databaseUserFavorite.setId(id);
    databaseUserFavorite.setName(name);
    databaseUserFavorite.setFavoriteSqlDetail(StringUtil.replceSingleQuot(statement));
    databaseUserFavorite.setClientIp(userHolder.getClientIp());
    databaseUserFavorite.setUserId(userHolder.getUserInfo().getId());
    databaseUserFavorite.setUpdateTime(new Date());
    return userDao.updateUserFavoriteSql(databaseUserFavorite);
  }

  @Override
  public List<DatabaseUserFavorite> queryDatabaseUserFavoriteSql(){
    return userDao.queryDatabaseUserFavoriteSql(userHolder.getUserInfo().getId());
  }

  @Override
  public String queryDatabaseUserHistorySql(){
    try{
      List<DatabaseUserSqlHistory> list = userDao.queryDatabaseUserHistorySql(userHolder.getUserInfo().getId());
      return JacksonUtils.toJson(list);
    }catch (Exception e){
      return StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }
  }

  @Override
  public int deleteUserFavoriteSql(long id){
    return userDao.deleteUserFavoriteSql(id,userHolder.getUserInfo().getId());
  }


  @Override
  public String updateUserPassword(String password,String newPassword,String confirmPassword){
    String encPassword = MD5Support.hex(password, Constant.MD5KEY);

    if(!(newPassword.equals(confirmPassword))){
      return StringUtil.returnResultStr(Constant.ERROR,"2次输入的密码不匹配");
    }else if(!(userHolder.getUserInfo().getPassword().equals(encPassword))){
      return StringUtil.returnResultStr(Constant.ERROR,"原始密码不正确");
    }else {
      int result = userDao.updateUserPassword(MD5Support.hex(newPassword, Constant.MD5KEY),userHolder.getUserInfo().getId());
      if(result == 1){
        //更新session中user的密码位新密码
        userHolder.getUserInfo().setPassword(MD5Support.hex(newPassword, Constant.MD5KEY));
        return StringUtil.returnResultStr(Constant.SUCCESS,Constant.SUCCESS_RESULT);
      }
      return StringUtil.returnFailResultStr(Constant.ERROR,Constant.SERRVER_ERROR);
    }
  }

  @Override
  public String createUserSubmit(DatabaseUserInfo userInfo,int roleGroup) {
    DatabaseUserInfo databaseUserInfo = getUserInfoByAccount(userInfo.getAccount());
    if(databaseUserInfo!=null){
      return StringUtil.returnResultStr(Constant.ERROR,"用户名重复！");
    }else{
      int insert = userDao.insertUser(userInfo);
      userGroupDao.insertUserGroup(userInfo.getId(),roleGroup);
      if(insert == 1){
        return StringUtil.returnResultStr(Constant.SUCCESS,Constant.SUCCESS_RESULT);
      }
      return StringUtil.returnFailResultStr(Constant.ERROR,Constant.SERRVER_ERROR);
    }
  }

  @Override
  public DatabaseUserInfo getUserInfoById(long id,long version) {
    return userDao.getUserById(id,version);
  }

  @Override
  public String shareUserFavoriteSql(int userId, long sqlId){
    int result = userDao.shareUserFavoriteSql(userId, sqlId,userHolder.getUserInfo().getId());
    if(result == 1){
      return StringUtil.returnResultStr(Constant.SUCCESS,Constant.SUCCESS_RESULT);
    }else{
      return StringUtil.returnFailResultStr(Constant.ERROR,Constant.SERRVER_ERROR);
    }
  }

  @Override
  public String getUserNamesByKey(String key){
    try {
      List<DatabaseUserInfo> list = userDao.getUserNames(key);
      return ReturnJacksonUtil.resultOk(list);
    } catch (Exception e) {
      e.printStackTrace();
      return StringUtils.EMPTY;
    }
  }

  @Override
  public String queryDatabaseUserInfo(int limit, int offset, String userName) {
    try{
      List<DatabaseUserInfo> list = null;
      if(StringUtils.isBlank(userName)){
        list = userDao.getAllUserInfo(offset, limit);
      }else{
        list = userDao.getUserInfo(offset, limit, userName);
      }
      JSONArray rowData = JSONArray.fromObject(list);
      Map<String,Object> result = new HashMap<>();
      result.put("rows",rowData);
      result.put("total",userDao.getTotalCountUsers());
      return JacksonUtils.toJson(result);
    } catch (Exception e) {
      return ReturnJacksonUtil.resultFail(Constant.ERROR,e.toString(),Locale.CHINESE);
    }
  }

  @Override
  public String updateUserInfo(DatabaseUserInfo userInfo){
    try{
      userDao.updateUserInfo(userInfo);
      return ReturnJacksonUtil.resultOk();
    }catch (Exception e) {
      e.printStackTrace();
      return ReturnJacksonUtil.resultFail(Constant.ERROR,e.toString(),Locale.CHINESE);
    }
  }

  @Override
  public String updateDefaultSchema(String defaultSchema) {
    try{
      int n = userDao.updateDefaultSchema(defaultSchema,userHolder.getUserInfo().getId());
      return StringUtil.returnResultStr(Constant.SUCCESS,"执行成功，影响行数："+n);
    }catch (Exception e){
      return StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }
  }

  @Override
  public String deleteUser(String idList) {
    try{
      List<Long> list = Arrays.stream(idList.substring(0, idList.length() - 1).split(",")).map(id -> {
        return NumberUtils.toLong(id);
      }).collect(Collectors.toList());
      int n = userDao.deleteUser(list);
      return StringUtil.returnResultStr(Constant.SUCCESS,"执行成功，影响行数："+n);
    }catch (Exception e){
      return StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }
  }

  @Override
  public String getSqlDetail(int sqlId) {
    try{
      return ReturnJacksonUtil.resultOk(userDao.getSqlDetailById(sqlId));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String resetPassword(long id,String password){
    try{
      int n = userDao.resetUserPassword(id, MD5Support.hex(password, Constant.MD5KEY));
      return StringUtil.returnResultStr(Constant.SUCCESS,"执行成功，影响行数："+n);
    }catch (Exception e){
      return StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }
  }
}
