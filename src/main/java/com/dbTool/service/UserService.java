package com.dbTool.service;


import com.dbTool.domain.DatabaseUserFavorite;
import com.dbTool.domain.DatabaseUserInfo;

import java.util.List;

public interface UserService {

  DatabaseUserInfo getUserInfoByAccount(String accout);

  void updateLastLoginData(DatabaseUserInfo user);

  int saveUserFavoriteSql(String name,String statement);

  int updateUserFavoriteSql(long id,String name,String statement);

  List<DatabaseUserFavorite> queryDatabaseUserFavoriteSql();

  String queryDatabaseUserHistorySql();

  int deleteUserFavoriteSql(long id);

  String updateUserPassword(String password,String newPassword,String confirmPassword);

  String createUserSubmit(DatabaseUserInfo userInfo,int roleGroup);

  DatabaseUserInfo getUserInfoById(long id,long version);

  String shareUserFavoriteSql(int userId, long sqlId);

  String getUserNamesByKey(String key);

  String queryDatabaseUserInfo(int limit, int offset, String userName);

  String updateUserInfo(DatabaseUserInfo userInfo);

  String updateDefaultSchema(String defaultSchema);

  String deleteUser(String idList);

  String getSqlDetail(int sqlId);

  String resetPassword(long id,String password);

}
