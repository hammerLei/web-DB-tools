package com.dbTool.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public final class HttpApiClient {
  private static final Log LOGGER = LogFactory.getLog(HttpApiClient.class);
  private ExecutorService executorService;
  private PoolingHttpClientConnectionManager connManager;
  private int timeout;
  private int connectionNumber;
  private RequestConfig defaultRequestConfig;
  private IdleConnectionMonitorThread monitor;

  protected HttpApiClient(int connectionNumber, int timeout) {
    this.connectionNumber = connectionNumber;
    this.timeout = timeout;
    this.connManager = new PoolingHttpClientConnectionManager();
    this.connManager.setDefaultMaxPerRoute(this.connectionNumber);
    this.connManager.setMaxTotal(this.connectionNumber);
    this.executorService = Executors.newFixedThreadPool(this.connectionNumber);
    HttpHost proxy = null;
    if (!System.getProperty("api.connection.proxy.ip", "").isEmpty()) {
      proxy = new HttpHost(System.getProperty("api.connection.proxy.ip", ""), Integer.parseInt(System.getProperty("api.connection.proxy.port", "0")), "http");
    }

    this.defaultRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout).setProxy(proxy).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
    this.monitor = new IdleConnectionMonitorThread(this.connManager);
    this.monitor.start();
  }

  public CloseableHttpClient getHttpClient() {
    CloseableHttpClient client = HttpClients.custom().setConnectionManager(this.connManager).setDefaultRequestConfig(this.defaultRequestConfig).build();
    return client;
  }

  private HttpResponse executeRequest(final HttpUriRequest request, final List<Header> headers) throws Exception {
    HttpResponse response = null;
    Future future = this.executorService.submit(new Callable<HttpResponse>() {
      public HttpResponse call() throws Exception {
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(defaultRequestConfig).setDefaultHeaders(headers).disableAutomaticRetries().setKeepAliveStrategy(ApiConnectionKeepAliveStrategy.INSTANCE).build();
        CloseableHttpResponse response = client.execute(request);

        try {
          HttpEntity httpEntity = response.getEntity();
          if (null != httpEntity) {
            String resultStr = EntityUtils.toString(httpEntity);
            response.setEntity(new StringEntity(resultStr, ContentType.get(httpEntity)));
          }
        } finally {
          try {
            response.close();
          } catch (IOException var10) {
            LOGGER.error("execute http get failed:" + request.getURI(), var10);
          }

        }

        return response;
      }
    });

    try {
      if (this.timeout > 0) {
        response = (HttpResponse)future.get((long)this.timeout, TimeUnit.MILLISECONDS);
      } else {
        response = (HttpResponse)future.get();
      }

      return response;
    } catch (Exception var6) {
      request.abort();
      throw var6;
    }
  }

  public HttpResponse executeGetRequest(String url, List<NameValuePair> parameterList, List<Header> headers) throws Exception {
    URIBuilder builder = new URIBuilder();
    if (parameterList != null) {
      Iterator var5 = parameterList.iterator();

      while(var5.hasNext()) {
        NameValuePair nameValuePair = (NameValuePair)var5.next();
        builder.setParameter(nameValuePair.getName(), nameValuePair.getValue());
      }
    }

    HttpGet request = new HttpGet(url + builder.build());
    return this.executeRequest(request, headers);
  }

  public HttpResponse executeGetRequest(String url, List<NameValuePair> parameterList) throws Exception {
    return this.executeGetRequest(url, parameterList, (List)null);
  }

  public HttpResponse executeGetRequest(String url) throws Exception {
    return this.executeGetRequest(url, (List)null, (List)null);
  }

  public HttpResponse executeGetRequestWithEntity(String url, HttpEntity entity, List<Header> headers) throws Exception {
    HttpGetWithEntity request = new HttpGetWithEntity(url);
    request.setEntity(entity);
    return this.executeRequest(request, headers);
  }

  public HttpResponse executeGetRequestWithEntity(String url, HttpEntity entity) throws Exception {
    return this.executeGetRequestWithEntity(url, entity, (List)null);
  }

  public HttpResponse executePostRequest(String url, List<NameValuePair> parameterList, List<Header> headers) throws Exception {
    HttpPost request = new HttpPost(url);
    if (parameterList != null) {
      request.setEntity(new UrlEncodedFormEntity(parameterList));
    }

    return this.executeRequest(request, headers);
  }

  public HttpResponse executePostRequest(String url, List<NameValuePair> parameterList) throws Exception {
    return this.executePostRequest(url, (List)parameterList, (List)null);
  }

  public HttpResponse executePostRequest(String url, HttpEntity entity, List<Header> headers) throws Exception {
    HttpPost request = new HttpPost(url);
    request.setEntity(entity);
    return this.executeRequest(request, headers);
  }

  public HttpResponse executePostRequest(String url, HttpEntity entity) throws Exception {
    return this.executePostRequest(url, (HttpEntity)entity, (List)null);
  }

  public HttpResponse executePutRequest(String url, HttpEntity entity, List<Header> headers) throws Exception {
    HttpPut request = new HttpPut(url);
    request.setEntity(entity);
    return this.executeRequest(request, headers);
  }

  public HttpResponse executePutRequest(String url, HttpEntity entity) throws Exception {
    return this.executePutRequest(url, (HttpEntity)entity, (List)null);
  }

  public HttpResponse executePutRequest(String url, List<NameValuePair> parameterList, List<Header> headers) throws Exception {
    HttpPut request = new HttpPut(url);
    if (parameterList != null) {
      request.setEntity(new UrlEncodedFormEntity(parameterList));
    }

    return this.executeRequest(request, headers);
  }

  public HttpResponse executePutRequest(String url, List<NameValuePair> parameterList) throws Exception {
    return this.executePutRequest(url, (List)parameterList, (List)null);
  }

  public HttpResponse executeDeleteRequest(String url, List<NameValuePair> parameterList, List<Header> headers) throws Exception {
    URIBuilder builder = new URIBuilder();
    if (parameterList != null) {
      Iterator var5 = parameterList.iterator();

      while(var5.hasNext()) {
        NameValuePair nameValuePair = (NameValuePair)var5.next();
        builder.setParameter(nameValuePair.getName(), nameValuePair.getValue());
      }
    }

    HttpDelete request = new HttpDelete(url + builder.build());
    return this.executeRequest(request, headers);
  }

  public HttpResponse executeDeleteRequest(String url, List<NameValuePair> parameterList) throws Exception {
    return this.executeDeleteRequest(url, parameterList, (List)null);
  }

  public void shutdown() {
    this.connManager.shutdown();
    this.monitor.shutdown();
    this.executorService.shutdown();
  }
}

