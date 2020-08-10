package com.dbTool.dao;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leifeifei on 17-9-11.
 */
public interface OperationDao {

  String executeSql(String env,String schema,String statement,Statement queryLeadQAInfo) throws JsonProcessingException, Exception;

  void insertExecuteSqlHistory(String env,String statement);

  void closeDBConnection();

  Statement createDBConnection(String env,String schema) throws Exception;

  String getTablesJson(String env, String schema);

  <T> ArrayList<T> queryObjectList(String statement, String schema, Class<T> clazz);
}
