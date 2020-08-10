package com.dbTool.controller;

import com.dbTool.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.dbTool.UserHolder;
import com.dbTool.WebSecurityConfig;
import com.dbTool.constant.Constant;
import com.dbTool.service.DataOperationService;
import com.dbTool.service.UserService;
import com.dbTool.util.CookieUtil;
import com.dbTool.util.MailUtil;
import com.dbTool.util.ReturnJacksonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class OperationController {

  private final static Log logger = LogFactory.getLog(OperationController.class);

//  @Value("${env}")
//  private String ENV;

  @Autowired
  UserHolder userHolder;

  @Autowired
  UserService userService;

  @Autowired
  DataOperationService dataOperationService;

  @ResponseBody
  @RequestMapping(value = "executeSql", method = RequestMethod.POST)
  public String update (
      @RequestParam(value =  "env") String env,
      @RequestParam(value = "schema") String schema,
      @RequestParam(value = "statement") String statement,
      HttpSession session, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
      if((StringUtil.replceMultipleOnlySpace(statement).toLowerCase().contains("drop database ") && StringUtil.replceMultipleOnlySpace(statement).toLowerCase().contains("drop table ")) && userHolder.getUserInfo().getLevel()!=9){
        dataOperationService.executeSql(userHolder.getENV(),"db_tool",String.format("update database_user_info set status=0 where id=%s",userHolder.getUserInfo().getId()));
        try{
          String to [] = {"feifei.lei@renren-inc.com"};
          String content=String.format("<div>env：%s <br>schema：%s <br>执行SQL：%s <br>clientIp：%s</div>",env,schema,statement,userHolder.getClientIp());
          MailUtil.sendHtmlMail(to,String.format("【警报】%s 尝试drop 操作",userHolder.getUserInfo().getId()+"_"+userHolder.getUserInfo().getAccount()),userHolder.getUserInfo().getAccount(),content);
        }catch (Exception e){
          logger.error(String.format("【警报】%s 尝试drop database,执行环境及SQL：%s | %s",userHolder.getUserInfo().getAccount(),env,statement));
        }
        if(StringUtil.replceMultipleOnlySpace(statement).toLowerCase().contains("drop database ")){
          session.removeAttribute("database_user");
          CookieUtil.clear(request, response, WebSecurityConfig.USER_COOKIE);
          return "login";
        }
      }
      return dataOperationService.executeSql(env,schema, StringUtil.replceMultipleOnlySpace(statement));
  }


  @RequestMapping(value = "operate", method = RequestMethod.GET)
  public ModelAndView operate() {
    ModelAndView view = new ModelAndView("operate/operate");
    view.addObject("env",userHolder.getENV());
    view.addObject("user",userHolder.getUserInfo());
    view.addObject("favoriteSqlList",userService.queryDatabaseUserFavoriteSql());
    view.addObject("databaseEnvList",userHolder.getDatabaseEnv());
    view.addObject("schemaList",userHolder.getSchemaList());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "saveUserFavoriteSql", method = RequestMethod.POST)
  public String saveUserFavoriteSql (
      @RequestParam(value =  "id", required = false,defaultValue = "0") long id,
      @RequestParam(value = "name") String name,
      @RequestParam(value = "statement") String statement) throws JsonProcessingException {
    if(id == 0 ){
      userService.saveUserFavoriteSql(name, statement);
    }else{
      return String.valueOf(userService.updateUserFavoriteSql(id,name, statement));
    }
    return ReturnJacksonUtil.resultOk(userService.queryDatabaseUserFavoriteSql());
  }

  @ResponseBody
  @RequestMapping(value = "deleteUserFavoriteSql", method = RequestMethod.POST)
  public String deleteUserFavoriteSql (
      @RequestParam(value = "id") long id) throws JsonProcessingException {
    int result = userService.deleteUserFavoriteSql(id);
    return Constant.SUCCESS;
//    return ReturnJacksonUtil.resultOk(userService.queryDatabaseUserFavoriteSql());
  }

  @RequestMapping(value = "queryFavoriteSql", method = RequestMethod.GET)
  public ModelAndView queryFavoriteSql() {
    ModelAndView view = new ModelAndView("operate/operate_favorite_sql");
    view.addObject("favoriteSqlList",userService.queryDatabaseUserFavoriteSql());
    return view;
  }

  @ResponseBody
  @RequestMapping(value = "getTables", method = RequestMethod.GET)
  public String getTables(
    @RequestParam(value =  "env", required = false, defaultValue = "") String env,
    @RequestParam(value = "schema") String schema
  ) throws JsonProcessingException {
    if(StringUtils.isBlank(env)) {
      env = userHolder.getENV();
    }
//    userService.updateDefaultSchema(schema);
    return dataOperationService.getAllTablesWithSchema(env,schema);
  }

  @ResponseBody
  @RequestMapping(value = "shareUserFavoriteSql", method = RequestMethod.POST)
  public String shareUserFavoriteSql(
    @RequestParam(value = "userId") int userId,
    @RequestParam(value = "sqlId") long sqlId

  ) {
    return userService.shareUserFavoriteSql(userId,sqlId);
  }


  @ResponseBody
  @RequestMapping(value = "getUserNames", method = RequestMethod.GET)
  public String getUserName(@RequestParam(value = "key") String key) {
    return userService.getUserNamesByKey(key);
  }



}
