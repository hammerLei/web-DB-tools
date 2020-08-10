package com.dbTool.util;

import com.dbTool.domain.dns.DNSRecord;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.xbill.DNS.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leifeifei on 17-10-12.
 */
public class DNSUtil {

  public static List<DNSRecord> getDomainMXRecords(String domain){
    List list = new ArrayList<>();
    try{
      Record[] records =null;
      Lookup lookup = new Lookup(domain, Type.MX);
      lookup.run();

      if(lookup.getResult() == Lookup.SUCCESSFUL){
        records=lookup.getAnswers();
      }else{
        return list;
      }
      for (int i = 0; i < records.length; i++) {
        DNSRecord dnsRecord = new DNSRecord();
        MXRecord mx = (MXRecord) records[i];
        dnsRecord.setName(mx.getName().toString());
        dnsRecord.setType("MX");
        dnsRecord.setValue(mx.getPriority() + " " + mx.getTarget().toString());
        dnsRecord.setTtl(mx.getTTL());
        list.add(dnsRecord);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
    return list;
  }

  public static List<DNSRecord> getDomainRecordsByType(String domain,int type){
    List list = new ArrayList<>();
    try{
      Record[] records =null;
      Lookup lookup = new Lookup(domain, type);
      lookup.run();

      if(lookup.getResult() == Lookup.SUCCESSFUL){
        records=lookup.getAnswers();
      }else{
        return list;
      }
      Record[] answers = lookup.getAnswers();
      for(Record rec : answers){
        String record = StringUtil.replceMultiTab(rec.toString());
        System.out.println(record);
        String recordStr [] = record.split("\t");
        if(recordStr.length==5){
          DNSRecord dnsRecord = new DNSRecord();
          dnsRecord.setName(recordStr[0]);
        dnsRecord.setType(recordStr[3]);
        dnsRecord.setValue(recordStr[4]);
        dnsRecord.setTtl(NumberUtils.toLong(recordStr[1]));
        list.add(dnsRecord);
        }
      }
    }catch(Exception e){
      e.printStackTrace();
    }
    return list;
  }


  /**
   * 获取域名常见records ： A、MX、TXT、NS、SOA、SRV、AAAA、www的CNAME
   * @param domain
   * @return
   */
  public static List<DNSRecord> getDomainCommonRecords(String domain){
    List<DNSRecord> list = new ArrayList<>();
    list.addAll(getDomainRecordsByType(domain,Type.A));
    list.addAll(getDomainRecordsByType(domain,Type.MX));
    list.addAll(getDomainRecordsByType(domain,Type.TXT));
    list.addAll(getDomainRecordsByType(domain,Type.SRV));
    list.addAll(getDomainRecordsByType(domain,Type.SOA));
    list.addAll(getDomainRecordsByType(domain,Type.AAAA));
    list.addAll(getDomainRecordsByType(domain,Type.CNAME));
    list.addAll(getDomainRecordsByType(domain,Type.NS));
    return list;
  }

  /**
   * 获取当前域名及其顶级域名的DNS配置
   * @param domain
   * @return
   */
  public static List<DNSRecord> getMainDomainCommonRecords(String domain){
    String mainDomain = StringUtil.getMainDomain(domain);
    List<DNSRecord> domainRecordList =  getDomainCommonRecords(domain);
    if(!domain.toLowerCase().equals(mainDomain)){
      List<DNSRecord> mainDomainRecordList =  getDomainCommonRecords(mainDomain);
      domainRecordList.addAll(mainDomainRecordList);
    }
    return domainRecordList;
  }



  public static boolean cnameEquals(String domain,String equalsValue){
    boolean result = false;
    List<DNSRecord> cnameList = getDomainRecordsByType(domain,Type.CNAME);
    for (DNSRecord dnsRecord : cnameList) {
      if(StringUtils.isNotBlank(dnsRecord.getValue()) && (equalsValue.equals(dnsRecord.getValue().trim()) || equalsValue.equals(dnsRecord.getValue().trim()))){
        result = true;
        break;
      }
    }
    return result;
  }

  private static final int DEFAULT_PORT = 43;

  public static String query(String domain) throws Exception {
    if(StringUtils.isBlank(domain)){
      return "";
    }
    String server = "";
    String tld = StringUtil.getDomainTld(domain);
    Map<String,String> map = PropertiesResolver.WHOSIS_SERVER_PROPERTIES;
    if(MapUtils.isNotEmpty(map) && map.get(tld) != null){
      server = map.get(tld);
    }
    if(StringUtils.isNotBlank(server)){
      return query(StringUtil.getMainDomain(domain), server);
    }else{
      return "";
    }
  }

  public static String query(String domain, String server) throws Exception {
    String result = "";
    if(StringUtils.isBlank(server)){
      return result;
    }
    if(server.contains(",")){
      String servers [] =  server.split(",");
      for (String nsServer : servers) {
        String re = queryNs(domain,nsServer);
        if(StringUtils.isNotBlank(server)){
          result = re;
          break;
        }
      }
    }else{
      result = queryNs(domain,server);
    }
    return result;
  }

  public static String queryNs(String domain, String server) throws Exception{
      Socket socket = new Socket(server, DEFAULT_PORT);
      String lineSeparator = "\r\n";

      PrintWriter out = new PrintWriter(socket.getOutputStream());
      out.println(domain);
      out.flush();

      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      StringBuilder ret = new StringBuilder();
      String line;
      while ((line = in.readLine()) != null) {
        ret.append(line + lineSeparator);
      }
      socket.close();
      return ret.toString();
    }

//  private static String getTLD(String domain) {
//    final int index;
//    return (domain == null || (index = domain.lastIndexOf('.') + 1) < 1) ? domain
//        : (index < (domain.length())) ? domain.substring(index) : "";
//  }

  public static Map queryDomainInfoMap(String domain) throws Exception{
    String domainInfo = query(domain);
    Map infoMap = new LinkedHashMap();
    if(StringUtils.isNotBlank(domainInfo)){
      String lastUpdateDesc = "";
      if(domainInfo.contains(">>>")){
        lastUpdateDesc = domainInfo.substring(domainInfo.indexOf(">>>"));
        domainInfo = domainInfo.substring(0,domainInfo.indexOf(">>>"));
      }
      String infoStr [] = domainInfo.split("\r\n");
      for (String info : infoStr) {
        if(StringUtils.isNotBlank(info)){
          String key = info.substring(0,info.indexOf(":"));
          String value = info.substring(info.indexOf(":")+1);
          infoMap.put(key.trim(),value.trim());
        }
      }
      if(StringUtils.isNotBlank(lastUpdateDesc)){
        infoMap.put("Last Update Description",lastUpdateDesc);
      }
    }
    return infoMap;
  }
  public static void main(String args[]) throws Exception{
//    String statement="sadasd;\n\n  \n  dfsfsfsf;\ndsadsa;\n;";
//    String a=StringUtil.replceMultipleOnlySpace(statement);
//    String sqlStr [] = a.split(";\n");
//    System.out.println(sqlStr);
//    System.out.println(sqlStr.length);
//    List<DNSRecord> list = getDomainCommonRecords("site.chime.me");
//    try {
//      System.out.println(JacksonUtils.toJson(list));
//    }catch (Exception e){
//      e.printStackTrace();
//    }
//    getDomainARecords("www.thereneewhiteteam.com",Type.);

//    System.out.println(query("baidu.com"));
//    System.out.println(query("csdn.net"));
//    System.out.println(query("apache.org"));
//    System.out.println(query("360.cn"));          //china
//    System.out.println(query("mixi.jp"));         //japan
//    System.out.println(query("laneige.co.kr"));   //korea
//    System.out.println(query("chime.me"));   //korea
//    System.out.println(query("qq.com.cn"));   //korea
//    System.out.println(query("thereneewhiteteam.com"));   //korea
//    Map map = queryDomainInfoMap("site.chime.me");
//    System.out.println(falg);
  }
}

