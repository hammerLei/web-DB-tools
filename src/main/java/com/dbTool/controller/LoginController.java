package com.dbTool.controller;

import com.dbTool.util.CookieUtil;
import com.dbTool.util.StringUtil;
import com.dbTool.util.MD5Support;
import com.dbTool.util.MailUtil;
import com.dbTool.util.RequestAccessUtil;
import com.dbTool.util.ReturnJacksonUtil;
import com.dbTool.util.SymmetricEncoder;
import com.dbTool.util.TemplateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.dbTool.UserHolder;
import com.dbTool.WebSecurityConfig;
import com.dbTool.constant.Constant;
import com.dbTool.constant.WebCodeEnum;
import com.dbTool.domain.DatabaseUserInfo;
import com.dbTool.service.UserService;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

/**
 * Created by leifeifei on 17-9-1.
 */
@Controller
public class LoginController {

  @Autowired
  UserService userService;

  private Logger log = LogManager.getLogger(LoginController.class);

  @Autowired
  UserHolder userHolder;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  @ResponseBody
  public String login(
    @RequestParam(value = "username") String username,
    @RequestParam(value = "password") String password,
    HttpSession session,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws JsonProcessingException {
    boolean pass = false;
    DatabaseUserInfo user = userService.getUserInfoByAccount(username);
    String encPassword = MD5Support.hex(password, Constant.MD5KEY);
    if (user != null && encPassword.equals(user.getPassword()) && user.getStatus() == 1) {
      pass = true;
    }
    if (pass) {
      session.setAttribute("database_user", user);
      session.setMaxInactiveInterval(120 * 60);
      user.setLastLoginTime(new Date());
      user.setLastLoginIp(userHolder.getClientIp());
      CookieUtil.saveSessionCookie(request, response, WebSecurityConfig.USER_COOKIE, CookieUtil.encode(user.getId(),MD5Support.hex(String.valueOf(System.currentTimeMillis()))));
      CookieUtil.saveSessionCookie(request, response, WebSecurityConfig.VERSION, CookieUtil.encode(user.getVersion()*user.getId(),MD5Support.hex(String.valueOf(user.getLastUpdatePasswordTime()))));

      userService.updateLastLoginData(user);
      return ReturnJacksonUtil.resultOk();
    } else {
      return ReturnJacksonUtil.resultWithFailed(WebCodeEnum.SIG_ERROR);
    }
  }

  @RequestMapping(value = "/logout", method = {RequestMethod.GET})
  public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
    session.removeAttribute("database_user");
    CookieUtil.clear(request, response, WebSecurityConfig.USER_COOKIE);
    return "login";
  }

  @RequestMapping(value = {"/login", "/"}, method = RequestMethod.GET)
  public String login(HttpSession session) {
    if (session.getAttribute("database_user") != null) {
      DatabaseUserInfo user = (DatabaseUserInfo)session.getAttribute("database_user");
      if(StringUtils.isNotBlank(user.getAfterLoginRedirect())){
        return "redirect:"+user.getAfterLoginRedirect();
      }
      return "redirect:operate";
    }
    return "login";
  }

  @RequestMapping(value = "/test", method = RequestMethod.GET)
  public String test(@RequestParam(value = "page", required = true) String page) {
    return page;
  }

  @RequestMapping(value = "/page/{path}/{page}", method = RequestMethod.GET)
  public ModelAndView pageTest(@PathVariable( "path") String path, @PathVariable( "page") String page,HttpServletRequest request) {
    ModelAndView mv = new ModelAndView(path+"/"+page);
    Map<String, String> parameterPairs = RequestAccessUtil.getParameterPairs(request);
    if(MapUtils.isNotEmpty(parameterPairs)){
      for (Map.Entry<String, String> entry : parameterPairs.entrySet()) {
        mv.addObject(entry.getKey(),entry.getValue());
      }
    }
    return mv;
  }

  @RequestMapping(value = "/forget", method = RequestMethod.GET)
  public ModelAndView forget(){
    ModelAndView view = new ModelAndView("/forget");
    return view;
  }

  @RequestMapping(value = "/reset", method = RequestMethod.GET)
  public ModelAndView reset(){
    ModelAndView view = new ModelAndView("/reset");
    return view;
  }

  @RequestMapping(value = "/forgetPassword", method = RequestMethod.GET)
  @ResponseBody
  public String forgetPassword(@RequestParam(value = "email") String email, @RequestParam(value = "userName") String userName) throws Exception{
    DatabaseUserInfo user = userService.getUserInfoByAccount(userName);
    if(user == null || !email.equalsIgnoreCase(user.getEmail())){
      return StringUtil.returnResultStr(Constant.ERROR,"邮箱不正确");
    }
    String resetLink;
    log.info("用户ID"+user.getId());
    String encodeId = SymmetricEncoder.AESEncode(SymmetricEncoder.ENCODE_RULES, String.valueOf(user.getId()));
    log.info("解码后的ID"+encodeId);
    if(userHolder.getENV().equals("test")){
      resetLink = "http://sitetest-database.chime.me/reset?id=" + encodeId;
    }else {
      resetLink = "http://site-database.chime.me/reset?id=" + encodeId;
    }
    Map<String,Object> map = new HashedMap();
    map.put("account",userName);
    map.put("resetLink",resetLink);
    String emailContentHtml = TemplateUtil.getTemplateByName("database_forget_email.ftl",map);
    if(StringUtils.isNotBlank(emailContentHtml)){
      String to [] = user.getEmail().split(",");
      MailUtil.sendHtmlMail(to,"Forget Password","Reset Password",emailContentHtml);
    }
    return StringUtil.returnResultStr(Constant.SUCCESS,"执行成功");
  }

  @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
  @ResponseBody
  public String resetPassword(@RequestParam(value = "id") String id,
                              @RequestParam(value = "password") String password){
    log.info("解码后的ID"+id);
    String dncodeId = SymmetricEncoder.AESDncode(SymmetricEncoder.ENCODE_RULES, id);
    log.info(dncodeId);
    log.info(Long.valueOf(dncodeId));
      return userService.resetPassword(Long.valueOf(dncodeId),password);
  }

}
