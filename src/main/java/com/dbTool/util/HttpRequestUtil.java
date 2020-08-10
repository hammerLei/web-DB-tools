package com.dbTool.util;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.XML;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Principal;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @Title: HttpRequestUtil.java
 * @Package com.dbtool.app.util
 * @Description: HttpRequest 参数获取 工具类
 */
public class HttpRequestUtil {
  private final static Log logger = LogFactory.getLog(HttpRequestUtil.class);

  public static final HttpApiClient CLIENT_1S = new HttpApiClientBuilder().setTimeout(1000).build();
  public static final HttpApiClient CLIENT_2S = new HttpApiClientBuilder().setTimeout(2000).build();
    private HttpRequestUtil(){

    }

    private static final char DEFAULT_SEPERATOR = '=';
    
    private static final String CHARSET = "UTF-8";

    public static List<String> getRequestParameterValueList(
        HttpServletRequest request) {
        return getRequestParameterValueList(request, DEFAULT_SEPERATOR);
    }

    public static List<String> getRequestParameterValueList(
        HttpServletRequest request, int maxLen) {
        return getRequestParameterValueList(request, maxLen, DEFAULT_SEPERATOR);
    }

    public static List<String> getRequestParameterValueList(
        HttpServletRequest request, int maxLen, String exclusion) {
        return getRequestParameterValueList(request, maxLen, DEFAULT_SEPERATOR,
            exclusion);
    }

    public static List<String> getRequestParameterValueList(
        HttpServletRequest request, int maxLen, Set<String> exclusions) {
        return getRequestParameterValueList(request, maxLen, DEFAULT_SEPERATOR,
            exclusions);
    }

    public static List<String> getRequestParameterValueList(
        HttpServletRequest request, char seperator) {
        Set<String> exclusions = new HashSet<String>();
        return getRequestParameterValueList(request, 0, seperator, exclusions);
    }

    public static List<String> getRequestParameterValueList(
        HttpServletRequest request, int maxLen, char seperator) {
        Set<String> exclusions = new HashSet<String>();
        return getRequestParameterValueList(request, maxLen, seperator,
            exclusions);
    }

    public static List<String> getRequestParameterValueList(
        HttpServletRequest request, int maxLen, char seperator, String exclusion) {
        Set<String> exclusions = new HashSet<String>();
        exclusions.add(exclusion);
        return getRequestParameterValueList(request, maxLen, seperator,
            exclusions);
    }

    public static List<String> getRequestParameterValueList(
        HttpServletRequest request, int maxLen, char seperator,
        Set<String> exclusions) {
        List<String> paramList = new ArrayList<String>();
        if (null == request) {
            return paramList;
        }

        @SuppressWarnings("unchecked")
        Enumeration<String> e = request.getParameterNames();
        String param = null;
        while (e.hasMoreElements()) {
            param = e.nextElement();
            if (param != null && !exclusions.contains(param)) {
                String value = request.getParameter(param);
                if (value != null) {
                    if (maxLen > 0 && value.length() > maxLen) {
                        value = value.substring(0, maxLen);
                    }
                    String param2value = param + seperator + value;
                    paramList.add(param2value);
                }
            }
        }
        return paramList;
    }

    public static Map<String, String> getRequestHeaderMap(
        HttpServletRequest request) {
        Map<String, String> param2value = new HashMap<String, String>();
        @SuppressWarnings("unchecked")
        Enumeration<String> e = request.getHeaderNames();
        String param = null;
        while (e.hasMoreElements()) {
            param = e.nextElement();
            if (param != null) {
                String value = request.getHeader(param);
                if (value != null) {
                    param2value.put(param, value);
                }
            }
        }
        return param2value;
    }

    public static Map<String, String> getRequestParamValueMap(
        HttpServletRequest request) {
        Map<String, String> param2value = new HashMap<String, String>();
        @SuppressWarnings("unchecked")
        Enumeration<String> e = request.getParameterNames();
        String param = null;
        while (e.hasMoreElements()) {
            param = e.nextElement();
            if (param != null) {
                String value = request.getParameter(param);
                if (value != null) {
                    param2value.put(param, value);
                }
            }
        }
        return param2value;
    }
    
    /**
     * UnsupportedEncodingException一定不会出现在线上环境，故借此方法隐去异常，简化代码
     * @param src
     * @return UTF-8编码的URLEncoded字符串
     */
    public static String getURLEncodedString(String src) {
        return getURLEncodedString(src, CHARSET);
    }
    /**
     * UnsupportedEncodingException一定不会出现在线上环境，故借此方法隐去异常，简化代码
     * @param src
     * @param charset
     * @return
     */
    public static String getURLEncodedString(String src, String charset) {
        String result = "";
        try {
            result = URLEncoder.encode(src, charset);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * UnsupportedEncodingException一定不会出现在线上环境，故借此方法隐去异常，简化代码
     * @param encodedString
     * @return UTF-8编码URLDecoded字符串
     */
    public static String getURLDecodedString(String encodedString) {
        return getURLDecodedString(encodedString, CHARSET);
    }
    
    /**
     * UnsupportedEncodingException一定不会出现在线上环境，故借此方法隐去异常
     * @param encodedString
     * @param charset
     * @return
     */
    public static String getURLDecodedString(String encodedString, String charset) {
        String result = "";
        try {
            result = URLDecoder.decode(encodedString, charset);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

  public static String getResultByPost(String url,NameValuePair[] param) {
    HttpClient httpClient = new HttpClient();
    PostMethod postMethod = new PostMethod(url);
    postMethod.addParameters(param);
    postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
        new DefaultHttpMethodRetryHandler());
//    postMethod.getParams().setVirtualHost("34.231.209.21");
//    httpClient.getHostConfiguration().setProxy("34.231.209.21",8080);
//    httpClient.getHostConfiguration().setHost("34.231.209.21");
    String responseString = null;
    try {
      int code = httpClient.executeMethod(postMethod);
      responseString = new String(postMethod.getResponseBody(), "UTF-8");
      logger.debug(String.format("[HttpRequestUtil.getResultByPost] url:%s, code:%s, rep:%s", url,
          code, responseString));
      return responseString;
    } catch (Exception e) {
      logger.error(String.format("[HttpRequestUtil.getResultByPost] error, url:%s", url), e);
      return null;
    }
  }


  public static JSONObject getResult(String url) {
    GetMethod getMethod = new GetMethod(url);
    getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
        new DefaultHttpMethodRetryHandler());
    String responseString = null;
    try {
      HttpResponse response = CLIENT_2S.executeGetRequest(url);
      HttpEntity httpEntity = response.getEntity();
      responseString = EntityUtils.toString(httpEntity);

      JSONObject result = JSONObject.fromObject(responseString);

      return result;
    } catch (Exception e) {
      logger.error("SiteHttpUtil:getResult:url=" + url + ", response string is " + responseString,e);
      return null;
    }
  }

  public static JSONObject getResultXmlToJson(String url) {
    GetMethod getMethod = new GetMethod(url);
    getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
        new DefaultHttpMethodRetryHandler());
    String responseString = null;
    try {
      HttpResponse response = CLIENT_2S.executeGetRequest(url);
      HttpEntity httpEntity = response.getEntity();
      responseString = EntityUtils.toString(httpEntity);

      JSONObject result = JSONObject.fromObject(XML.toJSONObject(responseString).toString());

      return result;
    } catch (Exception e) {
      logger.error("SiteHttpUtil:getResult:url="+url,e);
      return null;
    }
  }


  public static void main(String args[]) throws Exception{

    String scert = "MIIFODCCBCCgAwIBAgIIFXEYHW2KqOYwDQYJKoZIhvcNAQELBQAwgbQxCzAJBgNV\n" +
        "BAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQHEwpTY290dHNkYWxlMRow\n" +
        "GAYDVQQKExFHb0RhZGR5LmNvbSwgSW5jLjEtMCsGA1UECxMkaHR0cDovL2NlcnRz\n" +
        "LmdvZGFkZHkuY29tL3JlcG9zaXRvcnkvMTMwMQYDVQQDEypHbyBEYWRkeSBTZWN1\n" +
        "cmUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5IC0gRzIwHhcNMTgwMjI3MDAxMzAxWhcN\n" +
        "MjAwMjI3MDAxMzAxWjA9MSEwHwYDVQQLExhEb21haW4gQ29udHJvbCBWYWxpZGF0\n" +
        "ZWQxGDAWBgNVBAMTD2FzaGxleWRpcmtzLmNvbTCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
        "ggEPADCCAQoCggEBAMVPMtbYfEzks4+FCKE2PSLhovjLuhLHowAO8qX2XFkXd00b\n" +
        "BS4RSjSo7dPJ7DtTsZ/XWGogWY0owCwcC0+St2fo2MJNYehkckKODQjJtJreyxxH\n" +
        "APxvJHLs/aRy7SFfz/1qFq53Etha1/o7iuOeGHRI4ZleL/n7UUJHs8fkrM86DB+m\n" +
        "7Fw0xP1Emaxisch7nazhnvq4ufqXne8OnOxsSTN5kDt9h9rUtKhcAuPNgyiprA4w\n" +
        "ECaE8k4k4cWsue/xZ1AUXzWlge3rVvntbG06bjCPIT2X6ncud2aOf6OTJ2vjnzot\n" +
        "dA8CwcW9CvcUNE7J3ryFzENC00w/WY23kEZbvasCAwEAAaOCAcIwggG+MAwGA1Ud\n" +
        "EwEB/wQCMAAwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA4GA1UdDwEB\n" +
        "/wQEAwIFoDA3BgNVHR8EMDAuMCygKqAohiZodHRwOi8vY3JsLmdvZGFkZHkuY29t\n" +
        "L2dkaWcyczEtODExLmNybDBdBgNVHSAEVjBUMEgGC2CGSAGG/W0BBxcBMDkwNwYI\n" +
        "KwYBBQUHAgEWK2h0dHA6Ly9jZXJ0aWZpY2F0ZXMuZ29kYWRkeS5jb20vcmVwb3Np\n" +
        "dG9yeS8wCAYGZ4EMAQIBMHYGCCsGAQUFBwEBBGowaDAkBggrBgEFBQcwAYYYaHR0\n" +
        "cDovL29jc3AuZ29kYWRkeS5jb20vMEAGCCsGAQUFBzAChjRodHRwOi8vY2VydGlm\n" +
        "aWNhdGVzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkvZ2RpZzIuY3J0MB8GA1UdIwQY\n" +
        "MBaAFEDCvSeOzDSDMKIz1/tss/C0LIDOMC8GA1UdEQQoMCaCD2FzaGxleWRpcmtz\n" +
        "LmNvbYITd3d3LmFzaGxleWRpcmtzLmNvbTAdBgNVHQ4EFgQU/AtVvgu9b8Ki/fM9\n" +
        "VzuU1tZoaoUwDQYJKoZIhvcNAQELBQADggEBAB9oZwzetnrXIW5qKTpgIF7VjToW\n" +
        "UnxVkOKlghmGBF9Hy0EIJ+fl2NTsdDicfFG+yNvH8tIg6UoQm1lq+BwK5STYuNLm\n" +
        "L/N4JhvVKBqsjWTTdYwFILwVVu2Hhu5SmSchcAk6WASVPg4VG93gWKaWO8rySEAX\n" +
        "VTf5y83aW+uMpshTwVj4qzYRMqHL/rHo/5Pf/ufuTh2tFVYs8wdjqWdzTa8ehVsT\n" +
        "ObM+8SPGuzmjFBPaah100E6r6GeS//6m7so05g+B4M/xG7nt5RM/RB+JgGvOw4Gv\n" +
        "Dp9EAQPolLUFciJaWLaf8EvqVFCiJS0XcRcZnT4ih61HMTa59SPgZj2OYEA=";
// Base64解码
    BASE64Decoder decoder = new BASE64Decoder();
    byte[] byteCert = decoder.decodeBuffer(scert);
//转换成二进制流
    ByteArrayInputStream bain = new ByteArrayInputStream(byteCert);
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    X509Certificate oCert = (X509Certificate)cf.generateCertificate(bain);
    Principal p = oCert.getSubjectDN();
    Collection e = oCert.getIssuerAlternativeNames();
    String a = oCert.getSigAlgName();
    String b = oCert.getSigAlgOID();
//    String c = oCert.getSigAlgParams().toString();
//    String d = oCert.getSignature().toString();
    Collection al = oCert.getSubjectAlternativeNames();
    String info = p.getName();
    Principal aaa = oCert.getIssuerDN();

    NameValuePair[] param = new NameValuePair[3];
    param[0] = new NameValuePair("clientId", "2674");//2409 mymn-homevalue.com
    param[1] = new NameValuePair("page", "1");
    param[2] = new NameValuePair("type", "lead");
//    String aa = httpsRequest("https://www.venturehomerealestate.com/search/centerPoint?key=Lawrenceville%2C+PA&keywordType=city","GET",null);
//    System.out.println(aa);
  }
}
