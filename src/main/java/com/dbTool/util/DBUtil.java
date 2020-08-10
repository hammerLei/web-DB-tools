package com.dbTool.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 根据配置文件的内容返回数据库连接，使用数据库的名称作为key从Map中索引值
 *
 * Created by leifeifei on 17-10-12.
 */
public class DBUtil {

  private static Map<String, Connection> dbConnections;

  private static Map<String, Connection> gfDbConnections = getGfDBConnections();

  private static String sysEnv = "";

  /**
   * 返回数据库连接
   */
  public static Map<String,Connection> getGfDBConnections() {
    Map<String,Connection> map = new HashedMap();
    try{
      map = getOnlineDBConnections("prd","gf");
    }catch (Exception e){
      e.printStackTrace();
    }
    return map;
  }

  /**
   * 返回数据库连接
   */
  public static Map<String,Connection> getOnlineDBConnections(String env,String schema) throws Exception{
    if(StringUtils.isBlank(sysEnv)){
      Environment environment = SpringUtil.getBean(Environment.class);
      sysEnv = environment.getProperty("env");
    }
    if("gf".equals(schema) && (MapUtils.isNotEmpty(gfDbConnections) || "test".equals(sysEnv))){
      return gfDbConnections;
    }
    prepareDBConnections(env,schema);
    return dbConnections;
  }

  public static int getSqlTimeOut(){
    Map<String, String> databaseMap = PropertiesResolver.DATABASE_PROPERTIES;
    String timeout = databaseMap.get("jdbc.sql.timeout");
    return NumberUtils.toInt(timeout) == 0 ? 10 : NumberUtils.toInt(timeout);
  }


  /**
   * 准备数据库连接
   */
  public static void prepareDBConnections(String env,String schema) throws Exception{
    dbConnections = new HashMap<String, Connection>();
    Map<String, String> databaseMap = PropertiesResolver.DATABASE_PROPERTIES;
//		try {
    Class.forName(databaseMap.get("driver"));
    String userName = databaseMap.get(env+"."+schema+".userName");
    String password = databaseMap.get(env+"."+schema+".password");
    Connection sitebuilt = DriverManager.getConnection(databaseMap.get(env+"."+schema+".url"), userName, password);
    // UserHolder userHolder =new UserHolder();
    // if(userHolder.getUserInfo().getLevel()==0)
    // 	sitebuilt.setReadOnly(true);
    dbConnections.put("database", sitebuilt);
//		} catch (ClassNotFoundException | SQLException e) {
//			e.printStackTrace();
////			System.exit(0);
//		}
    if("gf".equals(schema)){
      gfDbConnections.putAll(dbConnections);
    }
  }

  /**
   * 关闭所有的数据库连接
   */
  public static void closeAllDBConnections(){
    if(dbConnections!=null && CollectionUtils.isNotEmpty(dbConnections.entrySet())){
      for(Entry<String,Connection> connection:dbConnections.entrySet()){
        try {
          connection.getValue().close();
        } catch (SQLException e) {
          e.printStackTrace();
          System.exit(0);
        }
      }
    }
  }

  /**
   * 对象属性转换为字段  例如：userName to user_name
   * @param property 字段名
   * @return
   */
  public static String propertyToField(String property) {
    if (null == property) {
      return "";
    }
    char[] chars = property.toCharArray();
    StringBuffer sb = new StringBuffer();
    for (char c : chars) {
      if (CharUtils.isAsciiAlphaUpper(c)) {
        sb.append("_" + StringUtils.lowerCase(CharUtils.toString(c)));
      } else {
        sb.append(c);
      }
    }
    String result=sb.toString();
    if(result.startsWith("_")){
      result = StringUtils.substring(result,1);
    }
    return result;
  }

  /**
   * 对象属性转换为字段  例如：userName to user_name
   * @param property 字段名
   * @return
   */
  public static String propertyInsertToField(String property) {

    String result=propertyToField(property);
    result="`"+result+"`";
    return result;
  }

  /**
   * 字段转换成对象属性 例如：user_name to userName
   * @param field
   * @return
   */
  public static String fieldToProperty(String field) {
    if (null == field) {
      return "";
    }
    char[] chars = field.toCharArray();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      if (c == '_') {
        int j = i + 1;
        if (j < chars.length) {
          sb.append(StringUtils.upperCase(CharUtils.toString(chars[j])));
          i++;
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
