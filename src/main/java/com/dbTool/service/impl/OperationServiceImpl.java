package com.dbTool.service.impl;

import com.dbTool.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.dbTool.UserHolder;
import com.dbTool.constant.Constant;
import com.dbTool.dao.OperationDao;
import com.dbTool.service.DataOperationService;
import com.dbTool.util.ReturnJacksonUtil;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Statement;
import java.util.*;

/**
 * Created by leifeifei on 17-9-11.
 */
@Service
public class OperationServiceImpl implements DataOperationService {

  @Autowired
  OperationDao operationDao;


  @Autowired
  UserHolder userHolder;

  public boolean isError =false;

  public String executeSql(String env,String schema,String statement) throws JsonProcessingException {
    String excResult;
    try{
      boolean explain=false;
      Statement queryLeadQAInfo=operationDao.createDBConnection(env,schema);
      if(statement.indexOf("explain")==0)
             explain=true;

    if(statement.contains(";\n")){
      String sqlStr [] = statement.split(";\n");
      if(sqlStr.length==1){
       excResult=excSql(env,schema,sqlStr[0],queryLeadQAInfo);
       operationDao.closeDBConnection();
       return excResult;
      }else{
        Map<String, String> map = new LinkedHashMap<>();
        isError=false;
        for (String statementSql: sqlStr) {
          if(StringUtils.isNoneBlank(statementSql)){
            if(explain&&statementSql.indexOf("explain")!=0){
              statementSql="explain "+statementSql;
            }
            if (isError) {
              String sqlRe =  StringUtil.returnFailResultStr("no execute","该SQL未执行!");
              map.put(statementSql,sqlRe);
              continue;
            }
            String sqlRe = excSql(env,schema,statementSql,queryLeadQAInfo);
            map.put(statementSql,sqlRe);

          }
        }
        if(MapUtils.isNotEmpty(map)){
          excResult= ReturnJacksonUtil.resultOk(map);
          operationDao.closeDBConnection();
          return excResult;
        }
      }

    }
    excResult = excSql(env,schema,statement,queryLeadQAInfo);
    operationDao.closeDBConnection();
    return excResult;
    }
    catch (Exception e)
    {
      return e.toString();
    }
  }

  private String excSql(String env,String schema,String statement,Statement queryLeadQAInfo) throws JsonProcessingException{
    if(statement.trim().endsWith(";")){
      statement = statement.substring(0,statement.length()-1);
    }
    String result;
    try{
      result=operationDao.executeSql(env,schema,statement,queryLeadQAInfo);
    }catch (Exception e){
      //e.printStackTrace();
      isError=true;
      result = StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }

    try{
      if(StringUtils.isNoneBlank(result)){
        JSONObject jsonResult = JSONObject.fromObject(result);
        if(jsonResult.getJSONObject(Constant.RESULT_STATUS).getInt(Constant.RESULT_CODE)==0){
          operationDao.insertExecuteSqlHistory(env,statement);
        }
      }
      //记录SQL执行日志
    }catch (Exception e){
      operationDao.insertExecuteSqlHistory(env,statement);
      e.printStackTrace();
    }
    return result;
  }

  public String excSqlWithoutHistory(String env,String schema,String statement) throws JsonProcessingException{
    if(statement.trim().endsWith(";")){
      statement = statement.substring(0,statement.length()-1);
    }
    String result;
    try{
      Statement queryLeadQAInfo=operationDao.createDBConnection(env,schema);
      result=operationDao.executeSql(env,schema,statement,queryLeadQAInfo);
    }catch (Exception e){
      //e.printStackTrace();
      isError=true;
      result = StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }

    return result;
  }

  @Override
  public String getAllTablesWithSchema(String env, String schema) {
    return operationDao.getTablesJson(env, schema);
  }
}
