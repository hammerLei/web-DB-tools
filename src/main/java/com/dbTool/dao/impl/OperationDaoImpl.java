package com.dbTool.dao.impl;

import com.dbTool.constant.Constant;
import com.dbTool.domain.DatabaseUserSqlHistory;
import com.dbTool.util.DBUtil;
import com.dbTool.util.StringUtil;
import com.dbTool.UserHolder;
import com.dbTool.dao.OperationDao;

import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by leifeifei on 17-9-11.
 */
@Service
public class OperationDaoImpl extends BaseDao implements OperationDao {

  @Autowired
  UserHolder userHolder;

  @Override
  public String executeSql(String env, String schema, String statement, Statement queryLeadQAInfo) throws Exception {
    String str = statement.trim().toLowerCase();
    boolean hasLimit = false;
    //sql是否有limit操作
    if(str.startsWith("select") && str.contains(" limit ")){
      String limit = str.substring(str.lastIndexOf(" limit ")+7);
      if(StringUtils.isNotBlank(limit)){
        String num [] = limit.split(",");
        //limit [num] || limit [start],[end]   2种情况  需要判断后面是否位数字，避免查询条件中有带有limit字符串情况
        if(num.length<=2 && num.length>0){
          hasLimit = true;
          for (String numc: num) {
            if(!NumberUtils.isDigits(numc.trim())){
              hasLimit = false;
              break;
            }
          }
        }
      }
    }
    //如果是select操作，且没有加limit,默认查询500条
    if (str.startsWith("select") && !hasLimit) {
      statement = "select * from ( " + statement + " ) as query_table limit 0,500";
    }else if(str.startsWith("select")){
      //加了limit最大查询10W条
      statement = "select * from ( " + statement + " ) as query_table limit 0,100000";
    }
    int level = 0;
    if(!"gf".equals(schema) && userHolder != null && userHolder.getUserInfo() != null){
      level = userHolder.getUserInfo().getLevel();
      if("prd".equals(env)) {
        level = userHolder.getUserInfo().getOnlineLevel();
      }
    }
    if(("test".equals(env) && statement.indexOf("jira_ad_risk_issue")>0) || statement.indexOf("update website_info set https=1")>=0){
      level = 1;
    }
    if (level == 0) {
      return executeSqlResultForOnlyRead(env, schema, statement);
    } else if (level == 1) {
      if (str.startsWith("create") || str.startsWith("alter") || str.startsWith("drop"))
        return StringUtil.returnFailResultStr(Constant.ERROR, "Permission denied");
      return executeSqlResultNew(env, schema, statement, queryLeadQAInfo);
    } else
      return executeSqlResultNew(env, schema, statement, queryLeadQAInfo);


  }

  @Override
  public void insertExecuteSqlHistory(String env,String statement){
    DatabaseUserSqlHistory databaseUserSqlHistory = new DatabaseUserSqlHistory();
    databaseUserSqlHistory.setClientIp(userHolder.getClientIp());
    databaseUserSqlHistory.setCreateTime(new Date());
    databaseUserSqlHistory.setEnv(env);
    databaseUserSqlHistory.setSqlDetail(StringUtil.replceSingleQuot(statement));
    databaseUserSqlHistory.setUserId(userHolder.getUserInfo().getId());
    databaseUserSqlHistory.setServerIp(userHolder.getServerIp());
    executeSqlResult(userHolder.getENV(),"db_tool",transformToInsertSql(databaseUserSqlHistory));
  }

  @Override
  public void closeDBConnection(){
    closeAllConnections();
  }

  @Override
  public Statement createDBConnection(String env,String schema) throws Exception{
    return  createConnection(env,schema);
  }

  @Override
  public String getTablesJson(String env, String schema) {
    return executeQueryTablesReturn(env, schema);
  }

  @Override
  public  <T> ArrayList<T> queryObjectList(String statement, String schema, Class<T> claszz) {
    try {
      System.out.println("--------"+statement);
      return getDataByClassFiled(executeQuery(userHolder.getENV(), schema, statement), claszz);
    } catch (Exception e) {
      System.out.println("======="+statement);
      e.printStackTrace();
      return null;
    } finally {
      DBUtil.closeAllDBConnections();
    }
  }

}
