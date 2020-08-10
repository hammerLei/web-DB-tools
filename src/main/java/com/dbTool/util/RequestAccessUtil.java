package com.dbTool.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


public class RequestAccessUtil {

  private static final Log LOG = LogFactory.getLog(RequestAccessUtil.class);

  private static final String[] MOBILE_DEVICE_FLAGS = new String[]{"android", "ipad", "iphone", "windows phone"};


  public static boolean isResponseBody(Object handler) {
    if (handler instanceof HandlerMethod) {
      HandlerMethod hm = (HandlerMethod) handler;
      return hm.getMethodAnnotation(ResponseBody.class) != null || hm.getBeanType().getAnnotation
          (ResponseBody.class) != null;
    }
    return false;
  }

  public static void markResponseBody(HttpServletRequest request, Object handler) {
    if (isResponseBody(handler)) {
      request.setAttribute("IS_RESPONSE_BODY", Boolean.TRUE);
    }
  }

  public static boolean isResponseBody(HttpServletRequest request) {
    return Boolean.TRUE.equals(request.getAttribute("IS_RESPONSE_BODY"));
  }

  public static boolean isOptions(HttpServletRequest request) {
    return "OPTIONS".equalsIgnoreCase(request.getMethod());
  }

  public static Map<String, String> getParameterPairs(HttpServletRequest request) {
    @SuppressWarnings("unchecked") Map<String, String[]> requestMap = request.getParameterMap();
    Map<String, String> result = new HashMap<>();
    requestMap.forEach((k, v) -> result.put(k, v[0]));
    handleMapPath4AlertUrl(result);
    try {
      LOG.info("search parameter pairs are " + JacksonUtils.toJson(result));
    }catch (Exception e){

    }
    return result;
  }

  private static void handleMapPath4AlertUrl(Map<String, String> result) {
    if (result.containsKey("mapPath") && !result.containsKey("layoutType")) {
      result.put("layoutType", "map");
    }
  }

  public static boolean isError(HttpServletRequest request) {
    return request.getRequestURI().startsWith("/error/");
  }


  public static boolean isMobileDevice(String userAgent) {
    return isBelongToType(userAgent, MOBILE_DEVICE_FLAGS);
  }

  public static boolean isBelongToType(String userAgent, String[] tyepFlags) {
    if (userAgent == null) {
      return false;
    } else {
      userAgent = userAgent.toLowerCase();

      for(int i = 0; i < tyepFlags.length; ++i) {
        if (userAgent.indexOf(tyepFlags[i]) > 0) {
          return true;
        }
      }

      return false;
    }
  }
}
