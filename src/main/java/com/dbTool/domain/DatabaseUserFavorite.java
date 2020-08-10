package com.dbTool.domain;

import java.util.Date;

/**
 * Created by leifeifei on 17-10-12.
 */
public class DatabaseUserFavorite {

  private long id;
  private long userId;
  private String favoriteSqlDetail;
  private String name;
  private Date createTime;
  private Date updateTime;
  private int deleteFlag;
  private String clientIp;
  private int orderSort;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getFavoriteSqlDetail() {
    return favoriteSqlDetail;
  }

  public void setFavoriteSqlDetail(String favoriteSqlDetail) {
    this.favoriteSqlDetail = favoriteSqlDetail;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public int getDeleteFlag() {
    return deleteFlag;
  }

  public void setDeleteFlag(int deleteFlag) {
    this.deleteFlag = deleteFlag;
  }

  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  public int getOrderSort() {
    return orderSort;
  }

  public void setOrderSort(int orderSort) {
    this.orderSort = orderSort;
  }
}
