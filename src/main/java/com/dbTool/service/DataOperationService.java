package com.dbTool.service;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by leifeifei on 17-9-11.
 */
public interface DataOperationService {
  /**
   * 执行SQL
   * @param env
   * @param schema
   * @param statement
   * @return
   */
  String executeSql(String env,String schema,String statement) throws JsonProcessingException;


  /**
   * 执行SQL
   * @param env
   * @param schema
   * @param statement
   * @return
   */
  String excSqlWithoutHistory(String env,String schema,String statement) throws JsonProcessingException;

  String getAllTablesWithSchema(String env, String schema) throws JsonProcessingException;
}
