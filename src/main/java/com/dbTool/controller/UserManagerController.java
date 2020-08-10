package com.dbTool.controller;

import com.dbTool.util.DateUtil;
import com.dbTool.util.StringUtil;
import com.dbTool.util.JacksonUtils;
import com.dbTool.util.MD5Support;
import com.dbTool.util.ReturnJacksonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.dbTool.UserHolder;
import com.dbTool.constant.Constant;
import com.dbTool.domain.DatabaseUserInfo;
import com.dbTool.domain.Menu;
import com.dbTool.domain.UserRole;
import com.dbTool.service.MenuService;
import com.dbTool.service.UserRoleService;
import com.dbTool.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UserManagerController extends BaseController{

  @Autowired
  private UserHolder userHolder;

  @Autowired
  private UserService userService;

  @Autowired
  private MenuService menuService;

  @Autowired
  private UserRoleService userRoleService;

  @RequestMapping(value = "update_password", method = RequestMethod.GET)
  public ModelAndView updatePassword() {
    ModelAndView view = new ModelAndView("update_password");
    view.addObject("env",userHolder.getENV());
    view.addObject("user",userHolder.getUserInfo());
    view.addObject("updatePasswordinterval", DateUtil.datesBetween(userHolder.getUserInfo().getLastUpdatePasswordTime(),new Date()));
    view.addObject("isInitPw","6a8c2125406ca9963fcd2aec1578155a".equals(userHolder.getUserInfo().getPassword()));
    view.addObject("schemaList",userHolder.getSchemaList());
    return view;
  }

  @RequestMapping(value = "history",method =RequestMethod.GET)
  public ModelAndView history()
  {
    ModelAndView view = new ModelAndView("history");
    view.addObject("env",userHolder.getENV());
    view.addObject("user",userHolder.getUserInfo());
    view.addObject("historySqlList",userService.queryDatabaseUserHistorySql());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "update_password_submit", method = RequestMethod.POST)
  public String updatePasswordSubmit (
      @RequestParam(value =  "password", required = true) String password,
      @RequestParam(value = "newPassword", required = true) String newPassword,
      @RequestParam(value = "confirmPassword", required = true) String
          confirmPassword) throws JsonProcessingException {
    return userService.updateUserPassword(password,newPassword,confirmPassword);
  }

  @RequestMapping(value = "updateEmail", method = RequestMethod.POST)
  @ResponseBody
  public String updateEmail(@RequestParam(value = "email") String email){
    DatabaseUserInfo userInfo = userHolder.getUserInfo();
    if(email.equals(userInfo.getEmail())){
        return ReturnJacksonUtil.resultFail("邮箱无修改");
    }
    userInfo.setEmail(email);
    return userService.updateUserInfo(userInfo);
  }


//  @RequestMapping(value = "create_user", method = RequestMethod.GET)
//  public ModelAndView createUser() {
//    ModelAndView view = new ModelAndView("page_not_found");
//    if(userHolder.getUserInfo().getLevel()==9){
//      view = new ModelAndView("create_user");
//    }
//    view.addObject("env",userHolder.getENV());
//    view.addObject("user",userHolder.getUserInfo());
//    view.addObject("databaseEnvList",userHolder.getDatabaseEnv());
//    return view;
//  }

  @ResponseBody
  @RequestMapping(value = "createUserSubmit", method = RequestMethod.POST)
  public String createUserSubmit (
      @RequestParam(value =  "account", required = true) String account,
      @RequestParam(value = "password", required = true) String password,
      @RequestParam(value = "confirmPassword", required = true) String  confirmPassword,
      @RequestParam(value = "email", required = true) String email,
      @RequestParam(value = "level", required = false,defaultValue = "0") int level,
      @RequestParam(value = "onlineLevel", required = false, defaultValue = "0") int onlineLevel,
    @RequestParam(value = "defaultSchema", required = false, defaultValue = "db_test1") String defaultSchema,
      @RequestParam(value = "roleGroup",defaultValue = "2") int roleGroup) throws JsonProcessingException {

    if(!isDbAdminUser()){
      return PERMISSION_DENIED;
    }
    if(!(password.equals(confirmPassword))){
      return StringUtil.returnResultStr(Constant.ERROR,"2次输入的密码不匹配");
    }
    DatabaseUserInfo userInfo = new DatabaseUserInfo();
    userInfo.setAccount(account);
    userInfo.setPassword(MD5Support.hex(password, Constant.MD5KEY));
    userInfo.setLevel(level);
    userInfo.setOnlineLevel(onlineLevel);
    userInfo.setDefaultSchema(defaultSchema);
    userInfo.setStatus(1);
    userInfo.setEmail(email);
    return userService.createUserSubmit(userInfo,roleGroup);
  }

  @RequestMapping(value = "userManager", method = RequestMethod.GET)
  public ModelAndView userManager() {
    ModelAndView view = new ModelAndView("page_not_found");
    if(isDbAdminUser()){
      view = new ModelAndView("userManager");
    }
    view.addObject("env",userHolder.getENV());
    view.addObject("user",userHolder.getUserInfo());
    view.addObject("databaseEnvList",userHolder.getDatabaseEnv());
    view.addObject("schemaList",userHolder.getSchemaList());
    view.addObject("roleList",userRoleService.getUserRoleList(100,1));
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "getUserInfo", method = RequestMethod.GET)
  public String getUserInfo(@RequestParam(value = "limit") int limit,
                            @RequestParam(value = "offset") int offset,
                            @RequestParam(value = "key", required = false, defaultValue = "") String userName,
                            HttpServletResponse response) throws IOException{
    if(!isDbAdminUser()){
      response.sendRedirect("/page_not_found");
      return "";
    }
    return userService.queryDatabaseUserInfo(limit, offset, userName);
  }

  @ResponseBody
  @RequestMapping(value = "update/UserInfo", method = RequestMethod.GET)
  public String updateUserInfo(@RequestParam(value = "id") int id,
    @RequestParam(value = "account") String account,
    @RequestParam(value = "level") int level,
    @RequestParam(value = "onlineLevel") int onlineLevel,
    @RequestParam(value = Constant.RESULT_STATUS) int status,
    @RequestParam(value = "defaultSchema") String defaultSchema,
    @RequestParam(value = "email") String email,
                               HttpServletResponse response) throws IOException{
    if(!isDbAdminUser()){
      response.sendRedirect("/page_not_found");
      return "";
    }
    DatabaseUserInfo databaseUserInfo = new DatabaseUserInfo();
    databaseUserInfo.setId(id);
    databaseUserInfo.setAccount(account);
    databaseUserInfo.setLevel(level);
    databaseUserInfo.setOnlineLevel(onlineLevel);
    databaseUserInfo.setStatus(status);
    databaseUserInfo.setDefaultSchema(defaultSchema);
    databaseUserInfo.setEmail(email);
    return userService.updateUserInfo(databaseUserInfo);
  }

  @ResponseBody
  @RequestMapping(value = "update/defaultSchema", method = RequestMethod.GET)
  public String updateDefaultSchema(@RequestParam(value = "defaultSchema") String defaultSchema,
                                    HttpServletResponse response) throws IOException{
    if(!isDbAdminUser()){
      response.sendRedirect("/page_not_found");
      return "";
    }
    return userService.updateDefaultSchema(defaultSchema);
  }

  @ResponseBody
  @RequestMapping(value = "deleteUser", method = RequestMethod.GET)
  public String deleteUser(@RequestParam(value = "idList") String idList,
                           HttpServletResponse response) throws IOException{
    if(!isDbAdminUser()){
      response.sendRedirect("/page_not_found");
      return "";
    }
    return userService.deleteUser(idList);
  }

  @ResponseBody
  @RequestMapping(value = "getSqlDetail", method = RequestMethod.GET)
  public String getSqlDetail(@RequestParam(value = "sqlId") int sqlId) {
    return userService.getSqlDetail(sqlId);
  }

  @ResponseBody
  @RequestMapping(value = "role/getUserRoleList", method = RequestMethod.GET)
  public String getUserRoleList(HttpServletResponse response,@RequestParam(value = "limit") int limit,
                                @RequestParam(value = "page")int page) throws IOException{
    if(!isDbAdminUser()){
      response.sendRedirect("/page_not_found");
      return "";
    }

    List<UserRole> list = userRoleService.getUserRoleList(limit,page);
    Map result = new HashMap();
    result.put(Constant.RESULT_DATA,list);
    result.put(Constant.RESULT_CODE,0);
    result.put("msg","");
    result.put("count",list.size());
    return JacksonUtils.toJson(result);
  }

  @RequestMapping(value = "role/edit", method = RequestMethod.GET)
  public ModelAndView roleEdit(HttpServletResponse response,@RequestParam(value = "id",defaultValue = "0") int id) {

    if(!isDbAdminUser()){
      return new ModelAndView("page_not_found");
    }

    ModelAndView view = new ModelAndView("/winUi/role/edit");

    UserRole userRole = new UserRole();
    if(id != 0){
      userRole = userRoleService.getUserRoleById(id);
    }

    List<Menu> allMenus = menuService.getAllMenuByRole(id);

    view.addObject("sysMenus",allMenus);
    view.addObject("userRole",userRole);
    view.addObject("dbUser",userHolder.getUserInfo());

    return view;
  }

  @RequestMapping(value = "role/update", method = RequestMethod.POST)
  @ResponseBody
  public String roleUpdate(HttpServletResponse response,@RequestParam(value = "roleName") String roleName,
                           @RequestParam(value = "description") String description,
                           @RequestParam(value = "menus") String menus,@RequestParam(value = "id" ,defaultValue = "0")int id) throws IOException{

    if(!isDbAdminUser()){
      response.sendRedirect("/page_not_found");
      return "";
    }
    UserRole userRole = new UserRole(id,roleName,description);
    //插入，得到id
    if(id == 0){
      id = userRoleService.insertUserRole(userRole);
      userRole.setId(id);
    }else{
      userRoleService.updateUserRole(userRole);
    }
    List<Integer> list = null;
    if(StringUtils.isNotBlank(menus)){
      list = Arrays.stream(menus.substring(0, menus.length() - 1).split(",")).map(roleId -> {
        return NumberUtils.toInt(roleId);
      }).collect(Collectors.toList());
    }

    int n = menuService.updateRoleMenus(id,list);

    return String.valueOf(n);
  }

}
