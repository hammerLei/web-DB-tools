package com.dbTool.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by leifeifei on 17-10-10.
 */
public class MD5Support {

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

  public MD5Support() {
  }

  public static String hex(String sourceStr, String secretKey) {
    StringBuilder sb = new StringBuilder();
    sb.append(sourceStr);
    sb.append(secretKey);
    return hex(sb.toString());
  }

  public static String hex(String[] stringArray, String secretKey) {
    Arrays.sort(stringArray);
    StringBuilder sb = new StringBuilder();
    String[] var3 = stringArray;
    int var4 = stringArray.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      String s = var3[var5];
      sb.append(s);
    }

    return hex(sb.toString(), secretKey);
  }

  public static String hex(List<String> stringList, String secretKey) {
    Collections.sort(stringList);
    StringBuilder sb = new StringBuilder();
    Iterator var3 = stringList.iterator();

    while(var3.hasNext()) {
      String s = (String)var3.next();
      sb.append(s);
    }

    return hex(sb.toString(), secretKey);
  }

  public static String hex(String sourceStr) {
    StringBuilder sb = new StringBuilder();
    sb.append(sourceStr);

    try {
      return DigestUtils.md5Hex(sb.toString().getBytes("UTF-8"));
    } catch (UnsupportedEncodingException var3) {
      var3.printStackTrace();
      return "";
    }
  }


}
