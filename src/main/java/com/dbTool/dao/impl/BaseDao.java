package com.dbTool.dao.impl;

import com.dbTool.constant.Constant;
import com.dbTool.domain.DatabaseUserInfo;
import com.dbTool.util.DBUtil;
import com.dbTool.util.DateUtil;
import com.dbTool.util.StringUtil;
import com.dbTool.util.JacksonUtils;
import com.dbTool.util.ReturnJacksonUtil;

import org.apache.commons.lang.math.NumberUtils;

import java.sql.*;
import java.util.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by leifeifei on 17-9-1.
 */

public class BaseDao {
  public static ResultSet executeQuery(String env,String schema,String statement) throws Exception {
    ResultSet resultSet = null;
    Map<String, Connection> dbConnections = DBUtil.getOnlineDBConnections(env,schema);
      Statement queryLeadQAInfo = dbConnections.get("database").createStatement();
      queryLeadQAInfo.setQueryTimeout(DBUtil.getSqlTimeOut());
      resultSet = queryLeadQAInfo.executeQuery(statement);
    return resultSet;
  }

  public static int executeUpInSql(String env,String schema,String statement) throws Exception {
    Map<String, Connection> dbConnections = DBUtil.getOnlineDBConnections(env,schema);
    Statement queryLeadQAInfo = dbConnections.get("database").createStatement();
    queryLeadQAInfo.setQueryTimeout(DBUtil.getSqlTimeOut());
    return queryLeadQAInfo.executeUpdate(statement); //execute sql
  }

  public static String executeSqlResultForOnlyRead(String env,String schema,String statement)
  {
    String result = null;
    try{
      Map<String, Connection> dbConnections = DBUtil.getOnlineDBConnections(env,schema);
      dbConnections.get("database").setReadOnly(true);
      Statement queryLeadQAInfo = dbConnections.get("database").createStatement();
      queryLeadQAInfo.setQueryTimeout(DBUtil.getSqlTimeOut());
      queryLeadQAInfo.execute(statement);
      ResultSet resultSet=queryLeadQAInfo.getResultSet();
      result=executeQueryResultReturn(resultSet);
    //  DBUtil.closeAllDBConnections();
    }catch (Exception e){
      return StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }
    return result;
  }
  public static Statement createConnection(String env,String schema) throws Exception{
    Map<String, Connection> dbConnections = DBUtil.getOnlineDBConnections(env,schema);
    return dbConnections.get("database").createStatement();
  }
  public static String executeSqlResultNew(String env,String schema,String statement,Statement queryLeadQAInfo) throws Exception{
   // Map<String, Connection> dbConnections = DBUtil.getOnlineDBConnections(env,schema);
   // Statement queryLeadQAInfo = dbConnections.get("database").createStatement();
    String result;
    if(queryLeadQAInfo.execute(statement))
    {
      ResultSet resultSet=queryLeadQAInfo.getResultSet();
      result=executeQueryResultReturn(resultSet);
   //  DBUtil.closeAllDBConnections();
    }
    else
    {
      int n=queryLeadQAInfo.getUpdateCount();
      result = JacksonUtils.toJson("执行成功，影响行数：" + n);

    }
    return result;
  }
  public static void closeAllConnections(){
     DBUtil.closeAllDBConnections();
  }
  public static String executeSqlResult(String env,String schema,String statement){
    String result = null;
    try{
      int n = executeUpInSql(env,schema,statement);
      result = StringUtil.returnResultStr(Constant.SUCCESS,"执行成功，影响行数："+n);
     // DBUtil.closeAllDBConnections();
    }catch (Exception e){
      return StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }
    return result;
  }
  public static String executeQueryResultReturn(ResultSet resultSet)
  {
    String result = null;
    try
    {
      if(resultSet != null){
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData md = resultSet.getMetaData(); //获得结果集结构信息,元数据
        int columnCount = md.getColumnCount();   //获得列数
        while (resultSet.next()) {
          Map<String,Object> rowData = new LinkedHashMap<>();
          for (int i = 1; i <= columnCount; i++) {
            if(md.getColumnType(i)==93 && resultSet.getTimestamp(i) != null){
              String str=resultSet.getTimestamp(i).toString();
              str=str.replaceAll("\\.0","");
              rowData.put(md.getColumnName(i), str);
            }
            else{
              rowData.put(md.getColumnName(i), resultSet.getObject(i));
            }

          }
          list.add(rowData);
        }
        //result = ReturnJacksonUtil.resultOk(list);
        result = JacksonUtils.toJson(list);
      }
    }
    catch(Exception e){
      return StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }
    return result;
  }

  public static int countTotal(String env, String schema) {
    String countStr = "SELECT COUNT(id) from database_user_info";
    int count = 0;
    try {
      ResultSet resultSet = executeQuery(env, schema, countStr);
      if(resultSet != null){
        while (resultSet.next()) {
          count = NumberUtils.toInt(resultSet.getObject(1).toString());
        }
      }
    } catch (Exception e) {
    }
    return count;
  }

  public static String executeQueryTablesReturn(String env, String schema) {
    Map<Object,List<String>> result = new HashMap<>();
    try{
      String showTables = "show tables";
      ResultSet resultSet = executeQuery(env, schema, showTables);
      if(resultSet != null){
        while (resultSet.next()) {
          List<String> columns = new ArrayList<>();
//          String showColumns = "show columns from " + resultSet.getObject(1).toString();
//          ResultSet columnResult = executeQuery(env, schema, showColumns);
//          List<String> columns = new ArrayList<>();
//          while (columnResult.next()) {
//            columns.add(columnResult.getObject(1).toString());
//          }
          result.put(resultSet.getObject(1),columns);
        }
      }
      return ReturnJacksonUtil.resultOk(result);

    } catch (Exception e) {
      return ReturnJacksonUtil.resultFail(result);
    }
  }


  public static String executeQueryResult(String env,String schema,String statement){
    String result = null;
    try{
      ResultSet resultSet = executeQuery(env,schema,statement);
      result=executeQueryResultReturn(resultSet);
      DBUtil.closeAllDBConnections();
    }catch(Exception e){
      return StringUtil.returnFailResultStr(Constant.ERROR,e.toString());
    }


    return result;
  }

  /**
   * 根据clazz和ResultSet返回对象(这个对象里的全部属性)集合
   *
   * @param rs
   * @param clazz
   * @return
   */
  public static <T> ArrayList<T> getDataByClassFiled(ResultSet rs,
                                                     Class<T> clazz) {
    ArrayList<T> domains = new ArrayList<>();
    try {

      // 获得自己的属性
      Field[] fields = clazz.getDeclaredFields();
      // 获得父类的属性
      Field[] superFields = clazz.getSuperclass().getDeclaredFields();
      // 自己的和父类的属性相加
      Field[] allFields = addFields(fields, superFields);
      while (rs.next()) {
        T vo = clazz.newInstance();
        // 历遍所有的属性
        for (Field field : allFields) {
          String methodName = getSetterMethodName(field.getName());
          try{
            Method method = clazz
                .getMethod(methodName, field.getType());
            invokeMothod(rs, field, method, vo);
          }catch (NoSuchMethodException e){
            //没有set方法的属性非数据库字段
            continue;
          }
        }
        domains.add(vo);
      }
    } catch (InstantiationException | IllegalAccessException
        | SecurityException | SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return domains;
  }

  /**
   * 根据返回的resultSet来决定生成对象有那些属性
   *
   * @param rs
   * @param clazz
   * @return
   */
  public static <T> ArrayList<T> getDataByResultSet(ResultSet rs,
                                                    Class<T> clazz) {
    ArrayList<T> domains = new ArrayList<>();

    // 通过metaData获取到的属性生成对应的FieldList
    List<Field> allFields = new ArrayList<>();

    try {
      //如果未空值返回
      if (null==rs) {
        return null;
      }
      // 先获取元数据再通过元数据进行细化的操作
      java.sql.ResultSetMetaData metaData = rs.getMetaData();

      // 通过ResultSet里面的column的名字来决定用这个类的哪一个Field
      for (int i = 0; i < metaData.getColumnCount(); i++) {
        allFields
            .add(clazz.getDeclaredField(metaData.getColumnName(i+1)));
        // allFields.add(clazz.getSuperclass().getDeclaredField(
        // metaData.getColumnName(i+1)));

      }
      while (rs.next()) {
        T vo = clazz.newInstance();
        // 历遍所有的属性
        for (Field field : allFields) {
          String methodName = getSetterMethodName(field.getName());
          Method method = clazz
              .getMethod(methodName, field.getType());
          invokeMothod(rs, field, method, vo);
        }
        domains.add(vo);
      }
    } catch (InstantiationException | IllegalAccessException
        | SecurityException | SQLException | NoSuchMethodException
        | NoSuchFieldException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return domains;
  }

  /**
   * 相加f1和f2的Field
   *
   * @param f1
   * @param f2
   * @return
   */
  private static Field[] addFields(Field[] f1, Field[] f2) {
    List<Field> fields = new ArrayList<>();
    for (Field field : f1) {
      fields.add(field);
    }
    for (Field field : f2) {
      fields.add(field);
    }
    return fields.toArray(new Field[f1.length + f2.length]);
  }

  /**
   * 根据属性名得到set的方法名
   *
   * @param name
   * @return
   */
  private static String getSetterMethodName(String name) {
    String begin = name.substring(0, 1).toUpperCase();
    String mothodName = "set" + begin + name.substring(1, name.length());
    return mothodName;
  }

  /**
   * 根据属性名得到set的方法名
   *
   * @param name
   * @return
   */
  private static String getGetterMethodName(String name) {
    String begin = name.substring(0, 1).toUpperCase();
    String mothodName = "get" + begin + name.substring(1, name.length());
    return mothodName;
  }

  /**
   * 根据ResultSet和Field的getName从结果集取出
   *
   * @param rs
   * @param field
   * @param method
   * @param object
   */
  private static void invokeMothod(ResultSet rs, Field field, Method method,
                                   Object object) {
    try {
      if(isExistColumn(rs,DBUtil.propertyToField(field.getName()))){
        Object o=rs.getObject(DBUtil.propertyToField(field.getName()));
        method.invoke(object, o);
      }
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  /**
   * 判断查询结果集中是否存在某列
   * @param rs 查询结果集
   * @param columnName 列名
   * @return true 存在; false 不存咋
   */
  private static boolean isExistColumn(ResultSet rs, String columnName) {
    try {
      if (rs.findColumn(columnName) > 0 ) {
        return true;
      }
    }
    catch (SQLException e) {
      return false;
    }

    return false;
  }


  /**
   * 通过反射机制生成插入sql语句
   *
   * @param
   * @return
   */
  public static String transformToInsertSql(Object obj) {
    StringBuffer bufferFront = new StringBuffer();
    StringBuffer bufferLast = new StringBuffer();
    String tableName = DBUtil.propertyInsertToField(obj.getClass().getSimpleName());
    bufferFront.append("insert into "
        + tableName.substring(0, 1).toLowerCase()
        + tableName.substring(1, tableName.length()) + "(");
    bufferLast.append(" values(");
    // 通过class得到所有的属性不受访问控制符空值
    Field[] fields = obj.getClass().getDeclaredFields();
    boolean flag = true;
    for (Field field : fields) {

//      String methodName = getGetterMethodName(field.getName());

      try{
        field.setAccessible(true);
        Object value = field.get(obj);
        if(value == null){
          continue;
        }
        if (field.getName().equals("id") && NumberUtils.toInt(value.toString()) == 0){
          continue;
        }
        String str = "";
        if(value instanceof Date){
          value = DateUtil.format((Date)value,DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
        }
        if(value instanceof Integer || value instanceof Long){
          str = value+",";
        }else{
          str = "'"+value+"',";
        }
        bufferLast.append(str);
      }catch (Exception e){
        e.printStackTrace();
        flag = false;
      }
      bufferFront.append(DBUtil.propertyInsertToField(field.getName()) + ",");
    }

    if(!flag){
      return null;
    }
    bufferFront.delete(bufferFront.length() - 1, bufferFront.length());
    bufferLast.delete(bufferLast.length() - 1, bufferLast.length());
    bufferFront.append(")");
    bufferLast.append(")");
    bufferFront.append(bufferLast);

    return bufferFront.toString();
  }

  /**
   * 通过反射机制生成通用的更新语句
   *
   * @param
   * @return
   */
  public static String transformToUpdateSql(Object obj) {

    StringBuffer bufferFront = new StringBuffer();
    StringBuffer bufferLast = new StringBuffer();
    String tableName = DBUtil.propertyInsertToField(obj.getClass().getSimpleName());
    bufferFront.append("update " + tableName.substring(0, 1).toLowerCase()
        + tableName.substring(1, tableName.length()) + " set ");
    long id=0;
    // 通过class得到所有的属性不受访问控制符空值
    Field[] fields = obj.getClass().getDeclaredFields();
    for (Field field : fields) {
      try{
        field.setAccessible(true);
        Object value = field.get(obj);
        if(value == null){
          continue;
        }
        String str = "";
        if(value instanceof Date){
          value = DateUtil.format((Date)value,DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
        }
        if(value instanceof Integer || value instanceof Long){
          str = "="+value+",";
        }else{
          str = "='"+value+"',";
        }
        if (field.getName().equals("id")){
          id=(Long) value;
          continue;
        }
        bufferFront.append(DBUtil.propertyInsertToField(field.getName()) + str);

      }catch (Exception e){
        e.printStackTrace();
      }


    }
    bufferLast.append(" where id="+id);
    bufferFront.delete(bufferFront.length() - 1, bufferFront.length());
    bufferFront.append(bufferLast);
    return bufferFront.toString();
  }



  /**
   * 通过反射机制生成插入sql语句
   *
   * @param
   * @return
   */
  public static String transformToInsertSql(Object obj,String tableName) {
    StringBuffer bufferFront = new StringBuffer();
    StringBuffer bufferLast = new StringBuffer();
    bufferFront.append("insert into "
        + tableName.substring(0, 1).toLowerCase()
        + tableName.substring(1, tableName.length()) + "(");
    bufferLast.append(" values(");
    // 通过class得到所有的属性不受访问控制符空值
    Field[] fields = obj.getClass().getDeclaredFields();
    boolean flag = true;
    for (Field field : fields) {

//      String methodName = getGetterMethodName(field.getName());

      try{
        field.setAccessible(true);
        Object value = field.get(obj);
        if (field.getName().equals("id") && NumberUtils.toInt(value.toString()) == 0) {
          continue;
        }

        String str = "#{"+obj.getClass().getSimpleName()+"."+field.getName()+"},";
//        if(value instanceof Date){
//          value = DateUtil.format((Date)value,DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
//        }
//        if(value instanceof Integer || value instanceof Long){
//          str = value+",";
//        }else{
//          str = "'"+value+"',";
//        }
        bufferLast.append(str);
      }catch (Exception e){
        e.printStackTrace();
        flag = false;
      }
      bufferFront.append(DBUtil.propertyInsertToField(field.getName()) + ",");
    }

    if(!flag){
      return null;
    }
    bufferFront.delete(bufferFront.length() - 1, bufferFront.length());
    bufferLast.delete(bufferLast.length() - 1, bufferLast.length());
    bufferFront.append(")");
    bufferLast.append(")");
    bufferFront.append(bufferLast);

    return bufferFront.toString();
  }

  /**
   * 通过反射机制生成通用的更新语句
   *
   * @param
   * @return
   */
  public static String transformToUpdateSql(Object obj,String tableName) {

    StringBuffer bufferFront = new StringBuffer();
    StringBuffer bufferLast = new StringBuffer();
//    String tableName = DBUtil.propertyInsertToField(obj.getClass().getSimpleName());
    bufferFront.append("update " + tableName.substring(0, 1).toLowerCase()
        + tableName.substring(1, tableName.length()) + " set ");
    long id=0;
    // 通过class得到所有的属性不受访问控制符空值
    Field[] fields = obj.getClass().getDeclaredFields();
    for (Field field : fields) {
      try{
        field.setAccessible(true);
        Object value = field.get(obj);
        if(value == null){
          continue;
        }
        String str = "";
        if(value instanceof Date){
          value = DateUtil.format((Date)value,DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
        }
        if(value instanceof Integer || value instanceof Long){
          str = "="+value+",";
        }else{
          str = "='"+value+"',";
        }
        if (field.getName().equals("id")){
          id=(Long) value;
          continue;
        }
        bufferFront.append(DBUtil.propertyInsertToField(field.getName()) + str);

      }catch (Exception e){
        e.printStackTrace();
      }


    }
    bufferLast.append(" where id="+id);
    bufferFront.delete(bufferFront.length() - 1, bufferFront.length());
    bufferFront.append(bufferLast);
    return bufferFront.toString();
  }

  public static void main(String [] srgs){
//    try{
//      System.out.println(getDataByClassFiled(executeQuery("test","sitebuilt","select * from database_user_info where account='feifei'"), DatabaseUserInfo.class));
//    }catch (Exception e){
//
//    }
//    executeQueryResult("test","sitebuilt","desc about_info");

//    System.out.println(MD5Support.hex("123456", Constant.MD5KEY));
//    System.out.println(transformToUpdateSql(DatabaseUserInfo.class));
//    DatabaseUserInfo user = new DatabaseUserInfo();
//    user.setAccount("feifei.lei");
//    user.setPassword(MD5Support.hex("lff@123", Constant.MD5KEY));
//    user.setLastLoginIp("0.0.0.0");
//    user.setLevel(2);
//    System.out.println(transformToInsertSql(user));
//
//    DatabaseUserInfo duser = new DatabaseUserInfo();
//    duser.setAccount("houjun.fan");
//    duser.setPassword(MD5Support.hex("houjun#123", Constant.MD5KEY));
//    duser.setLastLoginIp("0.0.0.0");
//    duser.setLevel(2);
//    System.out.println(transformToInsertSql(duser));
//
//    DatabaseUserInfo duser2 = new DatabaseUserInfo();
//    duser2.setAccount("qian.xu");
//    duser2.setPassword(MD5Support.hex("qian.xu%123", Constant.MD5KEY));
//    duser2.setLastLoginIp("0.0.0.0");
//    duser2.setLevel(1);
//    System.out.println(transformToInsertSql(duser2));
//    user.setLastLoginTime(new Date());
//    System.out.println(executeSqlResult("test","sitebuilt","insert into database_user_favorite(user_id,favorite_sql_detail,`name`,delete_flag,client_ip,order_sort) values(1,'select * from database_user_info ','收藏',0,'127.0.0.1',0);"));
    DatabaseUserInfo userFavorite = new DatabaseUserInfo();
    System.out.println(transformToInsertSql(userFavorite,"database_user_info"));
  }
}


