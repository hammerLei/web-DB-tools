package com.dbTool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.dbTool.util.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by zwk on 17-1-10.
 */
public class DatabaseUserInfo {

  private long id;
  //用户名
  private String account;
  //密码
  @JsonIgnore
  private String password;
  //权限，0：只读，1：读写
  private int level;
  //最后登录时间
  private Date lastLoginTime;
  //最后登录IP
  private String lastLoginIp;
  //最后更新密码时间
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Date lastUpdatePasswordTime;
  //email
  private String email;
  //乐观锁
  private int version;
  //线上权限
  private int onlineLevel;

  private int status;

  private String defaultSchema;

  private String afterLoginRedirect;

  @JsonIgnore
  private List<UserRole> userRoleList;

  public String getDefaultSchema() {
    return defaultSchema;
  }

  public void setDefaultSchema(String defaultSchema) {
    this.defaultSchema = defaultSchema;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public Date getLastLoginTime() {
    return lastLoginTime;
  }

  public void setLastLoginTime(Date lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
  }

  public String getLastLoginIp() {
    return lastLoginIp;
  }

  public void setLastLoginIp(String lastLoginIp) {
    this.lastLoginIp = lastLoginIp;
  }

  public Date getLastUpdatePasswordTime() {
    return lastUpdatePasswordTime;
  }

  public void setLastUpdatePasswordTime(Date lastUpdatePasswordTime) {
    this.lastUpdatePasswordTime = lastUpdatePasswordTime;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getUpdatePasswordinterval() {
    if(lastUpdatePasswordTime == null){
      return 0;
    }
    return DateUtil.datesBetween(lastUpdatePasswordTime,new Date());
  }

  public void setOnlineLevel(int onlineLevel) {
    this.onlineLevel = onlineLevel;
  }

  public int getOnlineLevel() {
    return onlineLevel;
  }

  public String getAfterLoginRedirect() {
    return afterLoginRedirect;
  }

  public void setAfterLoginRedirect(String afterLoginRedirect) {
    this.afterLoginRedirect = afterLoginRedirect;
  }

  public List<UserRole> getUserRoleList() {
    return userRoleList;
  }

  public void setUserRoleList(List<UserRole> userRoleList) {
    this.userRoleList = userRoleList;
  }
}
