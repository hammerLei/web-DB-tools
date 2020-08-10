/**
 * Copyright ©2016Renren. All rights reserved.
 */
package com.dbTool.constant;

import java.util.Locale;

/**
 * @ClassName: CodeEnum
 * @Description: 中英文网站返回信息枚举类
 */
public enum WebCodeEnum {

  OK(0, "success", "成功"),
  QUERY_SQL_ERROR(10000, "SQL ERROR", "SQL执行报错"),
  QUERY_SQL_FAIL(10001, "FAIL", "执行失败"),
  SIG_ERROR(100001, "Signal Error", "签名检验失败");

  private int errorCode; // 错误码

  private String enErrorMsg; // 英文错误信息

  private String zhErrorMsg; // 中文错误信息

  private WebCodeEnum(int errorCode, String enErrorMsg, String zhErrorMsg) {
    this.errorCode = errorCode;
    this.enErrorMsg = enErrorMsg;
    this.zhErrorMsg = zhErrorMsg;
  }

  public int getErrorCode() {
    return errorCode;
  }


  // 根据中英文获取相应的中英文错误信息
  public String getErrorMsg(Locale locale) {
    if (locale.equals(Locale.CHINESE) || locale.equals(Locale.CHINA) || locale.equals(Locale
        .TAIWAN) || locale.equals(Locale.PRC) || locale.equals(Locale.SIMPLIFIED_CHINESE) ||
        locale.equals(Locale.TRADITIONAL_CHINESE)) {
      return zhErrorMsg;
    } else {
      return enErrorMsg;
    }
  }

}

