package com.homethy;

/**
 * Created by wkzhao on 17-5-31.
 */

import com.homethy.domain.DatabaseUserInfo;
import com.homethy.service.UserService;
import com.homethy.util.CookieUtil;
import com.homethy.util.DateUtil;
import com.homethy.util.HttpRequestUtil;
import com.homethy.util.UserClientTypeHelper;
import com.homethy.util.crypto.Base62Encoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Configuration
public class WebSecurityConfig extends WebMvcConfigurerAdapter {
  private static final Log LOG = LogFactory.getLog(WebSecurityConfig.class);

  public static final String USER_COOKIE = "user_cookie_name";
  private static final String VERTICAL_LINE = "|";
  public static final String VERSION = "version";
  String emtryStr = "";

  @Value("${password.update.cycle}")
  private int PASSWORD_UPDATE_CYCLE;

  /**
   * 登录session key
   */
  public static final String SESSION_KEY = "database_user";

  @Autowired
  private UserService userService;

  @Bean
  public SecurityInterceptor getSecurityInterceptor() {
    return new SecurityInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    InterceptorRegistration addInterceptor = registry.addInterceptor(getSecurityInterceptor());

    // 排除配置
    addInterceptor.excludePathPatterns("/error");
    addInterceptor.excludePathPatterns("/login**");
    addInterceptor.excludePathPatterns("/logout");
    addInterceptor.excludePathPatterns("/domainInfo/**");
    addInterceptor.excludePathPatterns("/newLeadAlert/**");
    addInterceptor.excludePathPatterns("/csrView/**");
    addInterceptor.excludePathPatterns("/page/**");
    addInterceptor.excludePathPatterns("/forget");
    addInterceptor.excludePathPatterns("/forgetPassword");
    addInterceptor.excludePathPatterns("/reset");
    addInterceptor.excludePathPatterns("/resetPassword");

    // 拦截配置
    addInterceptor.addPathPatterns("/**");
  }

  private class SecurityInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object
      handler) throws Exception {
      printAccessLog(request);
      HttpSession session = request.getSession();
      DatabaseUserInfo user = (DatabaseUserInfo)session.getAttribute(SESSION_KEY);
      if (user == null) {
        user = getUserInfoFromCookie(request);
      }

      if (null != user) {

        session.setAttribute(SESSION_KEY, user);
        //密码超过周期修改,跳转到修改密码页，使用初始密码的也跳转到修改密码页
//        if(!isUpdatePassword(request) && ((user.getLastUpdatePasswordTime()!=null && user.getUpdatePasswordinterval()>=PASSWORD_UPDATE_CYCLE) || "6a8c2125406ca9963fcd2aec1578155a".equals(user.getPassword()))){
//          // 跳转登录
//          String url = "/update_password";
//          response.sendRedirect(url);
//          return false;
//        }
        return true;
      }

      // 跳转登录
      String url = "/login";
      response.sendRedirect(url);
      return false;
    }

    private boolean isUpdatePassword(HttpServletRequest request){
      String uri = request.getRequestURI();
      if("/update_password".equals(uri) || "/update_password_submit".equals(uri)){
        return true;
      }
      return false;
    }

    private DatabaseUserInfo getUserInfoFromCookie(HttpServletRequest request) {
      String cookie = CookieUtil.get(request, USER_COOKIE);
      String versionCookie = CookieUtil.get(request, VERSION);
      if (StringUtils.isBlank(cookie) || StringUtils.isBlank(versionCookie)) {
        return null;
      }
      long userId = CookieUtil.decode(cookie);
      long version = CookieUtil.decode(versionCookie)/userId;

      return userService.getUserInfoById(userId,version);
    }

    /**
     * 记录AccessLog
     * @param request
     */
    private void printAccessLog(HttpServletRequest request) {

      List<String> paramList = new ArrayList<String>();
      String terminalType = null;
      String userAgent = request.getHeader("User-Agent");
      if (UserClientTypeHelper.isMobileDevice(userAgent)) {
        terminalType = "3g";
      } else {
        terminalType = "web";
      }

      Map<String, String> paramPair = HttpRequestUtil.getRequestParamValueMap(request);

      for (Map.Entry<String, String> entry : paramPair.entrySet()) {
        String param2value = entry.getKey() + "=" + entry.getValue();
        paramList.add(param2value);
      }

      Collections.sort(paramList);
      StringBuffer buf = new StringBuffer();
      buf.append(StringUtils.isEmpty(request.getServerName()) ? emtryStr : request.getServerName()).append(VERTICAL_LINE);
      buf.append(StringUtils.isEmpty(request.getRequestURI()) ? emtryStr : request.getRequestURI()).append(VERTICAL_LINE);
      buf.append(emtryStr + terminalType).append(VERTICAL_LINE);
      buf.append(StringUtils.isEmpty(request.getHeader("x-forwarded-for")) ? emtryStr : request.getHeader("x-forwarded-for")).append(VERTICAL_LINE);
      buf.append(StringUtils.isEmpty(request.getHeader("Referer")) ? emtryStr : request.getHeader("Referer")).append(VERTICAL_LINE);

      buf.append("paramList:");
      for (String s : paramList) {
        buf.append(StringUtils.SPACE);
        buf.append(s);
      }

      LOG.info(buf);
    }
  }
}
