package com.dbTool.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by leifeifei on 17-10-12.
 */
public class ApiUtil {

  /**
   * 获取客户端IP地址,用户的IP
   * @param request
   * @return
   * @throws Exception
   */
  public static String getClientIP(HttpServletRequest request) {
    if (request == null) {
      return "";
    }
    String ip = request.getHeader("x-forwarded-for");
    if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    // 多个路由时，取第一个非unknown的ip
    final String[] arr = ip.split(",");
    for (final String str : arr) {
      if (!"unknown".equalsIgnoreCase(str)) {
        ip = str;
        break;
      }
    }

    return ip;
  }

  /**
   * 获取本地IP地址，当前服务器的IP
   * @return
   */
  public static String getLocalIP(){
    String localIP="";
    try {

      InetAddress inetAddr = InetAddress.getLocalHost();

      byte[] addr = inetAddr.getAddress();

      // Convert to dot representation
      for (int i = 0; i < addr.length; i++) {
        if (i > 0) {
          localIP += ".";
        }
        localIP += addr[i] & 0xFF;
      }

//      String hostname = inetAddr.getHostName();
    }
    catch (UnknownHostException e) {
      System.out.println("Host not found: " + e.getMessage());
    }

    return localIP;
  }

  public static void main(String[] args) {

    try {

      InetAddress inetAddr = InetAddress.getLocalHost();

      byte[] addr = inetAddr.getAddress();

      // Convert to dot representation
      String ipAddr = "";
      for (int i = 0; i < addr.length; i++) {
        if (i > 0) {
          ipAddr += ".";
        }
        ipAddr += addr[i] & 0xFF;
      }

      String hostname = inetAddr.getHostName();

      System.out.println("IP Address: " + ipAddr);
      System.out.println("Hostname: " + hostname);

    }
    catch (UnknownHostException e) {
      System.out.println("Host not found: " + e.getMessage());
    }

  }
}
