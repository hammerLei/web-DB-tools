package com.dbTool.util;

import com.fasterxml.jackson.databind.JsonNode;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.XML;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.Principal;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

public class SiteHttpUtil {

  private static final Log LOGGER = LogFactory.getLog(SiteHttpUtil.class);

  public static final HttpApiClient CLIENT_200 = new HttpApiClientBuilder().setTimeout(200).build();
  public static final HttpApiClient CLIENT_1S = new HttpApiClientBuilder().setTimeout(1000).build();
  public static final HttpApiClient CLIENT_2S = new HttpApiClientBuilder().setTimeout(2000).build();
  public static final HttpApiClient CLIENT_3S = new HttpApiClientBuilder().setTimeout(3000).build();
  public static final HttpApiClient CLIENT_5S = new HttpApiClientBuilder().setTimeout(5000).build();
  public static final HttpApiClient CLIENT_10S = new HttpApiClientBuilder().setTimeout(10000).build();

  public static class RequestHeader implements Header {

    private String name;
    private String value;

    public RequestHeader(String name, String value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getValue() {
      return value;
    }

    @Override
    public HeaderElement[] getElements() throws ParseException {
      return new HeaderElement[0];
    }
  }

  public static void close(AutoCloseable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception e) {
        // IGNORE
      }
    }
  }
//  public static JsonNode getResponseValueByKey(String url, Map<String, Object> params
//      , String key) {
//    return getResponseValueByKey(url,params,key,CLIENT_200);
//  }
  public static String getResponseValueByKey(String url, Map<String, Object> params, HttpApiClient client) {
    List<NameValuePair> list = new ArrayList<>();
    params.forEach((each, value) -> {
      list.add(new BasicNameValuePair(each, value.toString()));
    });

    try {
      HttpResponse response = client.executePostRequest(url, list);
      HttpEntity httpEntity = response.getEntity();
      String content = EntityUtils.toString(httpEntity);
      int code = response.getStatusLine().getStatusCode();
      LOGGER.info("ur:" + url + " code:" + code + " params:" + params + "\n response:" + content);
      if (code > 299) {
        throw new IOException("Bad request ur:" + url + " code:" + code + "\n response:" +
            content);
      }
      return content;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public static String getResponseValueByKeyWithHeaders(String url, Map<String, Object> params, HttpApiClient client,List<Header> headers) {
    List<NameValuePair> list = new ArrayList<>();
    params.forEach((each, value) -> {
      list.add(new BasicNameValuePair(each, value.toString()));
    });

    try {
      HttpResponse response = client.executePostRequest(url, list, headers);
      HttpEntity httpEntity = response.getEntity();
      String content = EntityUtils.toString(httpEntity);
      int code = response.getStatusLine().getStatusCode();
      LOGGER.info("ur:" + url + " code:" + code + " params:" + params + "\n response:" + content);
      if (code > 299) {
        throw new IOException("Bad request ur:" + url + " code:" + code + "\n response:" +
            content);
      }
      return content;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   *
   * @param url
   * @param body  json串
   * @return
   */
  public static String postBody(String url, String body) throws Exception{

    // 实例化httpClient
    CloseableHttpClient httpclient = HttpClients.createDefault();
    // 实例化post方法
    HttpPost httpPost = new HttpPost(url);

    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
        .setSocketTimeout(5000).build();
    httpPost.setConfig(requestConfig);
    // 结果
    CloseableHttpResponse response = null;
    String content = null;
    httpPost.setHeader("Content-Type" , "application/json");
    try {
      // 将参数给post方法
      if (body != null) {
        StringEntity stringEntity = new StringEntity(body , "UTF-8");
        httpPost.setEntity(stringEntity);
      }
      // 执行post方法
      response = httpclient.execute(httpPost);
      if (response.getStatusLine().getStatusCode() == 200) {
        try {
          content = EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
          response.close();
        }
      }else if(response.getStatusLine().getStatusCode() == 401 && url.contains("rest/auth/1/session")){
        content = "401";
      }
      LOGGER.info("postBody ur:" + url + " code:" + response.getStatusLine().getStatusCode() + " body:" + body + "\n response:" + content);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        httpclient.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return content;
  }

  /**
   *
   * @param url
   * @param o
   * @param key
   * @return
   * @throws IOException
   */
  public static JsonNode getResponseValueByKey(String url, Object o, String
      key) throws IOException {
    Map<String, Object> map = bean2Map(o);
    return getResponseValueByKey(url, map, key);
  }


  private static JsonNode findValueInJsonNode(JsonNode root, String key) {
    if (!root.has(key)) {
      if (root.isArray()) {
        for (int i = 0; i < root.size(); i++) {
          if (root.get(i).has(key)) {
            return root.get(i).path(key);
          } else {
            return findValueInJsonNode(root.get(i), key);
          }

        }
      } else {
        Iterator<JsonNode> elements = root.elements();
        while (elements.hasNext()) {
          JsonNode next = elements.next();
          if (next.has(key)) {
            return next.path(key);
          } else {
            findValueInJsonNode(next, key);
          }
        }
      }
    }
    return root.path(key);
  }


  private static <T> Map<String, Object> bean2Map(T t) {

    if (t instanceof Collection) {
      throw new RuntimeException(" can not format a collection ");
    }

    if (t instanceof Map) {
      return (Map<String, Object>) t;
    }

    Map<String, Object> map = new HashMap<>();
    Class<?> clazz = t.getClass();
    Field[] fields = clazz.getDeclaredFields();

    Arrays.stream(fields).forEach(
        item -> {
          item.setAccessible(true);
          try {
            map.put(item.getName(), item.get(t));
          } catch (IllegalAccessException e) {
            throw new RuntimeException(" format field failed : " + item.getName());
          }
        }
    );
    return map;
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
      LOGGER.error("SiteHttpUtil:getResult:url=" + url + ", response string is " + responseString,e);
      return null;
    }
  }


  public static String getResultString(String url) {
    GetMethod getMethod = new GetMethod(url);
    getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
        new DefaultHttpMethodRetryHandler());
    String responseString = null;
    try {
      HttpResponse response = CLIENT_5S.executeGetRequest(url);
      HttpEntity httpEntity = response.getEntity();
      responseString = EntityUtils.toString(httpEntity);


      return responseString;
    } catch (Exception e) {
      LOGGER.error("SiteHttpUtil:getResult:url=" + url + ", response string is " + responseString,e);
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
      LOGGER.error("SiteHttpUtil:getResult:url="+url,e);
      return null;
    }
  }

  public static JSONObject getResult2Json4HomeJunction(String url, List<Header> headers) {

    try {
      HttpResponse response = CLIENT_3S.executeGetRequest(url, null, headers);
      HttpEntity httpEntity = response.getEntity();
      String responseString = EntityUtils.toString(httpEntity);

      JSONObject result = JSONObject.fromObject(responseString);

      return result;
    } catch (Exception e) {
      LOGGER.error("SiteHttpUtil:home junction getResult:url="+url,e);
      return null;
    }
  }

  public static JSONObject getResult2Json4HjMs(String url) {
    try {
      return JSONObject.fromObject(getResult4HjMs(url));
    } catch (Exception e) {
      LOGGER.error("SiteHttpUtil:home junction 2 json getResult:url="+url,e);
      return null;
    }
  }

  public static String getResult4HjMs(String url) {

    try {
      HttpResponse response = CLIENT_3S.executeGetRequest(url);
      HttpEntity httpEntity = response.getEntity();
      String responseString = EntityUtils.toString(httpEntity);

      return responseString;
    } catch (Exception e) {
      LOGGER.error("SiteHttpUtil:home junction getResult:url="+url,e);
      return null;
    }
  }

  /**
   * 连接是否可用
   * domain带上Scheme前缀
   */
  public static Boolean isDomainEffect(String domain) {
    try {
      URL url = new URL(domain);
      InputStream in = url.openStream();
      return true;
    } catch (Exception e) {
      if(e.getMessage().indexOf("Server returned HTTP response code: 403")>=0){
        //cloudFlare的免费会报403
        return true;
      }
      return false;
    }
  }

  public static Map<String,String> decodeSslCrt(String crt){

    Map<String,String> result = new HashMap<>();
    try{
      if(crt.indexOf("-----BEGIN CERTIFICATE-----\n")>=0){
        crt = crt.substring(crt.indexOf("-----BEGIN CERTIFICATE-----\n") + 27);
      }

      if(crt.indexOf("-----END CERTIFICATE")>0){
        crt = crt.substring(0,crt.indexOf("-----END CERTIFICATE"));
      }

      // Base64解码
      BASE64Decoder decoder = new BASE64Decoder();
      byte[] byteCert = decoder.decodeBuffer(crt);
      //转换成二进制流
      ByteArrayInputStream bain = new ByteArrayInputStream(byteCert);
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      X509Certificate oCert = (X509Certificate)cf.generateCertificate(bain);
      Principal principal = oCert.getSubjectDN();
      String dnInfo = principal.getName();
      if(StringUtils.isNotBlank(dnInfo)){
        for (String dn : dnInfo.split(",")) {
          if(dn.indexOf("CN")>=0){
            result.put("domain",dn);
          }
          if(dn.indexOf("CN")>=0){
            result.put("department",dn);
          }
        }
      }
      Collection<List<?>> domains = oCert.getSubjectAlternativeNames();
      if(CollectionUtils.isNotEmpty(domains)){
        String bakDomains = "";
        for (List collection : domains) {
          if(bakDomains == ""){
            bakDomains = collection.get(1).toString();
          }else{
            bakDomains = bakDomains + "," + collection.get(1).toString();
          }
        }
        result.put("supportDomains",bakDomains);
      }
      oCert.getIssuerAlternativeNames();
//      oCert.

      Collection e = oCert.getIssuerAlternativeNames();
      String a = oCert.getSigAlgName();
      String b = oCert.getSigAlgOID();
      //    String c = oCert.getSigAlgParams().toString();
      //    String d = oCert.getSignature().toString();
      Collection al = oCert.getSubjectAlternativeNames();
//      String info = p.getName();
    }catch (Exception e){

    }

    return null;

  }



//
//  private static Map<String,Object> getQueryApiPara(JSONObject lead,boolean isFullLead,String agentAccount,int source){
//    Map<String,Object> map = new HashedMap();
//
//    map.put("city", StringUtil.nullToEmpty(lead.getString("city")));
//    map.put("state",StringUtil.nullToEmpty(lead.getString("state")));
//    map.put("notes",StringUtil.nullToEmpty(lead.getString("notes")));
//    map.put("source",source);
//
//    String streetLine = lead.getString("streetLine1");
//    long regDate = 0;
//    if(StringUtils.isNotBlank(lead.getString("dateCreated"))){
//      Date date = DateUtil.parse(lead.getString("dateCreated"),DateUtil.DATEFORMAT_MM_DD_YY);
//      regDate = date!=null ? date.getTime() : 0;
//    }
//    if(isFullLead){
//      map.put("agentAccount",agentAccount);
//      map.put("zipcode",StringUtil.nullToEmpty(lead.getString("zip")));
//      map.put("name",StringUtil.nullToEmpty(lead.getString("name")));
//      map.put("emails",StringUtil.nullToEmpty(lead.getString("email")));
//      map.put("phones",StringUtil.nullToEmpty(lead.getString("phone")));
//
//      map.put("regDate",regDate);
//      map.put("leadType",StringUtils.isNotBlank(lead.getString("lead_type")) ? lead.getString("lead_type").toLowerCase() : "");
//      String streetAddress =  "" + streetLine + (StringUtils.isNotBlank(lead.getString("unit")) ? ","+lead.getString("unit"):"");
//      map.put("streetAddress",streetAddress);
//      map.put("timeFrame",StringUtil.nullToEmpty(lead.getString("sellDate")));
//      map.put("leadStatus",StringUtil.nullToEmpty(lead.getString("leadStatus")));
//    }else{
//
//      map.put("account",agentAccount);
//      map.put("zipCode",StringUtil.nullToEmpty(lead.getString("zip")));
//      map.put("regTime",regDate);
//
//      String houseNumber = "";
//      String streetName = "";
//      if(StringUtils.isNotBlank(streetLine) && streetLine.trim().contains(" ")){
//        houseNumber = streetLine.trim().substring(0,streetLine.trim().indexOf(" "));
//        streetName = streetLine.trim().substring(streetLine.trim().indexOf(" ")+1);
//      }else if (StringUtils.isNotBlank(streetLine)){
//        if(NumberUtils.toLong(streetLine.trim()) == 0){
//          streetName = streetLine.trim();
//        }else{
//          houseNumber = streetLine.trim();
//        }
//      }
//
//      map.put("houseNumber",houseNumber);
//      map.put("streetName",streetName);
//      map.put("gfJson",lead.toString());
//      map.put("unit",StringUtil.nullToEmpty(lead.getString("unit")));
//
//    }
//
//    return map;
//
//  }

  public static void main(String args[]){
    Map<String, Object> params = new HashedMap();
//    params.put("clientId",12);
//    params.put("page",1);
//    params.put("type","address");
    params.put("moduleKee","md-get-more-info");
//    params.put("password","chime@1231");
//    params.put("cid",2674);
    try{

      System.out.println(JacksonUtils.toJson(params));

//    params.put("t","eGxafOmyi9x0Hi6dZmTpId95rZtCwGU5ZnPE1wITG8ymh6UAO8U7I3YTJoSz2pXCtq78H9IIyNCFxVDG5yLNX/pD2FPWVpTf6+2BxIW+I9aU6w4f8oa6iSorukptYulaUUOV2H2NxgeVoJSz6d1YOevZ4qZJiFYtcMK7fyG9Ckk=");


//    System.out.println(getResponseValueByKeyWithHeaders("http://chime.geographicfarmvalues.com/ws/ws.php/lead/admin/list",params,CLIENT_3S));
//    System.out.println(getResponseValueByKey("http://chime.geographicfarmvalues.com/ws/ws.php/config",params,CLIENT_10S));
//    System.out.println(getResponseValueByKey("http://chime.geographicfarmvalues.com/ws/ws.php/multiple-customers/generatePW",params,CLIENT_10S));
//    System.out.println(postBody("http://chime.geographicfarmvalues.com/ws/ws.php/config/init",JacksonUtils.toJson(params)));
//      List<Header> headers = new ArrayList<>();
//      headers.add(new SiteHttpUtil.RequestHeader("Content-Type", "application/json; charset=UTF-8"));
//      JSONObject lead = JSONObject.fromObject("{\"leadId\":\"398228\",\"leadCode\":null,\"address\":\"Newport Ln, Aurora, IL 60504, USA\",\"streetLine1\":\"Newport Ln\",\"city\":\"Aurora\",\"state\":\"IL\",\"zip\":\"60504\",\"unit\":\"226311\",\"name\":\"\",\"email\":\"\",\"phone\":\"\",\"sellDate\":\"None\",\"sellDateFormatted\":null,\"lat\":\"41.7485739\",\"lng\":\"-88.2367909\",\"level\":null,\"requestSchedule\":\"0\",\"dateCreated\":\"04/24/17\",\"notes\":\"\",\"valuations\":null,\"wpName\":\"\",\"wpLocation\":\"Aurora+IL\",\"wpStreet\":\"Newport+Ln\",\"caStreet\":null,\"caCity\":null,\"caProvince\":null,\"caPostalCode\":null,\"caLocation\":null,\"lookupLocation\":\"US\",\"addressFormatted\":null,\"streetNumber\":null,\"streetName\":null,\"updateType\":null,\"e_street_line_1\":null,\"street_line_1\":null,\"agentUnsubscribeMonthly\":null,\"capturedDomain\":null,\"clientId\":null,\"ck\":null,\"t\":null,\"success\":null,\"message\":null,\"leadStatus\":\"needs_follow_up\",\"lead_type\":\"Seller\"}");
//
////      String url = "https://prerelease.chime.me/api/partial/gf/update?city=Austin&state=TX&notes=&source=60&account=test2018062001@chime-mail.me&regTime=1413961200000&houseNumber=2250&streetName=Ridgepoint+Dr&unit=205&gfJson=%7B%22leadId%22%3A%2281%22%2C%22leadCode%22%3Anull%2C%22address%22%3A%222250+Ridgepoint+Drive%2C+Austin%2C+TX+78754%2C+USA%22%2C%22streetLine1%22%3A%222250+Ridgepoint+Dr%22%2C%22city%22%3A%22Austin%22%2C%22state%22%3A%22TX%22%2C%22zip%22%3A%2278754%22%2C%22unit%22%3A%22205%22%2C%22name%22%3A%22%22%2C%22email%22%3A%22%22%2C%22phone%22%3A%22%22%2C%22sellDate%22%3A%22None%22%2C%22sellDateFormatted%22%3Anull%2C%22lat%22%3A%2230.3306515%22%2C%22lng%22%3A%22-97.6762114%22%2C%22level%22%3Anull%2C%22requestSchedule%22%3A%220%22%2C%22dateCreated%22%3A%2210%2F22%2F14%22%2C%22notes%22%3A%22%22%2C%22valuations%22%3Anull%2C%22wpName%22%3A%22%22%2C%22wpLocation%22%3A%22Austin%2BTX%22%2C%22wpStreet%22%3A%222250%2BRidgepoint%2BDr%22%2C%22caStreet%22%3Anull%2C%22caCity%22%3Anull%2C%22caProvince%22%3Anull%2C%22caPostalCode%22%3Anull%2C%22caLocation%22%3Anull%2C%22lookupLocation%22%3A%22US%22%2C%22addressFormatted%22%3Anull%2C%22streetNumber%22%3Anull%2C%22streetName%22%3Anull%2C%22updateType%22%3Anull%2C%22e_street_line_1%22%3Anull%2C%22street_line_1%22%3Anull%2C%22agentUnsubscribeMonthly%22%3Anull%2C%22capturedDomain%22%3Anull%2C%22clientId%22%3Anull%2C%22ck%22%3Anull%2C%22t%22%3Anull%2C%22success%22%3Anull%2C%22message%22%3Anull%2C%22leadStatus%22%3A%22needs_follow_up%22%2C%22lead_type%22%3A%22Seller%22%7D"
//      String paurl = "https://dev.chime.me/api/lead/partial/gf/update?city=Aurora&state=IL&notes=&source=60&account=xiaoxue.wang@renren-inc.com&regTime=1493017200000&houseNumber=Newport&streetName=Ln&unit=2263";
//      Map map = new HashedMap();
//      map.put("gfJson",lead);
//      String rsyncCrm = postBody("https://prerelease.chime.me/api/lead/partial/gf/update",JacksonUtils.toJson(getQueryApiPara(lead,false,"test2018062001@chime-mail.me",60)));
//      postBody("http://192.168.20.205:8080/rest/auth/1/session",JacksonUtils.toJson(params));

//      String a = "[ {\"team_id\":111},{\"team_id\":222}]";
//      String aa = " feifei.lei@renren-inc.com";
//      System.out.println(aa.trim().substring(0,aa.trim().indexOf("@")));
//
//      JSONArray teamIdsArray = JSONArray.fromObject(a);
//      System.out.println(teamIdsArray.size() == 0);
//      teamIdsArray.forEach(jsonObj ->{
//        System.out.println(((JSONObject)jsonObj).values());});

      try {
//        String url_name="http://chime.me";
//        URI MyUri = new URI(url_name);
//        String http_or_https="";
//        http_or_https=MyUri.getScheme();
//        url_name=http_or_https+"://"+url_name;
//        System.out.println(String.format("URLNAME=%s",url_name));

//        URL url = new URL("https://www.beautifulbrookingshomes.com");
//        InputStream in = url.openStream();
//        System.out.println("连接可用");
      }
      catch (Exception e) {
        System.out.println(e.getMessage());
        if(e.getMessage().indexOf("Server returned HTTP response code: 403")>=0){
          //cloudFlare的免费会报403
          System.out.println("cloudflare");
        }else{
          System.out.println("连接打不开!");
          e.printStackTrace();
        }
      }



    }catch (Exception e){
e.printStackTrace();
    }

  }
}
