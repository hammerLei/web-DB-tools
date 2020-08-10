package com.dbTool.util;

import com.dbTool.domain.dns.ServerCSR;

import java.io.*;

/**
 * Created by leifeifei on 18-1-12.
 */
public class ExcShellUtil {

  public static String makeCSR(ServerCSR serverCSR) {
    StringBuilder sb = new StringBuilder();
    try {
      String cmdstring = "chmod a+x create_csr.sh";
      Process proc = Runtime.getRuntime().exec(cmdstring);
      proc.waitFor(); //阻塞，直到上述命令执行完
      cmdstring = "./create_csr.sh "+serverCSR.getCommonName()+" "+serverCSR.getCountryName()+" "+serverCSR.getProvinceName()+" "+serverCSR.getLocalityName()+" "+serverCSR.getOrganizationName()+" "+serverCSR.getOrganizadionUnitName()+" "+serverCSR.getEmail(); //这里也可以是ksh等
      proc = Runtime.getRuntime().exec(cmdstring);
      // 注意下面的操作
      String ls_1;

      BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(proc.getInputStream()));
      while ( (ls_1=bufferedReader.readLine()) != null){
        sb.append(ls_1).append("\n");
      };
      bufferedReader.close();
      proc.waitFor();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return sb.toString();

  }


  public static String buildSite(String account, String domain, String env) {
    StringBuilder sb = new StringBuilder();
    try {
      String cmdstring = "chmod a+x build_site.sh";
      Process proc = Runtime.getRuntime().exec(cmdstring);
      proc.waitFor(); //阻塞，直到上述命令执行完
      cmdstring = "sh build_site.sh "+account+","+domain+" "+env; //这里也可以是ksh等
      proc = Runtime.getRuntime().exec(cmdstring);
      // 注意下面的操作
      String ls_1;

      BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(proc.getInputStream()));
      while ( (ls_1=bufferedReader.readLine()) != null){
        sb.append(ls_1).append("\n");
      };
      bufferedReader.close();
      proc.waitFor();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return sb.toString();

  }

  public static void main(String args []){
    ServerCSR serverCSR = new ServerCSR();
    serverCSR.setCommonName("feifei.com");
    serverCSR.setCountryName("CA");
    serverCSR.setProvinceName("hubei");
    serverCSR.setLocalityName("wuhan");
    serverCSR.setOrganizationName("renren");
    serverCSR.setOrganizadionUnitName("market");
    serverCSR.setEmail("feifei@qq.com");

    makeCSR(serverCSR);

  }
}
