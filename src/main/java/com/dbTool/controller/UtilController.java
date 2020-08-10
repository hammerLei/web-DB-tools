package com.dbTool.controller;

import com.dbTool.UserHolder;
import com.dbTool.constant.Constant;
import com.dbTool.domain.dns.DNSRecord;
import com.dbTool.domain.dns.ServerCSR;
import com.dbTool.service.DataOperationService;
import com.dbTool.service.TemplateService;
import com.dbTool.service.UserService;
import com.dbTool.util.DNSUtil;
import com.dbTool.util.ExcShellUtil;
import com.dbTool.util.FileUtil;
import com.dbTool.util.JacksonUtils;
import com.dbTool.util.PropertiesResolver;
import com.dbTool.util.ReturnJacksonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.xbill.DNS.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UtilController {

  @Autowired
  UserHolder userHolder;

  @Autowired
  UserService userService;

  @Autowired
  DataOperationService dataOperationService;

  @Autowired
  TemplateService templateService;

  @RequestMapping(value = "domainInfo", method = RequestMethod.GET)
  public ModelAndView createUser() {
    ModelAndView view = new ModelAndView("domain_info");
    view.addObject("user", userHolder.getUserInfo());

    return view;
  }

  @ResponseBody
  @RequestMapping(value = "domainInfo/getDomainInfo", method = RequestMethod.POST)
  public String getDomainInfo(
      @RequestParam(value = "domain", required = true) String domain) throws Exception {
    Map map = new HashMap();
    List<DNSRecord> list = DNSUtil.getMainDomainCommonRecords(domain);
    List infoList = new ArrayList<>();
    Map<String, String> infoMap = DNSUtil.queryDomainInfoMap(domain);
    infoList.add(infoMap);
    if (MapUtils.isNotEmpty(infoMap) && StringUtils.isNotBlank(infoMap.get("Name Server"))) {
      String ns = infoMap.get("Name Server");
//      String registerProvider = infoMap.get("Registrar");
      //域名provider的ns关键字配置
      Map<String, String> dnsMap = PropertiesResolver.REGISTRAR_DNS_KERWORDS_PROPERTIES;
      if (StringUtils.isNotBlank(ns) && MapUtils.isNotEmpty(dnsMap)) {
        String godaddyNs = dnsMap.get("godaddy");
        String gfNs = dnsMap.get("geofarm");
        //NS包含配置的关键字，说明NS为默认NS
        if (ns.toLowerCase().contains(godaddyNs)) {
          map.put("nsFlag", 1);
        } else if(ns.toLowerCase().contains(gfNs)){
          map.put("nsFlag", 2);
        }else {
          map.put("nsFlag", 0);
        }
      }

    }
    map.put("dnsRecords", list);
    map.put("domainInfo", infoList);
    return ReturnJacksonUtil.resultOk(map);
  }


  @RequestMapping(value = "domainInfo/getDomainInfoForgf", method = RequestMethod.POST)
  public ModelAndView getDomainInfoForgf(
      @RequestParam(value = "domain", required = true) String domain) throws Exception {
    ModelAndView mv = new ModelAndView("tools/domain_info_query");
    mv.addObject("domain",domain);
    List<DNSRecord> alist = DNSUtil.getDomainRecordsByType(domain, Type.A);
    int domainIsToChime = 0;
    if(CollectionUtils.isNotEmpty(alist)){
      if(StringUtils.isNotBlank(alist.get(0).getValue()) && "52.52.24.52,52.9.101.47".contains(alist.get(0).getValue().trim())){
        domainIsToChime = 1;
      }
    }

    if(domainIsToChime == 0){

      List<DNSRecord> list = DNSUtil.getDomainRecordsByType(domain, Type.NS);
      Map<String, String> dnsMap = PropertiesResolver.REGISTRAR_DNS_KERWORDS_PROPERTIES;
      mv.addObject("note", "域名NS未托管给GF,NS不明确，请与客户沟通确认domain provider及账号密码");
      if (CollectionUtils.isNotEmpty(list) && MapUtils.isNotEmpty(dnsMap)) {

        String ns = list.get(0).getValue();
        String godaddyNs = dnsMap.get("godaddy");
        String gfNs = dnsMap.get("geofarm");
        //NS包含配置的关键字，说明NS为默认NS
        if (ns.toLowerCase().contains(godaddyNs)) {
          mv.addObject("note", "域名NS未托管给GF，godaddy域名，需要客户提供godaddy的账号和密码或者授权给我们配置！");
        } else if(ns.toLowerCase().contains(gfNs)){
          mv.addObject("note", "域名ns托管给了GF，无需提供其他账号信息即可配置三方");
        }
      }
    }else{
      mv.addObject("note", "该三方域名已配置到chime");
    }

    mv.addObject("domainIsToChime",domainIsToChime);
    return mv;
  }


  @RequestMapping(value = "jsonFormart", method = RequestMethod.GET)
  public ModelAndView jsonFormart() {
    ModelAndView view = new ModelAndView("formart_json");
    view.addObject("user", userHolder.getUserInfo());

    return view;
  }

  @RequestMapping(value = "regular", method = RequestMethod.GET)
  public ModelAndView regular() {
    ModelAndView view = new ModelAndView("regular_test");

    return view;
  }

  @RequestMapping(value = "freemarker", method = RequestMethod.GET)
  public ModelAndView freemarker() {
    ModelAndView view = new ModelAndView("freemarker_test");
    view.addObject("user", userHolder.getUserInfo());
    return view;
  }

  @RequestMapping(value = "freemarkerTest", method = RequestMethod.POST)
  @ResponseBody
  public String freemarkerTest(@RequestParam(value = "freemarkerDom", required = true) String freemarkerDom,
                               @RequestParam(value = "freemarkerData", required = false, defaultValue = "") String freemarkerData)
      throws Exception {
    return ReturnJacksonUtil.resultOk(templateService.process("freemarker_test", JacksonUtils.parseJSON2Map(freemarkerData), freemarkerDom));

  }


  @RequestMapping(value = "csrView", method = RequestMethod.GET)
  public ModelAndView csrView() {
    ModelAndView view = new ModelAndView("tools/ssl/create_csr");
    view.addObject("user", userHolder.getUserInfo());
    return view;
  }

  @RequestMapping(value = "csrView/createCsr", method = RequestMethod.POST)
  public ModelAndView createCsr(@ModelAttribute ServerCSR serverCSR)
      throws Exception {
    ModelAndView view = new ModelAndView("tools/ssl/csr_result");

    if (serverCSR.isAttributeEmpty()) {
      view.addObject(Constant.RESULT_CODE, 100);
      view.addObject(Constant.RESULT, "Input Must Not Be Empty!");
    } else {
      view.addObject(Constant.RESULT_CODE, 0);
      String resultConsole = ExcShellUtil.makeCSR(serverCSR);
      String csrJson = FileUtil.readFileByNamePathIgnoreExist(serverCSR.getCommonName() + ".csr", "/ssl/" + serverCSR.getCommonName() + "/");
      if (StringUtils.isNotBlank(csrJson)) {
        view.addObject(Constant.RESULT, csrJson);
      } else {
        view.addObject(Constant.RESULT, resultConsole);
      }
//      }
    }
    return view;
  }


  @RequestMapping(value = "sslConfig", method = RequestMethod.GET)
  public ModelAndView sslConfig() {
    ModelAndView view = new ModelAndView("tools/ssl/create_csr");
    view.addObject("user", userHolder.getUserInfo());
    return view;
  }

  @RequestMapping(value = "sslConfig/configSsl", method = RequestMethod.POST)
  public ModelAndView configSsl(@ModelAttribute ServerCSR serverCSR)
      throws Exception {
    ModelAndView view = new ModelAndView("tools/ssl/csr_result");

    if (serverCSR.isAttributeEmpty()) {
      view.addObject(Constant.RESULT_CODE, 100);
      view.addObject(Constant.RESULT, "Input Must Not Be Empty!");
    } else {
      view.addObject(Constant.RESULT_CODE, 0);
      String resultConsole = ExcShellUtil.makeCSR(serverCSR);
      String csrJson = FileUtil.readFileByNamePathIgnoreExist(serverCSR.getCommonName() + ".csr", "/ssl/" + serverCSR.getCommonName() + "/");
      if (StringUtils.isNotBlank(csrJson)) {
        view.addObject(Constant.RESULT, csrJson);
      } else {
        view.addObject(Constant.RESULT, resultConsole);
      }
//      }
    }
    return view;
  }


}
