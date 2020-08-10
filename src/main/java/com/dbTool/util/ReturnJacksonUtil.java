/**
 * Copyright ©2016Renren. All rights reserved.
 */
package com.dbTool.util;

import com.dbTool.constant.Constant;
import com.dbTool.constant.WebCodeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: ReturnJacksonUtil
 * @Description: 定制Controller返回值工具类
 */
public class ReturnJacksonUtil {

  /**
   * 返回不带数据的成功的串
   */
  public static String resultOk(Locale locale) throws JsonProcessingException {
    WebCodeEnum webCodeEnum = WebCodeEnum.OK;
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(locale), null, null);
  }

  public static String resultOk() throws JsonProcessingException {
    WebCodeEnum webCodeEnum = WebCodeEnum.OK;
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(Locale.ENGLISH), null, null);
  }

  public static String resultFail() throws JsonProcessingException {
    WebCodeEnum webCodeEnum = WebCodeEnum.QUERY_SQL_FAIL;
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(Locale.ENGLISH), null, null);
  }

  /**
   * 返回特定成功文案
   */
  public static String resultOkWithEnum(WebCodeEnum webCodeEnum, Locale locale) throws
    JsonProcessingException {
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(locale), null, null);
  }

  /**
   * 返回成功的数据
   */
  public static String resultOk(Object data, Locale locale) throws JsonProcessingException {
    WebCodeEnum webCodeEnum = WebCodeEnum.OK;
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(locale), data, null);
  }

  public static String resultOk(Object data) {
    WebCodeEnum webCodeEnum = WebCodeEnum.OK;
    try{
      return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(Locale.ENGLISH), data, null);
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }

  public static String resultFail(Object data) {
    WebCodeEnum webCodeEnum = WebCodeEnum.QUERY_SQL_ERROR;
    try{
      return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(Locale.ENGLISH), data, null);
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 成功时，在data中返回key value对
   */
  public static String resultOk(String key, Object value, Locale locale) throws
    JsonProcessingException {
    Map<String, Object> data = new HashMap<String, Object>();
    data.put(key, value);
    WebCodeEnum webCodeEnum = WebCodeEnum.OK;
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(locale), data, null);
  }

  /**
   * 成功时，在data中返回key value对
   */
  public static String resultFail(String key, Object value, Locale locale)  {
    Map<String, Object> data = new HashMap<String, Object>();
    data.put(key, value);
    WebCodeEnum webCodeEnum = WebCodeEnum.QUERY_SQL_ERROR;
    try{
      return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(locale), data, null);
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }
  /**
   * 返回成功的数据，并指定相应的类型应该排除的字段
   *
   * @param data          返回的数据
   * @param clazz         指定的类型
   * @param excludeFileds 需要排除的字段
   */
  @SuppressWarnings("rawtypes")
  public static String resultOkWithExclude(Object data, Class clazz, Locale locale, String...
    excludeFileds) throws JsonProcessingException {
    Map<Class, Set<String>> exclude = new HashMap<Class, Set<String>>();
    exclude.put(clazz, new HashSet<String>(Arrays.asList(excludeFileds)));
    WebCodeEnum webCodeEnum = WebCodeEnum.OK;
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(locale), data, null, exclude);
  }

  /**
   * 返回表示操作成功的数据，并指定相应类型及其需要包含的输出字段
   *
   * @param data          返回的数据
   * @param clazz         指定类型
   * @param includeFileds 指定类型需要包含的字段
   */
  @SuppressWarnings("rawtypes")
  public static String resultOkWithInclude(Object data, Class clazz, Locale locale, String...
    includeFileds) throws JsonProcessingException {
    Map<Class, Set<String>> include = new HashMap<Class, Set<String>>();
    include.put(clazz, new HashSet<String>(Arrays.asList(includeFileds)));
    WebCodeEnum webCodeEnum = WebCodeEnum.OK;
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(locale), data, include, null);
  }

  /**
   * 返回带错误码的数据
   */
  public static String resultWithFailed(WebCodeEnum webCodeEnum, Locale locale) throws
    JsonProcessingException {
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(locale), null, null);
  }

  public static String resultWithFailed(WebCodeEnum webCodeEnum) throws JsonProcessingException {
    return result(webCodeEnum.getErrorCode(), webCodeEnum.getErrorMsg(Locale.ENGLISH), null, null);
  }

  public static String returnResult(int code, String msg, Object object) throws
    JsonProcessingException {
    Map<String, Object> map = new HashMap<>();
    map.put("msg", msg);
    map.put(Constant.RESULT_CODE, code);
    map.put("object", object);
    return JacksonUtils.map2Json(map);
  }

  /**
   * 根据提供的view对data中的对象中的字段进行定制输出
   */
  public static <T> String result(int resultCode, String resultMsg, Object data, Class<T>
    jsonViewClazz) throws JsonProcessingException {

    Map<String, Object> status = new HashMap<String, Object>();
    status.put(Constant.RESULT_CODE, resultCode);
    status.put(Constant.RESULT_MSG, String.valueOf(resultMsg));

    Map<String, Object> result = new HashMap<String, Object>();
    result.put(Constant.RESULT_STATUS, status);
    if (null != data) {
      result.put(Constant.RESULT_DATA, data);
    }
    if (null == jsonViewClazz) {
      return JacksonUtils.toJson(result);
    } else {
      return JacksonUtils.toJson(result, jsonViewClazz);
    }
  }

  /**
   * 返回表示成功与否的resultCode, resultMsg的数据，并指定多个类型及其包含的需要包含的字段、多个类型及其需要包含的字段
   *
   * @param resultCode 成功与否的代码
   * @param resultMsg  成功与否的输出信息
   * @param data       返回数据
   * @param include    多个类型及其需要包含的输出字段
   * @param exclude    多个类型及其对应的需要排除的字段
   */
  @SuppressWarnings("rawtypes")
  public static String result(int resultCode, String resultMsg, Object data, Map<Class,
    Set<String>> include, Map<Class, Set<String>> exclude) throws JsonProcessingException {

    Map<String, Object> status = new HashMap<String, Object>();
    status.put(Constant.RESULT_CODE, resultCode);
    status.put(Constant.RESULT_MSG, String.valueOf(resultMsg));

    Map<String, Object> result = new HashMap<String, Object>();
    result.put(Constant.RESULT_STATUS, status);
    if (null != data) {
      result.put(Constant.RESULT_DATA, data);
    }
    return JacksonUtils.toJson(result, include, exclude);
  }

  /**
   * 返回封装后的json
   */
  public static String resultWithException(int resultCode, String resultMsg, String data) throws
    JsonProcessingException {

    Map<String, Object> status = new HashMap<String, Object>();
    status.put(Constant.RESULT_CODE, resultCode);
    status.put(Constant.RESULT_MSG, String.valueOf(resultMsg));

    Map<String, Object> result = new HashMap<String, Object>();
    result.put(Constant.RESULT_STATUS, status);
    if (null != data) {
      result.put(Constant.RESULT_DATA, data);
    }
    return JacksonUtils.toJson(result);
  }

}
