package com.dbTool.util;

import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leifeifei on 17-10-12.
 */
public class StringUtil {

  /**
   * \\s表示空白、回车、换行等空白符,替换回车等为一个空格
   * @param str
   * @return
   */
  public static String replceMultipleSpace(String str){
    Pattern p = Pattern.compile("\\s+");
    Matcher m = p.matcher(str);
    return m.replaceAll(" ");
  }

  /**
   * \\s表示空白、回车、换行等空白符,替换回车替换掉
   * @param str
   * @return
   */
  public static String replceSpace(String str){
    Pattern p = Pattern.compile("\\s+");
    Matcher m = p.matcher(str);
    return m.replaceAll("");
  }

  /**
   * 只替换空格，不替换回车等空白符号
   * @param str
   * @return
   */
  public static String replceMultipleOnlySpace(String str){
    str = str.replaceAll(" +\n"," \n");
    str = str.replaceAll("\n +","\n ");
//    str = str.replaceAll(" \n","\n");
//    str = str.replaceAll("\n ","\n");
    return str.replaceAll("\n+","\n");
  }

  public static String replceSingleQuot(String str){
    return str.replaceAll("'","%+");
  }

  public static String returnResultStr(String key,String value){
    List list = new ArrayList<>();
    Map map = new HashMap();
    map.put(key,value);
    list.add(map);
    return ReturnJacksonUtil.resultOk(list);
  }

  public static String returnFailResultStr(String key,String value){
    List list = new ArrayList<>();
    Map map = new HashMap();
    map.put(key,value);
    list.add(map);
    return ReturnJacksonUtil.resultFail(list);
  }

  /**
   * 只替换制表符
   * @param str
   * @return
   */
  public static String replceMultiTab(String str){
    return str.replaceAll("\t+","\t");
  }

  public static String getMainDomain(String domain){
    String domains [] = domain.toLowerCase().split("\\.");
    String mainDomain = "";
    //输入的域名只有一个.号
    if(domains.length == 2){
      return domain;
    }else if(domains.length > 2){
      String tld = domains[domains.length-2]+"."+domains[domains.length-1];
      Map<String,String> map = PropertiesResolver.WHOSIS_SERVER_PROPERTIES;
      if(MapUtils.isNotEmpty(map) && map.get(tld) != null){
        mainDomain = domains[domains.length-3] + "." + tld;
      }else{
        mainDomain=tld;
      }
    }
    return mainDomain;
  }


  public static String getDomainTld(String domain){
    String domains [] = domain.toLowerCase().split("\\.");
    String tld = "";
    //输入的域名只有一个.号
    if(domains.length == 2){
      return domains[1];
    }else if(domains.length > 2){
      tld = domains[domains.length-2]+"."+domains[domains.length-1];
      Map<String,String> map = PropertiesResolver.WHOSIS_SERVER_PROPERTIES;
      if(MapUtils.isNotEmpty(map) && map.get(tld) != null){
        return tld;
      }else{
        tld=domains[domains.length-1];
      }
    }
    return tld;
  }

  public static String nullToEmpty(String str) {
    return str == null ? "" : str;
  }


  /**
   * \\s表示空白、回车、换行等空白符,替换回车等为一个空格
   * @param str
   * @return
   */
  public static String replceSpaceToStr(String str,String replaceStr){
    Pattern p = Pattern.compile("\\s+");
    Matcher m = p.matcher(str);
    return m.replaceAll(replaceStr);
  }


  public static Boolean checkDomain(String domain){
    String pattern = "^(?=^.{3,255}$)[a-z0-9][-a-z0-9]{0,62}(\\.[a-z0-9][-a-z0-9]{0,62})+$";
    return Pattern.matches(pattern,domain);
  }

  public static void main(String args []){
//    String a= "select *   from collection limit 1,5;\n select * from   database_user_info;";
//    System.out.println(replceMultipleSpace(a));
//    System.out.println(a.replaceAll(" +"," "));
//    System.out.println(a.trim().substring(0,a.length()-1));
//    System.out.println(replceSingleQuot("select * from   database_user_info where account='selec'"));
//    "select *  from   aaa where ddd='aa   11 '
//    System.out.println(replceMultipleOnlySpace("select *  from   aaa where ddd='aa   11 ' \nand a  = 'aaa  d  '\n   and ada"));
    System.out.println(checkDomain(""));

  }
}
