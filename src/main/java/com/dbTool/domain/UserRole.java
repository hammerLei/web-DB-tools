package com.dbTool.domain;

import java.util.Date;

public class UserRole {

  private int id;
  private String roleName;
  private String description;
  private boolean dataState;
  private Date lastOperTime;
  private Date createDate;

  public UserRole(){

  }
  public UserRole(int id,String roleName,String description){
    this.id=id;
    this.roleName = roleName;
    this.description = description;
  }
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isDataState() {
    return dataState;
  }

  public void setDataState(boolean dataState) {
    this.dataState = dataState;
  }

  public Date getLastOperTime() {
    return lastOperTime;
  }

  public void setLastOperTime(Date lastOperTime) {
    this.lastOperTime = lastOperTime;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }
}
