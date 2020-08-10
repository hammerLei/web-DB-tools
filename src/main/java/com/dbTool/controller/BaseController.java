package com.dbTool.controller;

import com.dbTool.UserHolder;
import com.dbTool.constant.Constant;
import com.dbTool.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseController {

  public static String PERMISSION_DENIED = StringUtil.returnResultStr(Constant.ERROR,"没有权限！");

  @Autowired
  UserHolder userHolder;

  public boolean isDbAdminUser(){
    if(userHolder.getUserInfo().getLevel() == 9){
      return true;
    }
    return false;
  }

}
