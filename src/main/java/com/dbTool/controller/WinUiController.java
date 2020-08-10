package com.dbTool.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.dbTool.UserHolder;
import com.dbTool.constant.Constant;
import com.dbTool.domain.DatabaseUserInfo;
import com.dbTool.domain.Menu;
import com.dbTool.exception.MsgException;
import com.dbTool.service.MenuService;
import com.dbTool.util.JacksonUtils;
import com.dbTool.util.MD5Support;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WinUiController {


  @Autowired
  UserHolder userHolder;

  @Autowired
  MenuService menuService;

  @RequestMapping(value = {"/winUi/*/*","/winUi/index"}, method = RequestMethod.GET)
  public ModelAndView winui(HttpServletRequest request) {
    DatabaseUserInfo user = userHolder.getUserInfo();
    String page = request.getRequestURI().trim().substring(1);
    ModelAndView view = new ModelAndView(page);
    view.addObject("dbUser",user);
    return view;
  }


  @RequestMapping(value = "/getMenus/{type}")
  @ResponseBody
  public String getMenus(@PathVariable("type") int type) throws JsonProcessingException {
    List<Menu> menuLsit = menuService.getMenuByType(type);
    Map result = new HashMap();
    result.put(Constant.RESULT_CODE,0);
    result.put("msg","");
    result.put("count",CollectionUtils.isNotEmpty(menuLsit) ? menuLsit.size() : 0);
    result.put(Constant.RESULT_DATA,menuLsit);
    return JacksonUtils.toJson(result);
  }

  @RequestMapping(value = "/getMenuList")
  @ResponseBody
  public String getMenuList(@RequestParam(value = "page",defaultValue = "1") int page, @RequestParam(value = "limit",defaultValue = "10") int limit) throws JsonProcessingException {
    List<Menu> menuLsit = menuService.getMenuList(page,limit);

    int menuCount = menuService.getMenuCount();
    Map result = new HashMap();
    result.put(Constant.RESULT_CODE,0);
    result.put("msg","");
    result.put("count",menuCount);
    result.put(Constant.RESULT_DATA,menuLsit);
    return JacksonUtils.toJson(result);
  }

  @RequestMapping(value = "/menu/edit")
  public ModelAndView editMenu(@RequestParam(value = "id") int id,@RequestParam(value = "parentTableId") String parentTableId)  {
    DatabaseUserInfo userInfo = userHolder.getUserInfo();
    Menu menu = menuService.getMenuById(id);

    if(menu == null || (menu.getIsNecessary() && userInfo.getLevel() != 9) || (menu.getUserId()>0 && menu.getUserId() != menu.getUserId() && userInfo.getLevel() != 9)){
      throw new MsgException("menu不存在或没有权限！");
    }
    ModelAndView view = new ModelAndView("/winUi/menu/edit");
    List<Menu> menuLsit = menuService.getParentMenu();
    view.addObject("menu",menu);
    view.addObject("menuList",menuLsit);
    view.addObject("userInfo",userInfo);
    view.addObject("parentTableId",parentTableId);
    return view;
  }

  @RequestMapping(value = "/menu/add")
  public ModelAndView addMenu(@RequestParam(value = "parentTableId") String parentTableId)  {
    DatabaseUserInfo userInfo = userHolder.getUserInfo();
    ModelAndView view = new ModelAndView("/winUi/menu/add");
    List<Menu> menuLsit = menuService.getParentMenu();
    view.addObject("menuList",menuLsit);
    view.addObject("userInfo",userInfo);
    view.addObject("parentTableId",parentTableId);
    return view;
  }


  @RequestMapping(value = "/menu/save")
  @ResponseBody
  public String saveMenu(@ModelAttribute(value = "menu") Menu menu)  {
    DatabaseUserInfo userInfo = userHolder.getUserInfo();
    if(menu == null || (menu.getIsNecessary() && userInfo.getLevel() != 9)
        || (menu.getUserId()>0 && menu.getUserId() != menu.getUserId() && userInfo.getLevel() != 9)
        || (userInfo.getLevel() != 9  && (menu.getIsNecessary() || menu.getIsPublic()))){
      throw new MsgException("menu不存在或没有权限！");
    }
    menuService.saveMenu(menu);

    return Constant.SUCCESS;
  }

  @RequestMapping(value = "/menu/delete")
  @ResponseBody
  public String saveMenu(@RequestParam(value = "ids") String ids)  {
    if(StringUtils.isBlank(ids)){
      return "请选择一条记录";
    }
    if(ids.trim().length()>1 && ids.trim().endsWith(",")){
      ids = ids.trim().substring(0,ids.trim().length()-1);
    }
    String idStrings [] = ids.split(",");
    List<Integer> idList = new ArrayList<>();
    for (String id: idStrings) {
      idList.add(NumberUtils.toInt(id));
    }
    menuService.deleteMenus(idList);
    return Constant.SUCCESS;
  }


  @RequestMapping(value = "/unLockScreen")
  @ResponseBody
  public String unLockScreen(@RequestParam(value = "password") String password)  {
    DatabaseUserInfo userInfo = userHolder.getUserInfo();
    String encPassword = MD5Support.hex(password, Constant.MD5KEY);
    if(encPassword.equals(userInfo.getPassword())){
      return Constant.SUCCESS;
    }

    return "fail";
  }


}
