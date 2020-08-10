package com.dbTool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.dbTool.util.DateUtil;

import java.util.Date;

/**
 * Created by leifeifei on 17-10-12.
 */
public class DatabaseUserSqlHistory {

  private long id;
  private long userId;
  private String sqlDetail;
  private Date createTime;
  private String  clientIp;
  private String serverIp;
  private String env;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @JsonIgnore
  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getSqlDetail() {
    return sqlDetail;
  }

  public void setSqlDetail(String sqlDetail) {
    this.sqlDetail = sqlDetail;
  }

  @JsonIgnore
  public Date getCreateTime() {
    return createTime;
  }

  public String getTime(){
    return DateUtil.format(createTime,DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  @JsonIgnore
  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  @JsonIgnore
  public String getServerIp() {
    return serverIp;
  }

  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }

  public String getEnv() {
    return env;
  }

  public void setEnv(String env) {
    this.env = env;
  }

}
