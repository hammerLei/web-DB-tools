package com.dbTool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

public class Menu {
  private int id;
  private String title;
  private String name;

  private String pageUrl;
  private String icon;
  private int openType;
  private boolean extend;
  private int parentId;
  private int maxOpen;

  private List<Menu> childs;

  @JsonIgnore
  private boolean isDesk;
  @JsonIgnore
  private long userId;
  private boolean isNecessary;
  private boolean isPublic;
  @JsonIgnore
  private Date createTime;
  @JsonIgnore
  private Date updateTime;
  @JsonIgnore
  private String clientIp;

  private boolean roleSelect = false;


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public int getOpenType() {
    return openType;
  }

  public void setOpenType(int openType) {
    this.openType = openType;
  }

  public boolean isExtend() {
    return extend;
  }

  public void setExtend(boolean extend) {
    this.extend = extend;
  }

  public int getParentId() {
    return parentId;
  }

  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  public int getMaxOpen() {
    return maxOpen;
  }

  public void setMaxOpen(int maxOpen) {
    this.maxOpen = maxOpen;
  }

  public List<Menu> getChilds() {
    return childs;
  }

  public void setChilds(List<Menu> childs) {
    this.childs = childs;
  }

  public boolean getIsDesk() {
    return isDesk;
  }

  public void setDesk(boolean desk) {
    isDesk = desk;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public boolean getIsNecessary() {
    return isNecessary;
  }

  public void setNecessary(boolean isNecessary) {
    this.isNecessary = isNecessary;
  }

  public boolean getIsPublic() {
    return isPublic;
  }

  public void setPublic(boolean aPublic) {
    isPublic = aPublic;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  public String getPageUrl() {
    return pageUrl;
  }

  public void setPageUrl(String pageUrl) {
    this.pageUrl = pageUrl;
  }

  public boolean isRoleSelect() {
    return roleSelect;
  }

  public void setRoleSelect(boolean roleSelect) {
    this.roleSelect = roleSelect;
  }
}
