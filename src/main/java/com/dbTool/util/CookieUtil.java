package com.dbTool.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
  private static final int EXPIRED_TIME = 10 * 24 * 60 * 60;  // 10天
  private static final String DEFAULT_PATH = "/";

  public static String get(HttpServletRequest request, String key) {
    return get(request.getCookies(), key, request.getServerName(), DEFAULT_PATH);
  }

  public static void clear(HttpServletRequest request, HttpServletResponse response, String key) {
    save(request, response, key, "", 0);
  }

  public static void save(HttpServletRequest request, HttpServletResponse response, String key, String value) {
    save(request, response, key, value, EXPIRED_TIME);
  }

  /**
   * 会话级别cookie，即没有失效日期，仅关闭窗口或浏览器才失效
   * @param request
   * @param response
   * @param key
   * @param value
   */
  public static void saveSessionCookie(HttpServletRequest request, HttpServletResponse response, String key, String value) {
    response.addCookie(createSessionCookie(request.getServerName(), DEFAULT_PATH, key, value));
  }

  public static void save(HttpServletRequest request, HttpServletResponse response, String key, String value, int second) {
    response.addCookie(create(request.getServerName(), DEFAULT_PATH, key, value, second));
  }

  public static String encode(long entryId,String hex) {
    String encodeId = Base62Encoder.encode(entryId);
    String prefix = hex.substring(0, 16);
    String suffix = hex.substring(16);
    return prefix + encodeId + suffix;
  }

  public static long decode(String encodedStr) {
    if (StringUtils.isBlank(encodedStr) || encodedStr.length() <= 32) {
      return -1;
    }

    return Base62Encoder.decode(encodedStr.substring(16, encodedStr.length() - 16));
  }

  private static Cookie create(String domain, String path, String key, String value, int second) {
    Cookie cookie = new Cookie(key, value);
    cookie.setDomain(domain);
    cookie.setPath(path);
    cookie.setMaxAge(second);
    return cookie;
  }


  private static Cookie createSessionCookie(String domain, String path, String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setDomain(domain);
    cookie.setPath(path);
    return cookie;
  }

  private static String get(Cookie[] cookies, String key, String domain, String path) {
    if (null == cookies) {
      return StringUtils.EMPTY;
    }

    for (Cookie cookie : cookies) {
      if (isValidCookie(cookie, key)) {
        return cookie.getValue();
      }
    }

    return StringUtils.EMPTY;
  }

  private static boolean isValidCookie(Cookie cookie, String key) {
    return StringUtils.trimToEmpty(cookie.getName()).equalsIgnoreCase(key);
  }
}
