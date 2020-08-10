package com.dbTool.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.HashMap;
import java.util.Map;

public class Base62Encoder {
  private static final char[] ENCRYPT_DIC = "Mqs8kHbclZgfpdDmzAJLXxPo13h27eRtQCFnUIuj9rBEYWOySvNiTaK45G06Vw".toCharArray();
  private static final char[] CHECK_DIC = "D4dxSziRk83EqusecTXfmrBwtIMWJ5ZGLY1V27lH9Kjb0OhnCPAygQ6UavpNoF".toCharArray();
  private static final int SCALE = 62;
  private static final char BASE = '0';
  private static Map<Integer, Integer> alph2Index = new HashMap();

  private Base62Encoder() {
  }

  public static String encodeWithCheck(long number) {
    String encodeStr = encode(number);
    String checkStr = getCheckCode(number);
    return encodeStr + checkStr;
  }

  public static String encode(long number) {
    return encode(number, ENCRYPT_DIC);
  }

  public static String getCheckCode(long number) {
    return encode(number, CHECK_DIC);
  }

  private static String encode(long number, char[] dic) {
    StringBuilder sb = new StringBuilder();
    if (number == 0L) {
      sb.append(dic[0]);
    }

    while(number > 0L) {
      int val = (int)(number % 62L);
      sb.append(dic[val]);
      number /= 62L;
    }

    return sb.reverse().toString();
  }

  public static void main(String[] args) {
    String strUserId = encodeWithCheck(10000000514L);
    Long userId = decodeWithCheck(strUserId);
    System.out.println(strUserId + " decode : " + userId);
  }

  public static long decodeWithCheck(String str) {
    if (null != str && !"".equals(str)) {
      int len = str.length();
      if (len % 2 != 0) {
        return -1L;
      } else {
        String encodedStr = str.substring(0, len / 2);
        String checkStr = str.substring(len / 2);
        long val = decode(encodedStr);
        String recheckStr = getCheckCode(val);
        return checkStr.equals(recheckStr) ? val : -1L;
      }
    } else {
      return -1L;
    }
  }

  public static long decode(String encodedStr) {
    long val = 0L;
    if (encodedStr != null && !encodedStr.isEmpty()) {
      for(int i = 0; i < encodedStr.length(); ++i) {
        int index = (Integer)alph2Index.get(encodedStr.charAt(i) - 48);
        val = val * 62L + (long)index;
      }

      return val;
    } else {
      return val;
    }
  }

  static {
    for(int i = 0; i < ENCRYPT_DIC.length; ++i) {
      alph2Index.put(ENCRYPT_DIC[i] - 48, i);
    }

  }
}
